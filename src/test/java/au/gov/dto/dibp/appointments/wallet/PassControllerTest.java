package au.gov.dto.dibp.appointments.wallet;

import au.gov.dto.dibp.appointments.appointmentdetails.AppointmentDetails;
import au.gov.dto.dibp.appointments.appointmentdetails.AppointmentDetailsService;
import au.gov.dto.dibp.appointments.client.Client;
import au.gov.dto.dibp.appointments.util.FakeTemplateLoader;
import org.junit.Test;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class PassControllerTest {
    private static final String USER_AGENT_CHROME_ON_MAC = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36";
    private static final String USER_AGENT_SAFARI_ON_MAC = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_5) AppleWebKit/601.6.17 (KHTML, like Gecko) Version/9.1.1 Safari/601.6.17";

    @Test
    public void retrievePassRedirectsToHelpPageForUnsupportedDevice() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("user-agent", USER_AGENT_CHROME_ON_MAC);
        MockHttpServletResponse response = new MockHttpServletResponse();
        Client client = new Client("clientId", "familyName", "customerId", true, true, "unitId", "serviceId", "appointmentTypeId", true);

        new PassController(null, null, "passTypeIdentifier").retrievePass(client, request, response);

        assertThat(response.getRedirectedUrl(), equalTo("/wallet/pass/barcode?id=clientId&otherid=customerId"));
    }

    @Test
    public void retrievePassRedirectsToPassForSupportedDevice() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("user-agent", USER_AGENT_SAFARI_ON_MAC);
        MockHttpServletResponse response = new MockHttpServletResponse();

        Client client = new Client("clientId", "familyName", "customerId", true, true, "unitId", "serviceId", "appointmentTypeId", true);
        new PassController(stubPassBuilder(), stubAppointmentDetailsService(), "passTypeIdentifier").retrievePass(client, request, response);

        assertThat(response.getRedirectedUrl(), equalTo("/wallet/v1/passes/passTypeIdentifier/citizenship?id=clientId&otherid=customerId"));
    }

    @Test
    public void createPassRejectsRequestIfAppointmentTooFarInThePast() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("user-agent", USER_AGENT_SAFARI_ON_MAC);

        Client client = new Client("clientId", "familyName", "customerId", true, true, "unitId", "serviceId", "appointmentTypeId", true);
        ResponseEntity<Resource> responseEntity = new PassController(stubPassBuilder(), new AppointmentDetailsService(null, null, new FakeTemplateLoader(), "") {
            @Override
            public AppointmentDetails getExpectedAppointmentForClientForNextYear(Client client1) {
                return new AppointmentDetails(LocalDateTime.of(2016, 5, 22, 0, 0).minus(13L, ChronoUnit.HOURS), 20, "1", "1", "11111", "Some unit", "3939 Street, Place", "UTC");
            }
        }, "passTypeIdentifier").createPass(client, request);

        assertThat(responseEntity.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void createPassAcceptsRequestIfAppointmentNotTooFarInThePast() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("user-agent", USER_AGENT_SAFARI_ON_MAC);

        Client client = new Client("clientId", "familyName", "customerId", true, true, "unitId", "serviceId", "appointmentTypeId", true);
        ResponseEntity<Resource> responseEntity = new PassController(stubPassBuilder(), new AppointmentDetailsService(null, null, new FakeTemplateLoader(), "") {
            @Override
            public AppointmentDetails getExpectedAppointmentForClientForNextYear(Client client1) {
                return new AppointmentDetails(LocalDateTime.now().minus(11L, ChronoUnit.HOURS), 20, "1", "1", "11111", "Some unit", "3939 Street, Place", "UTC");
            }
        }, "passTypeIdentifier").createPass(client, request);

        assertThat(responseEntity.getStatusCode(), equalTo(HttpStatus.OK));
    }

    @Test
    public void createPassAcceptsRequestIfAppointmentInTheFuture() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("user-agent", USER_AGENT_SAFARI_ON_MAC);

        Client client = new Client("clientId", "familyName", "customerId", true, true, "unitId", "serviceId", "appointmentTypeId", true);
        ResponseEntity<Resource> responseEntity = new PassController(stubPassBuilder(), new AppointmentDetailsService(null, null, new FakeTemplateLoader(), "") {
            @Override
            public AppointmentDetails getExpectedAppointmentForClientForNextYear(Client client1) {
                return new AppointmentDetails(LocalDateTime.now().plus(13L, ChronoUnit.HOURS), 20, "1", "1", "11111", "Some unit", "3939 Street, Place", "UTC");
            }
        }, "passTypeIdentifier").createPass(client, request);

        assertThat(responseEntity.getStatusCode(), equalTo(HttpStatus.OK));
    }

    @Test
    public void testCreateWalletWebServiceUrl() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setSecure(true);
        request.setScheme("http");
        request.setServerName("example.com");
        request.setPathInfo("/path/morepath");
        request.setQueryString("a=b&c=d");

        URL url = new PassController(null, null, "passTypeIdentifier").getWalletWebServiceUrl(request);

        assertThat(url.toString(), equalTo("https://example.com/wallet"));
    }

    private AppointmentDetailsService stubAppointmentDetailsService() {
        return new AppointmentDetailsService(null, null, new FakeTemplateLoader(), "") {
            @Override
            public AppointmentDetails getExpectedAppointmentForClientForNextYear(Client client) {
                return new AppointmentDetails(LocalDateTime.now(), 20, "1", "1", "11111", "Some unit", "3939 Street, Place", "51");
            }
        };
    }

    private PassBuilder stubPassBuilder() {
        return new PassBuilder(null, null, null, null) {
            @Override
            public Pass createAppointmentPassForClient(Client client, AppointmentDetails appointment, URL walletWebServiceUrl) {
                return new Pass(null, null, null, null) {
                    @Override
                    public byte[] getBytes() {
                        return new byte[0];
                    }
                };
            }
        };
    }
}
