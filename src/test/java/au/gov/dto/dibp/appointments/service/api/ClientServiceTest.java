package au.gov.dto.dibp.appointments.service.api;

import au.gov.dto.dibp.appointments.model.Customer;
import com.squareup.okhttp.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.util.Map;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ClientServiceTest {

    @Mock
    ApiCallsSenderService senderService;

    @InjectMocks
    ClientService service;

    @Before
    public void setup() {
        ReflectionTestUtils.setField(service, "SERVICE_ADDRESS_CUSTOMER", "http://someurl");
    }

    @Test
    public void getCustomerByExternalReference_shouldPutClientIdIntoData() throws ParserConfigurationException, SAXException, XPathExpressionException, IOException {
        String clientId = "123";
        ArgumentCaptor<Map> dataArgumentCaptor = ArgumentCaptor.forClass(Map.class);

        when(senderService.sendRequest(anyString(), dataArgumentCaptor.capture(), anyString())).thenReturn(getStandardResponse());
        service.getCustomerByExternalReference(clientId);

        Map<String, String> capturedData = dataArgumentCaptor.getValue();
        assertThat(capturedData.size(), is(1));
        assertThat(capturedData.get("externalReference"), is(clientId));
    }

    @Test
    public void getCustomerByExternalReference_shouldCallSendServiceWithCorrectArguments() throws ParserConfigurationException, SAXException, XPathExpressionException, IOException {
        String clientId = "123";
        ArgumentCaptor<Map> dataArgumentCaptor = ArgumentCaptor.forClass(Map.class);
        ArgumentCaptor<String> templatePathArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> serviceAddressArgumentCaptor = ArgumentCaptor.forClass(String.class);

        when(senderService.sendRequest(templatePathArgumentCaptor.capture(), dataArgumentCaptor.capture(), serviceAddressArgumentCaptor.capture())).thenReturn(getStandardResponse());
        service.getCustomerByExternalReference(clientId);

        Map<String, String> capturedData = dataArgumentCaptor.getValue();
        assertThat(capturedData.size(), is(1));
        assertThat(templatePathArgumentCaptor.getValue(), endsWith("GetByExtRef.mustache"));
        assertThat(serviceAddressArgumentCaptor.getValue(), is("http://someurl"));
    }

    @Test
    public void getCustomerByExternalReference_shouldConvertResponseIntoCustomerObject() throws ParserConfigurationException, SAXException, XPathExpressionException, IOException {
        String clientId = "123";
        when(senderService.sendRequest(anyString(), Matchers.<Map<String, String>>any(), anyString())).thenReturn(getStandardResponse());
        Customer customer = service.getCustomerByExternalReference(clientId);

        assertThat(customer.isEnabled(), is(true));
        assertThat(customer.getCustomerClientId(), is("919191"));
        assertThat(customer.getId(), is("6"));
        assertThat(customer.isWithEmailAddress(), is(true));
        assertThat(customer.getPassword(), is("Smith"));
        assertThat(customer.getUsername(), is("919191"));
        assertThat(customer.getAuthorities().size(), is(1));
        assertThat(customer.getAuthorities().contains(new SimpleGrantedAuthority("USER")), is(true));
    }

    private Response getStandardResponse(){
        String response =
            "<s:Envelope xmlns:s=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:a=\"http://www.w3.org/2005/08/addressing\">\n" +
            "   <s:Header/>\n" +
            "   <s:Body>\n" +
            "      <GetByExtRefResponse xmlns=\"http://www.qnomy.com/Services\">\n" +
            "         <GetByExtRefResult xmlns:b=\"http://schemas.datacontract.org/2004/07/QFlow.Library\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
            "            <b:Customer>\n" +
            "               <b:Active>true</b:Active>\n" +
            "               <b:ExtRef>919191</b:ExtRef>\n" +
            "               <b:Id>6</b:Id>\n" +
            "               <b:Name>Smith Martin</b:Name>\n" +
            "               <b:EMail>2323@test.com</b:EMail>\n" +
            "               <b:FirstName>Martin</b:FirstName>\n" +
            "               <b:LastName>Smith</b:LastName>\n" +
            "            </b:Customer>\n" +
            "         </GetByExtRefResult>\n" +
            "      </GetByExtRefResponse>\n" +
            "   </s:Body>\n" +
            "</s:Envelope>";

        Response.Builder responseBuilder = new Response.Builder();
        responseBuilder.code(200);
        responseBuilder.protocol(Protocol.HTTP_1_1);

        ResponseBody body = ResponseBody.create(MediaType.parse("application/soap+xml; charset=utf-8"), response);
        responseBuilder.body(body);

        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url("http://test.test.com");
        responseBuilder.request(requestBuilder.build());
        return responseBuilder.build();
    }

}
