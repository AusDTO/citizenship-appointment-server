package au.gov.dto.dibp.appointments.security.context;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.http.Cookie;

import java.util.Base64;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CookieBasedSecurityContextRepositoryTest {

    @Test
    public void containsContextReturnsTrueIfSessionCookieExists() throws Exception {
        CookieBasedSecurityContextRepository cookieBasedSecurityContextRepository = new CookieBasedSecurityContextRepository(new SecurityCookieService(new JwtClientSerializer(Base64.getEncoder().encodeToString(new byte[32]), new ObjectMapper(), new ExpirationJwtClaimsVerifier())), null);
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setCookies(new Cookie(SecurityCookieService.COOKIE_NAME,""));
        assertTrue(cookieBasedSecurityContextRepository.containsContext(mockHttpServletRequest));
    }

    @Test
    public void containsContextReturnsFalseIfSessionCookieDoesNotExist() throws Exception {
        CookieBasedSecurityContextRepository cookieBasedSecurityContextRepository = new CookieBasedSecurityContextRepository(new SecurityCookieService(new JwtClientSerializer(Base64.getEncoder().encodeToString(new byte[32]), new ObjectMapper(), new ExpirationJwtClaimsVerifier())), null);
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        assertFalse(cookieBasedSecurityContextRepository.containsContext(mockHttpServletRequest));
    }


}
