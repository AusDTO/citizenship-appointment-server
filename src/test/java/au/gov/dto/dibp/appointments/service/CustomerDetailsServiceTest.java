package au.gov.dto.dibp.appointments.service;

import au.gov.dto.dibp.appointments.model.Customer;
import au.gov.dto.dibp.appointments.service.api.GetByExternalReferenceService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CustomerDetailsServiceTest {

    @Mock
    GetByExternalReferenceService getByExtRefService;

    @InjectMocks
    CustomerDetailsService service;

    @Test
    public void getCustomerByClientId_shouldDelegateToGetByExternalReferenceService() throws ParserConfigurationException, SAXException, XPathExpressionException, IOException {
        String clientId = "1234";
        Customer customer = mock(Customer.class);
        when(customer.getUsername()).thenReturn(clientId);
        when(getByExtRefService.getCustomerByExternalReference(eq(clientId))).thenReturn(customer);

        Customer result = service.getCustomerByClientId(clientId);
        verify(getByExtRefService, times(1)).getCustomerByExternalReference(anyString());
        assertThat(result.getUsername(), is(clientId));
    }

    @Test
    public void getCustomerByClientId_shouldReturnNullIfExceptionOccurs() throws ParserConfigurationException, SAXException, XPathExpressionException, IOException {
        String clientId = "123";
        when(getByExtRefService.getCustomerByExternalReference(eq(clientId))).thenThrow(new ParserConfigurationException());

        Customer result = service.getCustomerByClientId(clientId);
        assertThat(result, is(nullValue()));
    }

}
