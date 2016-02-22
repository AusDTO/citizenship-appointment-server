package au.gov.dto.dibp.appointments.security.context;

import au.gov.dto.dibp.appointments.client.Client;
import com.oakfusion.security.AESCodec;
import com.oakfusion.security.Codec;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;

/**
 * Adapted from oakfusion/spring-cookie-session under the MIT license:
 * https://github.com/oakfusion/spring-cookie-session/blob/spring-cookie-session-1.0/src/main/java/com/oakfusion/security/SecurityCookieService.java
 */
@Component
public class SecurityCookieService {

    private static final String COOKIE_NAME = "session";
    private static final String COOKIE_PATH = "/";
    private final Codec codec;
    private final AuthenticationSerializer authenticationSerializer;
    private final JwtClientSerializer jwtClientSerializer;

    @Autowired
    public SecurityCookieService(@Value("${session.encryption.key}") String sessionEncryptionKey, AuthenticationSerializer authenticationSerializer, JwtClientSerializer jwtClientSerializer) {
        this.codec = new AESCodec(sessionEncryptionKey);
        this.authenticationSerializer = authenticationSerializer;
        this.jwtClientSerializer = jwtClientSerializer;
    }

    public Authentication getAuthenticationFrom(Cookie cookie) {
        byte[] decodedFromBase64 = Base64.decodeBase64(cookie.getValue());
        try {
            byte[] decryptedAuthentication = codec.decrypt(decodedFromBase64);
            return authenticationSerializer.deserializeFrom(decryptedAuthentication);
        } catch (Exception e) {
            Client client = jwtClientSerializer.deserialize(cookie.getValue());
            return client == null ? null : new UsernamePasswordAuthenticationToken(client, null, Collections.emptyList());
        }
    }

    public Cookie createSecurityCookie(Client client) {
        String payload = jwtClientSerializer.serialize(client);

        Cookie cookie = new Cookie(COOKIE_NAME, payload);
        cookie.setPath(COOKIE_PATH);

        return cookie;
    }

    public Cookie getSecurityCookieFrom(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if(cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(COOKIE_NAME)) {
                    return cookie;
                }
            }
        }

        return null;
    }

    public Cookie createLogoutCookie() {
        Cookie cookie = new Cookie(COOKIE_NAME, "");
        cookie.setPath(COOKIE_PATH);
        cookie.setMaxAge(0);
        return cookie;
    }

    public boolean containsSecurityCookie(HttpServletRequest request) {
        return getSecurityCookieFrom(request) != null;
    }
}
