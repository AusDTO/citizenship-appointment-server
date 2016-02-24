package au.gov.dto.dibp.appointments.security.context;

import au.gov.dto.dibp.appointments.security.csrf.CookieBasedCsrfTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SaveContextOnUpdateOrErrorResponseWrapper;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Adapted from oakfusion/spring-cookie-session under the MIT license:
 * https://github.com/oakfusion/spring-cookie-session/blob/spring-cookie-session-1.0/src/main/java/com/oakfusion/security/CookieSecurityContextRepository.java
 */
@Component
public class CookieBasedSecurityContextRepository implements SecurityContextRepository {
    private final CookieBasedCsrfTokenRepository csrfTokenRepository;
    private final SecurityContextSerializer securityContextSerializer;

    @Autowired
    public CookieBasedSecurityContextRepository(CookieBasedCsrfTokenRepository csrfTokenRepository,
                                                SecurityContextSerializer securityContextSerializer) {
        this.csrfTokenRepository = csrfTokenRepository;
        this.securityContextSerializer = securityContextSerializer;
    }

    @Override
    public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
        SaveToCookieResponseWrapper responseWrapper = new SaveToCookieResponseWrapper(requestResponseHolder.getRequest(), requestResponseHolder.getResponse(), true);
        requestResponseHolder.setResponse(responseWrapper);
        return securityContextSerializer.deserialize(requestResponseHolder.getRequest(), requestResponseHolder.getResponse());
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
        return securityContextSerializer.hasSessionCookie(request);
    }

    private class SaveToCookieResponseWrapper extends SaveContextOnUpdateOrErrorResponseWrapper {
        private final HttpServletRequest request;

        public SaveToCookieResponseWrapper(HttpServletRequest request, HttpServletResponse response, boolean disableUrlRewriting) {
            super(response, disableUrlRewriting);
            this.request = request;
        }

        @Override
        protected void saveContext(SecurityContext securityContext) {
            HttpServletResponse response = (HttpServletResponse) getResponse();
            if (securityContext.getAuthentication() != null) {
                CsrfToken csrfToken = csrfTokenRepository.loadToken(request);
                if (csrfToken != null) {
                    csrfTokenRepository.saveToken(csrfToken, request, response);
                }
            }
            securityContextSerializer.serialize(securityContext, request, response);
        }
    }
}
