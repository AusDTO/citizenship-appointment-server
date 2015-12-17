package au.gov.dto.dibp.appointments.security;


import au.gov.dto.dibp.appointments.service.api.GetByExternalReferenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;

public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    GetByExternalReferenceService getByExternalReferenceService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails user = null;
        try {
            user = getByExternalReferenceService.getCustomerByExternalReference(username);
        } catch (ParserConfigurationException|SAXException|XPathExpressionException|IOException e){
            throw new RuntimeException("Error when retrieving client with clientId=[" + username + "]", e);
        }
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        return user;
    }
}
