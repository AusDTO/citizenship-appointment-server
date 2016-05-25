package au.gov.dto.dibp.appointments.config;

import au.gov.dto.dibp.appointments.login.LoginClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.context.NullSecurityContextRepository;

@Configuration
@Order(2)  // must have higher value (lower priority) than ClientSecurityConfigurerAdapter
@EnableWebSecurity
public class WalletSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {
    @Autowired
    private LoginClientService loginClientService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
            .securityContext()
                .securityContextRepository(new NullSecurityContextRepository())
                .and()
            .csrf()
                .disable()
            .requestCache()
                .disable()
            .anonymous()
                .disable()
            .addFilterBefore(new WalletAuthenticationFilter(), BasicAuthenticationFilter.class)
            .requestMatchers()
                .antMatchers(HttpMethod.POST, "/wallet/v1/devices/**")
                .antMatchers(HttpMethod.DELETE, "/wallet/v1/devices/**")
                .antMatchers("/wallet/v1/passes/**")
                .antMatchers("/wallet/pass/**")
                .antMatchers("/wallet/pass*")
                .and()
            .authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
            .headers()
                .httpStrictTransportSecurity().disable()
                .contentTypeOptions().disable()
                .frameOptions().disable()
                .xssProtection().disable();
    }

    @Override
    protected UserDetailsService userDetailsService() {
        return loginClientService;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        WalletAuthenticationProvider walletAuthenticationProvider = new WalletAuthenticationProvider();
        walletAuthenticationProvider.setUserDetailsService(loginClientService);
        auth.authenticationProvider(walletAuthenticationProvider);
    }
}
