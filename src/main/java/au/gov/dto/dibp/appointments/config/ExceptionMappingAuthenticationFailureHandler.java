package au.gov.dto.dibp.appointments.config;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.util.Assert;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ExceptionMappingAuthenticationFailureHandler extends
        SimpleUrlAuthenticationFailureHandler {

    private final Map<String, String> failureUrlMap = new HashMap<String, String>();

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response, AuthenticationException exception)
            throws IOException, ServletException {
        String exceptionClass = exception.getClass().getName();
        if(exception.getCause() != null){
            exceptionClass = exception.getCause().getClass().getName();
        }

        String clientId = request.getParameter("username");
        if (clientId != null && exception instanceof BadCredentialsException) {
            getRedirectStrategy().sendRedirect(request, response, "/login?error&id=" + clientId);
        }
        else {
            String url = failureUrlMap.get(exceptionClass);

            if (url != null) {
                getRedirectStrategy().sendRedirect(request, response, url);
            }
            else {
                super.onAuthenticationFailure(request, response, exception);
            }
        }
    }

    public void setExceptionMappings(Map<?, ?> failureUrlMap) {
        this.failureUrlMap.clear();
        for (Map.Entry<?, ?> entry : failureUrlMap.entrySet()) {
            Object exception = entry.getKey();
            Object url = entry.getValue();
            Assert.isInstanceOf(String.class, exception,
                    "Exception key must be a String (the exception classname).");
            Assert.isInstanceOf(String.class, url, "URL must be a String");
            Assert.isTrue(UrlUtils.isValidRedirectUrl((String) url),
                    "Not a valid redirect URL: " + url);
            this.failureUrlMap.put((String) exception, (String) url);
        }
    }
}
