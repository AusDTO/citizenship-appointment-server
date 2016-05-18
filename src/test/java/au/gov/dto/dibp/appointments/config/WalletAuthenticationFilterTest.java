package au.gov.dto.dibp.appointments.config;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class WalletAuthenticationFilterTest {
    @Before
    public void setup() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(null);
        SecurityContextHolder.clearContext();
    }

    @Test
    public void shouldCreateAuthenticationInSecurityContextFromQueryString() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setQueryString("?id=123&otherid=abc");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        new WalletAuthenticationFilter().doFilterInternal(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication.getPrincipal(), equalTo("123"));
        assertThat(authentication.getCredentials(), equalTo("abc"));
        assertThat(authentication.isAuthenticated(), equalTo(false));
        assertThat(filterChain.getRequest(), sameInstance(request));
        assertThat(filterChain.getResponse(), sameInstance(response));
    }

    @Test
    public void shouldNotCreateAuthenticationInSecurityContextForMissingQueryString() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        new WalletAuthenticationFilter().doFilterInternal(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication, nullValue());
        assertThat(filterChain.getRequest(), sameInstance(request));
        assertThat(filterChain.getResponse(), sameInstance(response));
    }

    @Test
    public void shouldNotCreateAuthenticationInSecurityContextForOtherQueryStringParameters() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setQueryString("?a=b&c=d");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        new WalletAuthenticationFilter().doFilterInternal(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication, nullValue());
        assertThat(filterChain.getRequest(), sameInstance(request));
        assertThat(filterChain.getResponse(), sameInstance(response));
    }

    @Test
    public void shouldCreateAuthenticationInSecurityContextForApplePassAuthorizationHeader() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        String authorizationToken = "123:abc";
        String encodedAuthorizationToken = Base64.getMimeEncoder(Integer.MAX_VALUE, new byte[]{'\r', '\n'}).encodeToString(authorizationToken.getBytes(StandardCharsets.ISO_8859_1));
        request.addHeader("authorization", "ApplePass " + encodedAuthorizationToken);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        new WalletAuthenticationFilter().doFilterInternal(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication.getPrincipal(), equalTo("123"));
        assertThat(authentication.getCredentials(), equalTo("abc"));
        assertThat(authentication.isAuthenticated(), equalTo(false));
        assertThat(filterChain.getRequest(), sameInstance(request));
        assertThat(filterChain.getResponse(), sameInstance(response));
    }

    @Test
    public void shouldNotCreateAuthenticationInSecurityContextForNonApplePassAuthorizationHeader() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        String authorizationToken = "123:abc";
        String encodedAuthorizationToken = Base64.getMimeEncoder(Integer.MAX_VALUE, new byte[]{'\r', '\n'}).encodeToString(authorizationToken.getBytes(StandardCharsets.ISO_8859_1));
        request.addHeader("authorization", "Basic " + encodedAuthorizationToken);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        new WalletAuthenticationFilter().doFilterInternal(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication, nullValue());
        assertThat(filterChain.getRequest(), sameInstance(request));
        assertThat(filterChain.getResponse(), sameInstance(response));
    }

    @Test
    public void shouldNotCreateAuthenticationInSecurityContextForInvalidApplePassAuthorizationHeader() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("authorization", "ApplePass invalidToken");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        new WalletAuthenticationFilter().doFilterInternal(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication, nullValue());
        assertThat(filterChain.getRequest(), sameInstance(request));
        assertThat(filterChain.getResponse(), sameInstance(response));
    }
}
