package au.gov.dto.dibp.appointments.wallet;

import au.gov.dto.dibp.appointments.appointmentdetails.AppointmentDetails;
import au.gov.dto.dibp.appointments.appointmentdetails.AppointmentDetailsService;
import au.gov.dto.dibp.appointments.client.Client;
import au.gov.dto.dibp.appointments.util.FakeTemplateLoader;
import org.junit.Test;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class PassControllerTest {
    @Test
    public void testRedirectForUnsupportedDevice() throws Exception {
        Client client = new Client("11111111111", "familyName", "customerId", true, true, "unitId", "serviceId", "appointmentTypeId", true);
        MockHttpServletRequest request = new MockHttpServletRequest();
        String userAgentChromeOnMac = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36";
        request.addHeader("user-agent", userAgentChromeOnMac);
        MockHttpServletResponse response = new MockHttpServletResponse();

        new PassController(null, null).createPass(client, request, response);

        assertThat(response.getRedirectedUrl(), equalTo("/wallet/barcode.html"));
    }

    @Test
    public void testDoesNotRedirectForSupportedDevice() throws Exception {
        Client client = new Client("11111111111", "familyName", "customerId", true, true, "unitId", "serviceId", "appointmentTypeId", true);
        MockHttpServletRequest request = new MockHttpServletRequest();
        String userAgentSafariOnMac = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_5) AppleWebKit/601.6.17 (KHTML, like Gecko) Version/9.1.1 Safari/601.6.17";
        request.addHeader("user-agent", userAgentSafariOnMac);
        MockHttpServletResponse response = new MockHttpServletResponse();

        ResponseEntity<Resource> responseEntity = new PassController(stubPassBuilder(), stubAppointmentDetailsService()).createPass(client, request, response);

        assertThat(response.getRedirectedUrl(), nullValue());
        assertThat(responseEntity, notNullValue());
        assertThat(responseEntity.getHeaders().getContentType().toString(), equalTo("application/vnd.apple.pkpass"));
    }

    @Test
    public void testCreateWalletWebServiceUrl() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setSecure(true);
        request.setScheme("http");
        request.setServerName("example.com");
        request.setPathInfo("/path/morepath");
        request.setQueryString("a=b&c=d");

        URL url = new PassController(null, null).getWalletWebServiceUrl(request);

        assertThat(url.toString(), equalTo("https://example.com/wallet"));
    }

    private AppointmentDetailsService stubAppointmentDetailsService() {
        return new AppointmentDetailsService(null, null, new FakeTemplateLoader(), ""){
            @Override
            public AppointmentDetails getExpectedAppointmentForClientForNextYear(Client client) {
                return new AppointmentDetails(LocalDateTime.parse("2016-05-23T13:00:00"),
                        20, "1", "1", "11111", "Some unit", "3939 Street, Place", "51");
            }
        };
    }

    private PassBuilder stubPassBuilder() {
        return new PassBuilder(null, null, null, null) {
            @Override
            public Pass createAppointmentPassForClient(Client client, AppointmentDetails appointment, URL walletWebServiceUrl) {
                return new Pass(null, null, null, null) {
                    @Override
                    public InputStream getInputStream() {
                        return new ByteArrayInputStream(new byte[0]);
                    }
                };
            }
        };
    }
}
