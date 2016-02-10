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
        String id = UUID.randomUUID().toString();
        LOG.info("Received request method=[{}] path=[{}] userAgent=[{}] requestId=[{}]", request.getMethod(), path, userAgent, id);
        filterChain.doFilter(request, response);
        long responseTime = System.currentTimeMillis() - startTime;
        LOG.info("Returning response statusCode=[{}] responseTime=[{}ms] method=[{}] path=[{}] requestId=[{}]", response.getStatus(), responseTime, request.getMethod(), path, id);
    }

    @Override
    public void destroy() {
    }
}
