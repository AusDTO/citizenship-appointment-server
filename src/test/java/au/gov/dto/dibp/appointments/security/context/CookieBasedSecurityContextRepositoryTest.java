package au.gov.dto.dibp.appointments.security.context;

import au.gov.dto.dibp.appointments.client.Client;
import au.gov.dto.dibp.appointments.security.csrf.CookieBasedCsrfTokenRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpRequestResponseHolder;

import javax.servlet.ServletResponseWrapper;
import javax.servlet.http.Cookie;
import java.util.Base64;
import java.util.Collections;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class CookieBasedSecurityContextRepositoryTest {

    @Test
    public void containsContextReturnsTrueIfSessionCookieExists() throws Exception {
        CookieBasedSecurityContextRepository repository = createCookieBasedSecurityContextRepository();
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setCookies(new Cookie(SecurityCookieService.COOKIE_NAME,""));

        assertTrue(repository.containsContext(mockHttpServletRequest));
    }

    @Test
    public void containsContextReturnsFalseIfSessionCookieDoesNotExist() throws Exception {
        CookieBasedSecurityContextRepository repository = createCookieBasedSecurityContextRepository();
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();

        assertFalse(repository.containsContext(mockHttpServletRequest));
    }

    @Test
    public void returnsEmptySecurityContextForUnauthenticatedRequest() throws Exception {
        CookieBasedSecurityContextRepository repository = createCookieBasedSecurityContextRepository();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        SecurityContext securityContext = repository.loadContext(new HttpRequestResponseHolder(request, response));

        assertThat(securityContext.getAuthentication(), nullValue());
    }

    @Test
    public void returnsSecurityContextWithAuthenticationForAuthenticatedRequest() throws Exception {
        CookieBasedSecurityContextRepository repository = createCookieBasedSecurityContextRepository();
        JwtClientSerializer jwtClientSerializer = createJwtClientSerializer();
        Client client = new Client("clientId", "familyName", "customerId", true, true, "unitId", "serviceId", "appointmentTypeId", true);
        String payload = jwtClientSerializer.serialize(client);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie(SecurityCookieService.COOKIE_NAME,payload));
        MockHttpServletResponse response = new MockHttpServletResponse();

        SecurityContext securityContext = repository.loadContext(new HttpRequestResponseHolder(request, response));

        assertThat(securityContext.getAuthentication(), notNullValue());
        Client authenticatedClient = (Client) securityContext.getAuthentication().getPrincipal();
        assertThat(authenticatedClient.getClientId(), equalTo(client.getClientId()));
    }

    @Test
    public void returnsEmptySecurityContextForExpiredAuthToken() throws Exception {
        CookieBasedSecurityContextRepository repository = createCookieBasedSecurityContextRepository();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie(SecurityCookieService.COOKIE_NAME, "eyJjdHkiOiJKV1QiLCJlbmMiOiJBMjU2R0NNIiwiYWxnIjoiZGlyIn0..v3OyQykgTQI5U7gP.dKsmMKX1MHGoMx2rXrCCWOCbyax-J8JS6gu63OBXEDm7Ab926OwlwlZcvoOZGW5nO7ZR95h2pe8pQs8s8cqWJUO4L4dGI9jTj4jK_Lsy9cPWDY4BMzs2bVBuasn88OQYjC-3zuZyvPKfQHrSVS9OjTaMLeMBwMfKP-k3IysOUfUtWUNcRb86v7VCnOd0ATljXUN8DekK8iZ0wD5AtBJVaOQLbaNWiXGY2pnA2eOW9cI_vPbCqqn4ZW-r7sEy6UzHgXYgRAr4bKb7abVtRvO1Xg3CcpquE597Om0bKJIk-VVCz7fVzpz5rkp16vzN-RKBJBs2MK-UsXKD9Lkgedh5w--Q4muiWrAqA5_Tx36mvkESlzR5pbsKu84ZweE5dfen47q_BWaZguVb8jFJB1pofpEgNiZ1C1K8aKIO03CIR-cOOfvoPrsdte-0M4F5bq4KwLna8fYm9D3OeJN3sai3Ba2KKPtLsfz-F5jJlCOV44JE-F9Pqa1xfdpD_S5UenWFi9IUsM912BoCTX4ouEMP6ZUVHwKgTeFjInJXe6iJVqvhPfrWUeVUBmBURy_8XGrzW12GqN_Qp_-275gQ_jlQfyMsdtkLdMp9YxpIbPb4Whq0ey5eKvy924Z4aWKQcw6SrVPAhFjXbvtwGVJYv2lzQ2vQIDE9g1dxqPpRvAG_qb_4M3Xfhtjo2W1Md-U1Oo5cfDsrbqeeegeYDH_AA5t5tJxLDB7TtR8xtjFb52WNItxcKeMnb6jegAwWlEjAkAqY.1d7Z0BNKOegXeUI_fY8yQg"));
        MockHttpServletResponse response = new MockHttpServletResponse();
        HttpRequestResponseHolder requestResponseHolder = new HttpRequestResponseHolder(request, response);

        SecurityContext securityContext = repository.loadContext(requestResponseHolder);

        assertThat(securityContext.getAuthentication(), nullValue());
    }

    @Test
    public void expireSessionCookieForExpiredAuthToken() throws Exception {
        CookieBasedSecurityContextRepository repository = createCookieBasedSecurityContextRepository();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setSecure(true);
        request.setCookies(new Cookie(SecurityCookieService.COOKIE_NAME, "eyJjdHkiOiJKV1QiLCJlbmMiOiJBMjU2R0NNIiwiYWxnIjoiZGlyIn0..v3OyQykgTQI5U7gP.dKsmMKX1MHGoMx2rXrCCWOCbyax-J8JS6gu63OBXEDm7Ab926OwlwlZcvoOZGW5nO7ZR95h2pe8pQs8s8cqWJUO4L4dGI9jTj4jK_Lsy9cPWDY4BMzs2bVBuasn88OQYjC-3zuZyvPKfQHrSVS9OjTaMLeMBwMfKP-k3IysOUfUtWUNcRb86v7VCnOd0ATljXUN8DekK8iZ0wD5AtBJVaOQLbaNWiXGY2pnA2eOW9cI_vPbCqqn4ZW-r7sEy6UzHgXYgRAr4bKb7abVtRvO1Xg3CcpquE597Om0bKJIk-VVCz7fVzpz5rkp16vzN-RKBJBs2MK-UsXKD9Lkgedh5w--Q4muiWrAqA5_Tx36mvkESlzR5pbsKu84ZweE5dfen47q_BWaZguVb8jFJB1pofpEgNiZ1C1K8aKIO03CIR-cOOfvoPrsdte-0M4F5bq4KwLna8fYm9D3OeJN3sai3Ba2KKPtLsfz-F5jJlCOV44JE-F9Pqa1xfdpD_S5UenWFi9IUsM912BoCTX4ouEMP6ZUVHwKgTeFjInJXe6iJVqvhPfrWUeVUBmBURy_8XGrzW12GqN_Qp_-275gQ_jlQfyMsdtkLdMp9YxpIbPb4Whq0ey5eKvy924Z4aWKQcw6SrVPAhFjXbvtwGVJYv2lzQ2vQIDE9g1dxqPpRvAG_qb_4M3Xfhtjo2W1Md-U1Oo5cfDsrbqeeegeYDH_AA5t5tJxLDB7TtR8xtjFb52WNItxcKeMnb6jegAwWlEjAkAqY.1d7Z0BNKOegXeUI_fY8yQg"));
        MockHttpServletResponse response = new MockHttpServletResponse();
        HttpRequestResponseHolder requestResponseHolder = new HttpRequestResponseHolder(request, response);

        repository.loadContext(requestResponseHolder);

        ServletResponseWrapper responseWrapper = (ServletResponseWrapper) requestResponseHolder.getResponse();
        MockHttpServletResponse wrappedResponse = (MockHttpServletResponse) responseWrapper.getResponse();
        Cookie sessionCookie = wrappedResponse.getCookie(SecurityCookieService.COOKIE_NAME);
        assertThat(sessionCookie.getMaxAge(), equalTo(0));
        assertThat(sessionCookie.getValue(), isEmptyString());
        assertTrue(sessionCookie.getSecure());
        assertTrue(sessionCookie.isHttpOnly());
    }

    @Test
    public void addSessionCookieOnResponseForNonEmptySecurityContext() throws Exception {
        CookieBasedSecurityContextRepository repository = createCookieBasedSecurityContextRepository();
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        Client client = new Client("clientId", "familyName", "customerId", true, true, "unitId", "serviceId", "appointmentTypeId", true);
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken(client, null, Collections.emptyList()));
        String payload = createJwtClientSerializer().serialize(client);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setSecure(true);
        request.setCookies(new Cookie(SecurityCookieService.COOKIE_NAME,payload));
        MockHttpServletResponse response = new MockHttpServletResponse();

        HttpRequestResponseHolder requestResponseHolder = new HttpRequestResponseHolder(request, response);
        repository.loadContext(requestResponseHolder);

        repository.saveContext(securityContext, requestResponseHolder.getRequest(), requestResponseHolder.getResponse());

        Cookie sessionCookie = response.getCookie(SecurityCookieService.COOKIE_NAME);
        assertThat(sessionCookie.getMaxAge(), equalTo(1800));
        assertThat(sessionCookie.getValue().length(), greaterThan(0));
        assertTrue(sessionCookie.getSecure());
        assertTrue(sessionCookie.isHttpOnly());
    }

    @Test
    public void addCsrfCookieOnResponseForNonEmptySecurityContext() throws Exception {
        CookieBasedSecurityContextRepository repository = createCookieBasedSecurityContextRepository();
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        Client client = new Client("clientId", "familyName", "customerId", true, true, "unitId", "serviceId", "appointmentTypeId", true);
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken(client, null, Collections.emptyList()));
        String payload = createJwtClientSerializer().serialize(client);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setSecure(true);
        request.setCookies(new Cookie(SecurityCookieService.COOKIE_NAME, payload));
        request.setCookies(new Cookie(CookieBasedCsrfTokenRepository.CSRF_COOKIE_AND_PARAMETER_NAME, "csrfTokenValue"));
        MockHttpServletResponse response = new MockHttpServletResponse();

        HttpRequestResponseHolder requestResponseHolder = new HttpRequestResponseHolder(request, response);
        repository.loadContext(requestResponseHolder);

        repository.saveContext(securityContext, requestResponseHolder.getRequest(), requestResponseHolder.getResponse());

        Cookie csrfCookie = response.getCookie(CookieBasedCsrfTokenRepository.CSRF_COOKIE_AND_PARAMETER_NAME);
        assertThat(csrfCookie.getMaxAge(), equalTo(1800));
        assertThat(csrfCookie.getValue(), equalTo("csrfTokenValue"));
        assertTrue(csrfCookie.getSecure());
        assertTrue(csrfCookie.isHttpOnly());
    }

    @Test
    public void expireSessionCookieForEmptySecurityContext() throws Exception {
        CookieBasedSecurityContextRepository repository = createCookieBasedSecurityContextRepository();
        SecurityContext emptySecurityContext = SecurityContextHolder.createEmptyContext();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setSecure(true);
        MockHttpServletResponse response = new MockHttpServletResponse();

        HttpRequestResponseHolder requestResponseHolder = new HttpRequestResponseHolder(request, response);
        repository.loadContext(requestResponseHolder);

        repository.saveContext(emptySecurityContext, requestResponseHolder.getRequest(), requestResponseHolder.getResponse());

        Cookie sessionCookie = response.getCookie(SecurityCookieService.COOKIE_NAME);
        assertThat(sessionCookie.getMaxAge(), equalTo(0));
        assertThat(sessionCookie.getValue(), isEmptyString());
        assertTrue(sessionCookie.getSecure());
        assertTrue(sessionCookie.isHttpOnly());
    }

    private JwtClientSerializer createJwtClientSerializer() {
        return new JwtClientSerializer(Base64.getEncoder().encodeToString(new byte[32]), new ObjectMapper(), new ExpirationJwtClaimsVerifier());
    }

    private CookieBasedSecurityContextRepository createCookieBasedSecurityContextRepository() {
        return new CookieBasedSecurityContextRepository(new SecurityCookieService(createJwtClientSerializer()), new CookieBasedCsrfTokenRepository());
    }
}
