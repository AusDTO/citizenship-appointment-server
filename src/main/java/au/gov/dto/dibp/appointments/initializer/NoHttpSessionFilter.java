package au.gov.dto.dibp.appointments.initializer;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class NoHttpSessionFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest wrappedRequest = new HttpServletRequestWrapper((HttpServletRequest) request) {
            @Override
            public HttpSession getSession() {
                throw new RuntimeException("HttpSession should not be used");
            }

            @Override
            public HttpSession getSession(boolean create) {
                if (create) {
                    throw new RuntimeException("HttpSession should not be used");
                }
                return null;
            }
        };
        filterChain.doFilter(wrappedRequest, response);
    }

    @Override
    public void destroy() {
    }
}
