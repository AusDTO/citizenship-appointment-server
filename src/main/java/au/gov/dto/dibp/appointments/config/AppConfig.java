package au.gov.dto.dibp.appointments.config;

import au.gov.dto.dibp.appointments.initializer.DoSFilter;
import au.gov.dto.dibp.appointments.initializer.HttpsOnlyFilter;
import au.gov.dto.dibp.appointments.initializer.LogClientIdFilter;
import au.gov.dto.dibp.appointments.initializer.NoHttpSessionFilter;
import au.gov.dto.dibp.appointments.initializer.RequestLoggingFilter;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.embedded.ServletContextInitializer;
import org.springframework.boot.context.embedded.ServletListenerRegistrationBean;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.jetty.JettyServerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import javax.servlet.http.HttpSessionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Configuration
@ComponentScan(basePackages = "au.gov.dto.dibp.appointments")
public class AppConfig {
    private static final Logger LOG = LoggerFactory.getLogger(AppConfig.class);

    @Bean
    public ServletContextInitializer noSessionTrackingServletContextInitializer() {
        return servletContext -> servletContext.setSessionTrackingModes(Collections.emptySet());
    }

    @Bean
    public EmbeddedServletContainerCustomizer disableServerHeader() {
        return container -> {
            if (container instanceof JettyEmbeddedServletContainerFactory) {
                ((JettyEmbeddedServletContainerFactory) container).addServerCustomizers(new JettyServerCustomizer() {
                    @Override
                    public void customize(Server server) {
                        for (Connector connector : server.getConnectors()) {
                            if (connector instanceof ServerConnector) {
                                HttpConnectionFactory connectionFactory = connector.getConnectionFactory(HttpConnectionFactory.class);
                                connectionFactory.getHttpConfiguration().setSendServerVersion(false);
                            }
                        }
                    }
                });
            }
        };
    }

    @Bean
    @Order(1)
    public FilterRegistrationBean dosFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        DoSFilter dosFilter = new DoSFilter();
        registration.setFilter(dosFilter);
        registration.addInitParameter("enabled", "true");
        registration.addInitParameter("insertHeaders", "false");
        registration.addInitParameter("ipWhitelist", getIpWhitelist());
        registration.addInitParameter("managedAttr", "true");  // allow filter to be managed via JMX
        registration.addInitParameter("remotePort", "false");
        registration.addInitParameter("trackSessions", "false");
        registration.addUrlPatterns("/*");
        return registration;
    }

    private String getIpWhitelist() {
        List<String> ipWhitelist = new ArrayList<>();
        String ipAddressOrCidrRange;
        for (int i = 1; StringUtils.isNotBlank(ipAddressOrCidrRange = System.getenv("IP_WHITELIST_" + i)); i++) {
            LOG.info("Adding IP address or range=[{}] to DoSFilter whitelist", ipAddressOrCidrRange);
            ipWhitelist.add(ipAddressOrCidrRange);
        }
        String[] ipWhitelistArray = ipWhitelist.toArray(new String[ipWhitelist.size()]);
        return String.join(",", ipWhitelistArray);
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
    @Order(3)
    public FilterRegistrationBean logClientIdFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new LogClientIdFilter());
        registration.addUrlPatterns("/*");
        return registration;
    }

    @Bean
    @Order(4)
    public FilterRegistrationBean reuqestLoggingFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new RequestLoggingFilter());
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
    public ServletListenerRegistrationBean<HttpSessionListener> sessionCreatedListener() {
        ServletListenerRegistrationBean<HttpSessionListener> listenerRegistrationBean = new ServletListenerRegistrationBean<>();
        listenerRegistrationBean.setListener(new HttpSessionCreatedListener());
        return listenerRegistrationBean;
    }
}
