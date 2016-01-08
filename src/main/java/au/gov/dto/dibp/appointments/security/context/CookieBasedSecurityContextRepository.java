package au.gov.dto.dibp.appointments.security.context;

import com.oakfusion.security.SecurityCookieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SaveContextOnUpdateOrErrorResponseWrapper;
import org.springframework.security.web.context.SecurityContextRepository;
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

    @Autowired
    public CookieBasedSecurityContextRepository(SecurityCookieService securityCookieService) {
        this.securityCookieService = securityCookieService;
    }

    @Override
    public SecurityContext loadContext(final HttpRequestResponseHolder requestResponseHolder) {
        HttpServletRequest request = requestResponseHolder.getRequest();

        SaveToCookieResponseWrapper response = new SaveToCookieResponseWrapper(requestResponseHolder.getResponse(), true, request.isSecure());
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
        private final boolean secure;

        public SaveToCookieResponseWrapper(HttpServletResponse response, boolean disableUrlRewriting, boolean secure) {
            super(response, disableUrlRewriting);
            this.secure = secure;
        }

        @Override
        protected void saveContext(SecurityContext context) {
            Cookie securityCookie = securityCookieService.createSecurityCookie(context.getAuthentication());
            if (securityCookie != null) {
                if (!this.isContextSaved()) {
                    securityCookie.setHttpOnly(true);
                    securityCookie.setSecure(secure);
                    addCookie(securityCookie);
                }
            } else {
                Cookie logoutCookie = securityCookieService.createLogoutCookie();
                logoutCookie.setHttpOnly(true);
                logoutCookie.setSecure(secure);
                addCookie(logoutCookie);
            }
        }
    }
}
