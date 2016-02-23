package au.gov.dto.dibp.appointments.security.context;

import au.gov.dto.dibp.appointments.client.Client;
import au.gov.dto.dibp.appointments.security.csrf.CookieBasedCsrfTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

/**
 * Adapted from oakfusion/spring-cookie-session under the MIT license:
 * https://github.com/oakfusion/spring-cookie-session/blob/spring-cookie-session-1.0/src/main/java/com/oakfusion/security/CookieSecurityContextRepository.java
 */
@Component
public class CookieBasedSecurityContextRepository implements SecurityContextRepository {
    private final SecurityCookieService securityCookieService;
    private final CookieBasedCsrfTokenRepository csrfTokenRepository;
    private static final int COOKIE_MAX_AGE_SECONDS = 60 * 30;

    @Autowired
    public CookieBasedSecurityContextRepository(SecurityCookieService securityCookieService,
                                                CookieBasedCsrfTokenRepository csrfTokenRepository) {
        this.securityCookieService = securityCookieService;
        this.csrfTokenRepository = csrfTokenRepository;
    }

    @Override
    public SecurityContext loadContext(final HttpRequestResponseHolder requestResponseHolder) {
        HttpServletRequest request = requestResponseHolder.getRequest();

        SaveToCookieResponseWrapper response = new SaveToCookieResponseWrapper(request, requestResponseHolder.getResponse(), true);
        requestResponseHolder.setResponse(response);

        SecurityContext context = SecurityContextHolder.createEmptyContext();

        if (request.getCookies() != null) {
            Cookie securityCookie = securityCookieService.getSecurityCookieFrom(request);
            if (securityCookie != null) {
                Authentication authentication = securityCookieService.getAuthenticationFrom(securityCookie);
                if (authentication == null) {
                    Cookie cookie = securityCookieService.createLogoutCookie();
                    cookie.setHttpOnly(true);
                    cookie.setSecure(request.isSecure());
                    requestResponseHolder.getResponse().addCookie(cookie);
                }

                context.setAuthentication(authentication);
                return context;
            }
        }

        return context;
    }

    @Override
    public void saveContext(SecurityContext context, HttpServletRequest request, HttpServletResponse response) {
        SaveToCookieResponseWrapper responseWrapper = (SaveToCookieResponseWrapper) response;

        if (!responseWrapper.isContextSaved()) {
            responseWrapper.saveContext(context);
        }
    }

    @Override
    public boolean containsContext(HttpServletRequest request) {
        return securityCookieService.containsSecurityCookie(request);
    }

    private class SaveToCookieResponseWrapper extends SaveContextOnUpdateOrErrorResponseWrapper {
        private final HttpServletRequest request;

        public SaveToCookieResponseWrapper(HttpServletRequest request, HttpServletResponse response, boolean disableUrlRewriting) {
            super(response, disableUrlRewriting);
            this.request = request;
        }

        @Override
        protected void saveContext(SecurityContext context) {
            if (context.getAuthentication() != null &&
                    context.getAuthentication().getPrincipal() != null &&
                    Client.class.isAssignableFrom(context.getAuthentication().getPrincipal().getClass())) {
                if (!this.isContextSaved()) {
                    CsrfToken csrfToken = csrfTokenRepository.loadToken(request);
                    if (csrfToken != null) {
                        csrfTokenRepository.saveToken(csrfToken, request, (HttpServletResponse) getResponse());
                    }
                    Client client = (Client) context.getAuthentication().getPrincipal();
                    Cookie securityCookie = securityCookieService.createSecurityCookie(client);
                    securityCookie.setHttpOnly(true);
                    securityCookie.setSecure(request.isSecure());
                    securityCookie.setMaxAge(COOKIE_MAX_AGE_SECONDS);
                    addCookie(securityCookie);
                }
            } else {
                Cookie logoutCookie = securityCookieService.createLogoutCookie();
                logoutCookie.setHttpOnly(true);
                logoutCookie.setSecure(request.isSecure());
                addCookie(logoutCookie);
            }
        }
    }
}
