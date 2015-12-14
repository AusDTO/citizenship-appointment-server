package au.gov.dto.dibp.appointments.security;


import au.gov.dto.dibp.appointments.service.CustomerDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;

public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    CustomerDetailsService customerDetailsService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails user = customerDetailsService.getCustomerByClientId(username);
        if(user == null){
            throw new UsernameNotFoundException(username);
        }
        return user;
    }
}
