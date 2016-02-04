package au.gov.dto.dibp.appointments.initializer;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class CustomAuthenticationProvider extends DaoAuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if(isPasswordBlank(authentication)){
            throw new BadCredentialsException("Bad credentials");
        }
        return super.authenticate(authentication);
    }

    private boolean isPasswordBlank(Authentication authentication){
        final String credentials = (String) authentication.getCredentials();
        return StringUtils.isBlank(credentials);
    }
}
