package au.gov.dto.dibp.appointments.initializer;

import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

@Component
public class LogClientIdFilter implements Filter {
    private static final String MDC_KEY_CLIENT_ID = "clientId";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse
            response, FilterChain filterChain) throws IOException,
            ServletException {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && !(auth.getPrincipal() instanceof String) ) {
                User user = (User) auth.getPrincipal();
                MDC.put(MDC_KEY_CLIENT_ID, user.getUsername());
            }
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(MDC_KEY_CLIENT_ID);
        }
    }

    @Override
    public void destroy() {
    }
}
