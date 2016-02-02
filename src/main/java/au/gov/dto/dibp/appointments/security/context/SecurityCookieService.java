package au.gov.dto.dibp.appointments.security.context;

import com.oakfusion.security.AESCodec;
import com.oakfusion.security.Codec;
import org.apache.commons.codec.binary.Base64;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * Adapted from oakfusion/spring-cookie-session under the MIT license:
 * https://github.com/oakfusion/spring-cookie-session/blob/spring-cookie-session-1.0/src/main/java/com/oakfusion/security/SecurityCookieService.java
 */
@Component
public class SecurityCookieService {
    private static final String DEFAULT_COOKIE_PATH = "/";

    private final String cookieName;
    private final String cookiePath;
    private final Codec codec;
    private final AuthenticationSerializer authenticationSerializer;

    public SecurityCookieService(String cookieName, String key, AuthenticationSerializer authenticationSerializer) {
        this(cookieName, key, DEFAULT_COOKIE_PATH, authenticationSerializer);
    }

    private SecurityCookieService(String cookieName, String key, String cookiePath, AuthenticationSerializer authenticationSerializer) {
        this.cookieName = cookieName;
        this.cookiePath = cookiePath;
        this.codec = new AESCodec(key);
        this.authenticationSerializer = authenticationSerializer;
    }

    public Authentication getAuthenticationFrom(Cookie cookie) {
        byte[] decodedFromBase64 = Base64.decodeBase64(cookie.getValue());
        byte[] decryptedAuthentication = codec.decrypt(decodedFromBase64);

        return authenticationSerializer.deserializeFrom(decryptedAuthentication);
    }

    public Cookie createSecurityCookie(Authentication auth) {
        byte[] serializedAuthentication = authenticationSerializer.serializeToByteArray(auth);
        byte[] encryptedAuthentication = codec.encrypt(serializedAuthentication);
        String encodedWithBase64 = Base64.encodeBase64URLSafeString(encryptedAuthentication);

        Cookie c = new Cookie(cookieName, encodedWithBase64);
        c.setPath(cookiePath);

        return c;
    }

    public Cookie getSecurityCookieFrom(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if(cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName)) {
                    return cookie;
                }
            }
        }

        return null;
    }

    public Cookie createLogoutCookie() {
        Cookie c = new Cookie(cookieName, "");
        c.setPath(cookiePath);
        c.setMaxAge(0);
        return c;
    }

    public boolean containsSecurityCookie(HttpServletRequest request) {
        return getSecurityCookieFrom(request) != null;
    }
}
