package au.gov.dto.dibp.appointments.security.context;

import au.gov.dto.dibp.appointments.client.Client;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.junit.Test;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class JoseSpikeTest {
    @Test
    public void shouldCreateJwtFromClientObject() throws Exception {
        byte[] key = "12345678901234567890123456789012".getBytes(StandardCharsets.UTF_8);
        Date date = Date.from(ZonedDateTime.now(ZoneOffset.UTC).minusMinutes(30).toInstant());
        Client client = new Client("clientId", "familyName", "customerId", true, true, "unitId", "serviceId", "appointmentTypeId", true);
        ObjectMapper objectMapper = new ObjectMapper();

        JWSSigner signer = new MACSigner(key);

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder().subject(objectMapper.writeValueAsString(client)).expirationTime(date).build();

        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);

        signedJWT.sign(signer);

        JWEObject jweObject = new JWEObject(
                new JWEHeader.Builder(JWEAlgorithm.DIR, EncryptionMethod.A256GCM)
                        .contentType("JWT") // required to signal nested JWT
                        .build(),
                new Payload(signedJWT));

        DirectEncrypter encrypter = new DirectEncrypter(new SecretKeySpec(key, "AES").getEncoded());
        jweObject.encrypt(encrypter);
        String jweString = jweObject.serialize();

        JWEObject jweObject1 = JWEObject.parse(jweString);
        jweObject1.decrypt(new DirectDecrypter(new SecretKeySpec(key, "AES").getEncoded()));
        SignedJWT signedJWT1 = jweObject1.getPayload().toSignedJWT();

        assertTrue(signedJWT1.verify(new MACVerifier(key)));
        Client client1 = objectMapper.readValue(signedJWT1.getJWTClaimsSet().getSubject(), Client.class);
        assertThat(client1.getClientId(), equalTo(client.getClientId()));
    }
}
