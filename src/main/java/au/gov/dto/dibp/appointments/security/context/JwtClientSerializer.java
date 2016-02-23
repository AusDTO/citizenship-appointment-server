package au.gov.dto.dibp.appointments.security.context;

import au.gov.dto.dibp.appointments.client.Client;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtClientSerializer {

    private static final int SESSION_TIME_IN_MINUTES = 30;
    private final byte[] sessionJwtEncryptionKey;
    private final ObjectMapper objectMapper;
    private final ExpirationJwtClaimsVerifier expirationJwtClaimsVerifier;

    @Autowired
    public JwtClientSerializer(@Value("${session.jwt.encryption.key.base64}") String sessionJwtEncryptionKeyBase64, ObjectMapper objectMapper, ExpirationJwtClaimsVerifier expirationJwtClaimsVerifier) {
        this.sessionJwtEncryptionKey = Base64.getDecoder().decode(sessionJwtEncryptionKeyBase64);
        this.objectMapper = objectMapper;
        this.expirationJwtClaimsVerifier = expirationJwtClaimsVerifier;
    }

    public String serialize(Client client) {
        try {
            Date date = Date.from(ZonedDateTime.now(ZoneOffset.UTC).plusMinutes(SESSION_TIME_IN_MINUTES).toInstant());
            JWSSigner signer = new MACSigner(sessionJwtEncryptionKey);

            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder().subject(objectMapper.writeValueAsString(client)).expirationTime(date).build();

            SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);

            signedJWT.sign(signer);

            JWEObject jweObject = new JWEObject(
                    new JWEHeader.Builder(JWEAlgorithm.DIR, EncryptionMethod.A256GCM)
                            .contentType("JWT") // required to signal nested JWT
                            .build(),
                    new Payload(signedJWT));

            DirectEncrypter encrypter = new DirectEncrypter(sessionJwtEncryptionKey);
            jweObject.encrypt(encrypter);

            String jweString = jweObject.serialize();

            return jweString;
        } catch (JsonProcessingException | JOSEException e) {
            throw new RuntimeException("Could not serialize Client to JWT", e);
        }
    }

    public Client deserialize(String jweString) {
        try {
            JWEObject jweObject = JWEObject.parse(jweString);
            jweObject.decrypt(new DirectDecrypter(sessionJwtEncryptionKey));
            SignedJWT signedJWT = jweObject.getPayload().toSignedJWT();

            if(!signedJWT.verify(new MACVerifier(sessionJwtEncryptionKey))) {
                return null;
            }
            if(!expirationJwtClaimsVerifier.verify(signedJWT.getJWTClaimsSet())) {
                return null;
            }
            return objectMapper.readValue(signedJWT.getJWTClaimsSet().getSubject(), Client.class);
        } catch (ParseException | JOSEException | IOException e) {
            throw new RuntimeException("Could not deserialize JWT to Client", e);
        }
    }
}
