package au.gov.dto.dibp.appointments.security.context;

import au.gov.dto.dibp.appointments.client.Client;
import au.gov.dto.dibp.appointments.security.csrf.CookieBasedCsrfTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SaveContextOnUpdateOrErrorResponseWrapper;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

/**
 * Adapted from oakfusion/spring-cookie-session under the MIT license:
 * https://github.com/oakfusion/spring-cookie-session/blob/spring-cookie-session-1.0/src/main/java/com/oakfusion/security/CookieSecurityContextRepository.java
 */
@Component
public class CookieBasedSecurityContextRepository implements SecurityContextRepository {
    private static final int COOKIE_MAX_AGE_SECONDS = 60 * 30;
    static final String COOKIE_NAME = "session";
    static final String COOKIE_PATH = "/";

    private final CookieBasedCsrfTokenRepository csrfTokenRepository;
    private final JwtClientSerializer jwtClientSerializer;

    @Autowired
    public CookieBasedSecurityContextRepository(CookieBasedCsrfTokenRepository csrfTokenRepository,
                                                JwtClientSerializer jwtClientSerializer) {
        this.csrfTokenRepository = csrfTokenRepository;
        this.jwtClientSerializer = jwtClientSerializer;
    }

    @Override
    public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
        HttpServletRequest request = requestResponseHolder.getRequest();
        HttpServletResponse response = requestResponseHolder.getResponse();
        SaveToCookieResponseWrapper responseWrapper = new SaveToCookieResponseWrapper(request, response, true);
        requestResponseHolder.setResponse(responseWrapper);

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        Client client = getClientFrom(request);
        if (client == null) {
            addLogoutCookie(requestResponseHolder.getRequest(), requestResponseHolder.getResponse());
        } else {
            securityContext.setAuthentication(createAuthenticationFor(client));
        }

        return securityContext;
    }

    private void addLogoutCookie(HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie = new Cookie(COOKIE_NAME, "");
        cookie.setPath(COOKIE_PATH);
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        cookie.setSecure(request.isSecure());
        response.addCookie(cookie);
    }

    private Client getClientFrom(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }
        Optional<Cookie> maybeCookie = Arrays.stream(request.getCookies()).filter(cookie -> cookie.getName().equals(COOKIE_NAME)).findFirst();
        if (!maybeCookie.isPresent()) {
            return null;
        }
        return jwtClientSerializer.deserialize(maybeCookie.get().getValue());
    }

    private Authentication createAuthenticationFor(Client client) {
        return new UsernamePasswordAuthenticationToken(client, null, Collections.emptyList());
    }

    @Override
    public void saveContext(SecurityContext securityContext, HttpServletRequest request, HttpServletResponse response) {
        SaveToCookieResponseWrapper responseWrapper = (SaveToCookieResponseWrapper) response;
        if (!responseWrapper.isContextSaved()) {
            responseWrapper.saveContext(securityContext);
        }
    }

    @Override
    public boolean containsContext(HttpServletRequest request) {
        return request.getCookies() != null && Arrays.stream(request.getCookies()).anyMatch(cookie -> cookie.getName().equals(COOKIE_NAME));
    }

    private class SaveToCookieResponseWrapper extends SaveContextOnUpdateOrErrorResponseWrapper {
        private final HttpServletRequest request;

        public SaveToCookieResponseWrapper(HttpServletRequest request, HttpServletResponse response, boolean disableUrlRewriting) {
            super(response, disableUrlRewriting);
            this.request = request;
        }

        @Override
        protected void saveContext(SecurityContext context) {
            HttpServletResponse response = (HttpServletResponse) getResponse();
            if (context.getAuthentication() != null) {
                CsrfToken csrfToken = csrfTokenRepository.loadToken(request);
                if (csrfToken != null) {
                    csrfTokenRepository.saveToken(csrfToken, request, response);
                }
                Client client = (Client) context.getAuthentication().getPrincipal();
                String payload = jwtClientSerializer.serialize(client);
                Cookie securityCookie = new Cookie(COOKIE_NAME, payload);
                securityCookie.setPath(COOKIE_PATH);
                securityCookie.setHttpOnly(true);
                securityCookie.setSecure(request.isSecure());
                securityCookie.setMaxAge(COOKIE_MAX_AGE_SECONDS);
                addCookie(securityCookie);
            } else {
                addLogoutCookie(request, response);
            }
        }
    }
}
