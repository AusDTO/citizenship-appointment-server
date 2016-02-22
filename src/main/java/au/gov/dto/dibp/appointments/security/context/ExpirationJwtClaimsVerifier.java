package au.gov.dto.dibp.appointments.security.context;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;

@Component
public class ExpirationJwtClaimsVerifier {

    private static final Logger LOG = LoggerFactory.getLogger(ExpirationJwtClaimsVerifier.class);

    private static final int MAX_CLOCK_SKEW_SECONDS = 60;

    public boolean verify(JWTClaimsSet claimsSet) {
        Date now = Date.from(ZonedDateTime.now(ZoneOffset.UTC).toInstant());
        Date exp = claimsSet.getExpirationTime();

        if(exp == null) {
            LOG.error("Missing expiration date on JWT");
            return false;
        }

        return DateUtils.isAfter(exp, now, MAX_CLOCK_SKEW_SECONDS);
    }
}
