package au.gov.dto.dibp.appointments.config;

import au.gov.dto.dibp.appointments.initializer.HttpsOnlyFilter;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "au.gov.dto.dibp.appointments")
public class AppConfig {
    @Bean
    public FilterRegistrationBean httpsFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new HttpsOnlyFilter());
        registration.addUrlPatterns("/*");
        return registration;
    }
}
