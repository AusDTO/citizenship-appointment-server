package au.gov.dto.dibp.appointments.config;

import au.gov.dto.dibp.appointments.security.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.encoding.PlaintextPasswordEncoder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
            .authorizeRequests()
                .antMatchers("/", "/images/*", "/static/*").permitAll()  // no authentication on endpoints '/' and public assets
                .anyRequest().authenticated()  // all other endpoints require authentication
                .and()
            .formLogin()
                .loginPage("/login") // custom login page
                .passwordParameter("familyName") // form element name
                .defaultSuccessUrl("/booking")
                .failureUrl("/login?error")
                .permitAll()  // no authentication on login endpoint
                .and()
            .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
                .logoutSuccessUrl("/login")
                .permitAll();  // no authentication on logout endpoint
    }

    @Bean
    public UserDetailsService customUserDetailsService() {
        return new CustomUserDetailsService();
    }

    @Autowired
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        PlaintextPasswordEncoder passwordEncoder = new PlaintextPasswordEncoder();
        passwordEncoder.setIgnorePasswordCase(true);

        auth
            .userDetailsService(customUserDetailsService())
            .passwordEncoder(passwordEncoder);
    }
}
