package au.gov.dto.dibp.appointments.config;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

class HttpSessionCreatedListener implements HttpSessionListener {
    private static final Logger LOG = LoggerFactory.getLogger(HttpSessionCreatedListener.class);

    @Override
    public void sessionCreated(HttpSessionEvent event) {
        String stackTrace = StringUtils.join(Thread.currentThread().getStackTrace(), " ");
        LOG.warn("HttpSession was created: " + stackTrace);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
    }
}
