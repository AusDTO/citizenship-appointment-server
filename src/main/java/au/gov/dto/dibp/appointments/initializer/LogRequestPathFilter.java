package au.gov.dto.dibp.appointments.initializer;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
public class LogRequestPathFilter implements Filter {

    private static final String MDC_KEY_REQUEST_PATH = "requestPath";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try {
            HttpServletRequest request = (HttpServletRequest) servletRequest;
            String requestPath = request.getRequestURI();
            MDC.put(MDC_KEY_REQUEST_PATH, requestPath);
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            MDC.remove(MDC_KEY_REQUEST_PATH);
        }
    }

    @Override
    public void destroy() {

    }
}
