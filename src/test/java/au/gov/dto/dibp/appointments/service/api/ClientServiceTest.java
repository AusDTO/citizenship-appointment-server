package au.gov.dto.dibp.appointments.service.api;

import au.gov.dto.dibp.appointments.model.Client;
import au.gov.dto.dibp.appointments.util.ResponseWrapper;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ClientServiceTest {

    @Test
    public void getCustomerByExternalReference_shouldConvertResponseIntoCustomerObject() throws Exception {
        ClientService service = new ClientService(new ApiCallsSenderService() {
            @Override
            public ResponseWrapper sendRequest(String requestTemplatePath, Map<String, String> messageParams, String serviceAddress) {
                return getStandardResponse();
            }
        }, "serviceUrl");

        Client client = service.loadUserByUsername("919191");

        assertThat(client.isEnabled(), is(true));
        assertThat(client.getUsername(), is("919191"));
        assertThat(client.getClientId(), is("919191"));
        assertThat(client.getPassword(), is("Smith"));
        assertThat(client.getCustomerId(), is("6"));
        assertThat(client.hasEmail(), is(true));
    }

    private ResponseWrapper getStandardResponse() {
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
        return new ResponseWrapper(200, new ByteArrayInputStream(response.getBytes(StandardCharsets.UTF_8)));
    }

}
