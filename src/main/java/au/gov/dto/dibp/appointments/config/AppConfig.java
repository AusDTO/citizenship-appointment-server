package au.gov.dto.dibp.appointments.config;

import au.gov.dto.dibp.appointments.initializer.HttpsOnlyFilter;
import au.gov.dto.dibp.appointments.initializer.LogClientIdFilter;
import au.gov.dto.dibp.appointments.initializer.NoHttpSessionFilter;
import au.gov.dto.dibp.appointments.initializer.DoSFilter;
import com.oakfusion.security.SecurityCookieService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.embedded.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import javax.servlet.http.HttpSessionListener;

@Configuration
@ComponentScan(basePackages = "au.gov.dto.dibp.appointments")
public class AppConfig {
    private static final Logger LOG = LoggerFactory.getLogger(AppConfig.class);
    private static final String SESSION_COOKIE_NAME = "session";

    @Bean
    @Order(1)
    public FilterRegistrationBean dosFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        DoSFilter dosFilter = new DoSFilter();
        String ipAddressOrCidrRange;
        for (int i = 1; StringUtils.isNotBlank(ipAddressOrCidrRange = System.getenv("IP_WHITELIST_" + i)); i++) {
            dosFilter.addWhitelistAddress(ipAddressOrCidrRange);
            LOG.info("Adding IP address or range=[{}] to DoSFilter whitelist", ipAddressOrCidrRange);
        }
        registration.setFilter(dosFilter);
        registration.addInitParameter("managedAttr", "true");  // allow filter to be managed via JMX
        registration.addUrlPatterns("/*");
        return registration;
    }

    @Bean
    @Order(2)
    public FilterRegistrationBean httpsFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new HttpsOnlyFilter());
        registration.addUrlPatterns("/*");
        return registration;
    }

    @Bean
    public FilterRegistrationBean noHttpSessionFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new NoHttpSessionFilter());
        registration.addUrlPatterns("/*");
        return registration;
    }

    @Bean
    public FilterRegistrationBean logClientIdFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new LogClientIdFilter());
        registration.addUrlPatterns("/*");
        return registration;
    }

    @Bean
    public ServletListenerRegistrationBean<HttpSessionListener> sessionCreatedListener() {
        ServletListenerRegistrationBean<HttpSessionListener> listenerRegistrationBean = new ServletListenerRegistrationBean<>();
        listenerRegistrationBean.setListener(new HttpSessionCreatedListener());
        return listenerRegistrationBean;
    }

    @Bean
    public SecurityCookieService securityCookieService(@Value("${session.encryption.key}") String sessionEncryptionKey) {
        return new SecurityCookieService(SESSION_COOKIE_NAME, sessionEncryptionKey);
    }
}
