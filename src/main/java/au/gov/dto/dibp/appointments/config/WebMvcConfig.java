package au.gov.dto.dibp.appointments.config;

import au.gov.dto.dibp.appointments.initializer.SecurityHeaderInterceptor;
import org.springframework.boot.autoconfigure.mustache.web.MustacheViewResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("redirect:/login");
    }

    /**
     * PWS requires this override to resolve mustache views,
     * otherwise it produces the following error:
     * "Circular view path [login]: would dispatch back to the current handler URL [/login] again"
     */
    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        registry.viewResolver(new MustacheViewResolver());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        String certificateFingerprintSha256 = System.getenv("CERTIFICATE_FINGERPRINT_SHA256_BASE64");
        registry.addInterceptor(new SecurityHeaderInterceptor(certificateFingerprintSha256));
    }

}
