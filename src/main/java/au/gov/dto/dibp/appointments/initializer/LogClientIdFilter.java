package au.gov.dto.dibp.appointments.initializer;

import au.gov.dto.dibp.appointments.client.Client;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.*;
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
                Client client = (Client) auth.getPrincipal();
                MDC.put(MDC_KEY_CLIENT_ID, client.getClientId());
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
