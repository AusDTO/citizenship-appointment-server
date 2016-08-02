package au.gov.dto.dibp.appointments.config;

import au.gov.dto.dibp.appointments.initializer.CustomAuthenticationProvider;
import au.gov.dto.dibp.appointments.login.LoginClientService;
import au.gov.dto.dibp.appointments.qflowintegration.ApiLoginException;
import au.gov.dto.dibp.appointments.qflowintegration.MaintenanceException;
import au.gov.dto.dibp.appointments.security.context.CookieBasedSecurityContextRepository;
import au.gov.dto.dibp.appointments.security.csrf.CookieBasedCsrfTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.encoding.PlaintextPasswordEncoder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.HashMap;
import java.util.Map;

/**
 * Authentication configuration for clients (citizenship applicants).
 */
@Configuration
@Order(3)  // must have higher value (lower priority) than AdminSecurityConfigurerAdapter
@EnableWebSecurity
public class ClientSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {
    private static final String LOGIN_FAILURE_URL = "/login?error";
    private static final String LOGIN_SYSTEM_ERROR_URL = "/login?system_error";
    private static final String LOGIN_MAINTENANCE_URL = "/login?maintenance";

    @Autowired
    private LoginClientService loginClientService;

    @Autowired
    private CookieBasedCsrfTokenRepository cookieBasedCsrfTokenRepository;

    @Autowired
    private CookieBasedSecurityContextRepository cookieBasedSecurityContextRepository;

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(HttpMethod.GET, "/images/**", "/static/**", "/barcode/**", "/analytics_basic.js", "/login", "/session_timeout", "/sessionExpired", "/error", "/cookies", "/");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
            .securityContext()
                .securityContextRepository(cookieBasedSecurityContextRepository)
                .and()
            .csrf()
                .csrfTokenRepository(cookieBasedCsrfTokenRepository)
                .ignoringAntMatchers("/login")
                .and()
            .requestCache()
                .disable()
            .anonymous()
                .disable()
            .authorizeRequests()
                .antMatchers("/analytics_basic.js", "/", "/login").permitAll()  // no authentication on endpoints '/' and public assets
                .antMatchers("/monitoring/**").permitAll()  // handled by AdminSecurityConfigurerAdapter
                .antMatchers("/wallet/**").permitAll()  // handled by WalletSecurityConfigurerAdapter
                .anyRequest().authenticated()  // all other endpoints require authentication
                .and()
            .formLogin()
                .loginPage("/login") // custom login page
                .passwordParameter("familyName") // form element name
                .defaultSuccessUrl("/calendar")
                .failureHandler(getLoginExceptionFailureHandler())
                .permitAll()  // no authentication on login endpoint
                .and()
            .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
                .logoutSuccessUrl("/login")
                .permitAll()  // no authentication on logout endpoint
                .and()
            .exceptionHandling()
                .accessDeniedPage("/sessionExpired")
                .and()
            .headers()
                .httpStrictTransportSecurity().disable()
                .contentTypeOptions().disable()
                .frameOptions().disable()
                .xssProtection().disable();
    }

    private ExceptionMappingAuthenticationFailureHandler getLoginExceptionFailureHandler(){
        ExceptionMappingAuthenticationFailureHandler authenticationFailureHandler = new ExceptionMappingAuthenticationFailureHandler();
        authenticationFailureHandler.setAllowSessionCreation(false);
        authenticationFailureHandler.setDefaultFailureUrl(LOGIN_FAILURE_URL);

        Map<String, String> failureUrlMap = new HashMap<String, String>();
        failureUrlMap.put(ApiLoginException.class.getName(), LOGIN_SYSTEM_ERROR_URL);
        failureUrlMap.put(MaintenanceException.class.getName(), LOGIN_MAINTENANCE_URL);
        authenticationFailureHandler.setExceptionMappings(failureUrlMap);

        return authenticationFailureHandler;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return loginClientService;
    }

    @Autowired
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        PlaintextPasswordEncoder passwordEncoder = new PlaintextPasswordEncoder();
        passwordEncoder.setIgnorePasswordCase(true);

        CustomAuthenticationProvider provider = new CustomAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(userDetailsService());

        auth.authenticationProvider(provider);
    }

}
