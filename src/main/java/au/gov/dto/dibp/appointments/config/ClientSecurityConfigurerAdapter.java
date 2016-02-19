package au.gov.dto.dibp.appointments.config;

import au.gov.dto.dibp.appointments.initializer.CustomAuthenticationProvider;
import au.gov.dto.dibp.appointments.login.LoginClientService;
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
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Authentication configuration for clients (citizenship applicants).
 */
@Configuration
@Order(2)  // must have higher value (lower priority) than AdminSecurityConfigurerAdapter
@EnableWebSecurity
public class ClientSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {
    private static final String LOGIN_FAILURE_URL = "/login?error";

    @Autowired
    private LoginClientService loginClientService;

    @Autowired
    private CookieBasedCsrfTokenRepository cookieBasedCsrfTokenRepository;

    @Autowired
    private CookieBasedSecurityContextRepository cookieBasedSecurityContextRepository;

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(HttpMethod.GET, "/images/**", "/static/**", "/barcode/**", "/login", "/session_timeout", "/sessionExpired", "/error", "/");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        SimpleUrlAuthenticationFailureHandler authenticationFailureHandler = new SimpleUrlAuthenticationFailureHandler(LOGIN_FAILURE_URL);
        authenticationFailureHandler.setAllowSessionCreation(false);

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
                .antMatchers("/", "/login").permitAll()  // no authentication on endpoints '/' and public assets
                .antMatchers("/admin/**", "/metrics/**").permitAll()  // handled by AdminSecurityConfigurerAdapter
                .anyRequest().authenticated()  // all other endpoints require authentication
                .and()
            .formLogin()
                .loginPage("/login") // custom login page
                .passwordParameter("familyName") // form element name
                .defaultSuccessUrl("/calendar")
                .failureHandler(authenticationFailureHandler)
                .permitAll()  // no authentication on login endpoint
                .and()
            .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
                .logoutSuccessUrl("/login")
                .permitAll()  // no authentication on logout endpoint
                .and()
            .exceptionHandling()
                .accessDeniedPage("/sessionExpired");
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
