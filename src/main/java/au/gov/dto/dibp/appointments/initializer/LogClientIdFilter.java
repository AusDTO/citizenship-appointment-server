package au.gov.dto.dibp.appointments.initializer;

import org.slf4j.MDC;
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
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        try {
            MDC.put(MDC_KEY_CLIENT_ID, UsernameExtractor.getAuthenticatedUsername());
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(MDC_KEY_CLIENT_ID);
        }
    }

    @Override
    public void destroy() {
    }
}
