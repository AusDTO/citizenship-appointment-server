package au.gov.dto.dibp.appointments.initializer;

import org.eclipse.jetty.servlets.CloseableDoSFilter;

import javax.servlet.ServletRequest;

/**
 * Documentation: http://www.eclipse.org/jetty/documentation/current/dos-filter.html
 */
public class DoSFilter extends CloseableDoSFilter {
    @Override
    protected String extractUserId(ServletRequest servletRequest) {
        return UsernameExtractor.getAuthenticatedUsername();
    }
}
