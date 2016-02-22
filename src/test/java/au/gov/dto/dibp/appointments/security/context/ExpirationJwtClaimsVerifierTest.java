package au.gov.dto.dibp.appointments.security.context;

import com.nimbusds.jwt.JWTClaimsSet;
import org.junit.Test;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ExpirationJwtClaimsVerifierTest {
    @Test
    public void shouldReturnFalseIfExpiryDateIsNotSet() throws Exception {
        ExpirationJwtClaimsVerifier expirationJwtClaimsVerifier = new ExpirationJwtClaimsVerifier();
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder().build();
        assertFalse(expirationJwtClaimsVerifier.verify(jwtClaimsSet));
    }

    @Test
    public void shouldReturnTrueForFutureExpiryDate() throws Exception {
        ExpirationJwtClaimsVerifier expirationJwtClaimsVerifier = new ExpirationJwtClaimsVerifier();
        Date date = Date.from(ZonedDateTime.now(ZoneOffset.UTC).plusMinutes(30).toInstant());

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder().expirationTime(date).build();
        assertTrue(expirationJwtClaimsVerifier.verify(jwtClaimsSet));
    }

    @Test
    public void shouldReturnFalseForPastExpiryDate() throws Exception {
        ExpirationJwtClaimsVerifier expirationJwtClaimsVerifier = new ExpirationJwtClaimsVerifier();
        Date date = Date.from(ZonedDateTime.now(ZoneOffset.UTC).minusMinutes(30).toInstant());

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder().expirationTime(date).build();
        assertFalse(expirationJwtClaimsVerifier.verify(jwtClaimsSet));
    }

    @Test
    public void shouldReturnFalseForExpiryDateBefore01Jan1970() throws Exception {
        ExpirationJwtClaimsVerifier expirationJwtClaimsVerifier = new ExpirationJwtClaimsVerifier();
        Date date = Date.from(Instant.EPOCH.minusSeconds(60*60*24));
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder().expirationTime(date).build();
        assertFalse(expirationJwtClaimsVerifier.verify(jwtClaimsSet));
    }
}
