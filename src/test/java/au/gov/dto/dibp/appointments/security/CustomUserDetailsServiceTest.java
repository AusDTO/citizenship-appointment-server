package au.gov.dto.dibp.appointments.security;

import au.gov.dto.dibp.appointments.model.Customer;
import au.gov.dto.dibp.appointments.service.CustomerDetailsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CustomUserDetailsServiceTest {

    @Mock
    CustomerDetailsService customerDetailsService;

    @InjectMocks
    CustomUserDetailsService service;

    @Test
    public void loadUserByUsername_shouldDelegateToCustomerDetailsServiceAndReturnFoundUser(){
        String userName = "ASD";

        Customer customer = mock(Customer.class);
        when(customer.getCustomerClientId()).thenReturn(userName);
        when(customer.getUsername()).thenReturn(userName);
        when(customerDetailsService.getCustomerByClientId(eq(userName))).thenReturn(customer);

        UserDetails result = service.loadUserByUsername(userName);
        verify(customerDetailsService, times(1)).getCustomerByClientId(userName);
        assertThat(result.getUsername(), is(userName));
    }

    @Test(expected=UsernameNotFoundException.class)
    public void loadUserByUsername_shouldThrowUsernameNotFoundExceptionIfUserNotFound(){
        String userName = "ASD";

        when(customerDetailsService.getCustomerByClientId(eq(userName))).thenReturn(null);
        service.loadUserByUsername(userName);
    }

}
