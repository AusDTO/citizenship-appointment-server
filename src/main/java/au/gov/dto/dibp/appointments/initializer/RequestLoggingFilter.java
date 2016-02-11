package au.gov.dto.dibp.appointments.initializer;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

public class RequestLoggingFilter implements Filter {
    private static final Logger LOG = LoggerFactory.getLogger(RequestLoggingFilter.class);
    private static final String UNKNOWN_USER_AGENT = "unknown";
    public static final String REQUEST_ATTRIBUTE_CORRELATION_ID = "correlationId";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        long startTime = System.currentTimeMillis();
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String userAgent = StringUtils.defaultString(request.getHeader("user-agent"), UNKNOWN_USER_AGENT);
        String path = request.getQueryString() == null ? request.getRequestURI() : request.getRequestURI() + "?" + request.getQueryString();
        String correlationId = UUID.randomUUID().toString();
        request.setAttribute(REQUEST_ATTRIBUTE_CORRELATION_ID, correlationId);
        LOG.info("Received request method=[{}] path=[{}] userAgent=[{}] correlationId=[{}]", request.getMethod(), path, userAgent, correlationId);
        filterChain.doFilter(request, response);
        long responseTime = System.currentTimeMillis() - startTime;
        LOG.info("Returning response statusCode=[{}] responseTime=[{}ms] method=[{}] path=[{}] correlationId=[{}]", response.getStatus(), responseTime, request.getMethod(), path, correlationId);
    }

    @Override
    public void destroy() {
    }
}
