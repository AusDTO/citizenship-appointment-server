package au.gov.dto.dibp.appointments.wallet;

import au.gov.dto.dibp.appointments.appointmentdetails.AppointmentDetails;
import au.gov.dto.dibp.appointments.appointmentdetails.AppointmentDetailsService;
import au.gov.dto.dibp.appointments.client.Client;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.DeviceType;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import eu.bitwalker.useragentutils.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

@Controller
class PassController {
    private static final Logger LOG = LoggerFactory.getLogger(PassController.class);

    private final PassBuilder passBuilder;
    private final AppointmentDetailsService appointmentDetailsService;
    private final String passTypeIdentifier;

    @Autowired
    public PassController(PassBuilder passBuilder,
                          AppointmentDetailsService appointmentDetailsService,
                          @Value("${wallet.pass.type.identifier}") String passTypeIdentifier) {
        this.passBuilder = passBuilder;
        this.appointmentDetailsService = appointmentDetailsService;
        this.passTypeIdentifier = passTypeIdentifier;
    }

    @RequestMapping(value = "/wallet/pass", method = RequestMethod.GET)
    public void retrievePass(@AuthenticationPrincipal Client client,
                             HttpServletRequest request,
                             HttpServletResponse response) throws IOException, URISyntaxException {
        String userAgentHeader = request.getHeader("user-agent");
        if (!isSupportedDevice(userAgentHeader)) {
            LOG.info("Redirecting unsupported Wallet device, User-Agent=[{}]", userAgentHeader);
            response.sendRedirect("/wallet/barcode.html");
            return;
        }
        response.sendRedirect(String.format("/wallet/v1/passes/%s/citizenship?id=%s&otherid=%s", passTypeIdentifier, client.getClientId(), client.getCustomerId()));
    }

    /**
     * Reference: https://developer.apple.com/library/ios/documentation/PassKit/Reference/PassKit_WebService/WebService.html#//apple_ref/doc/uid/TP40011988-CH0-SW6
     */
    @RequestMapping(value = "/wallet/v1/passes/${wallet.pass.type.identifier}/citizenship", method = RequestMethod.GET, produces = "application/vnd.apple.pkpass")
    public ResponseEntity<Resource> createPass(@AuthenticationPrincipal Client client,
                                               HttpServletRequest request) throws IOException, URISyntaxException {
        LOG.info("Creating pass for clientId=[{}]", client.getClientId());
        AppointmentDetails appointment = appointmentDetailsService.getExpectedAppointmentForClientForNextYear(client);
        // TODO validate appointment is not in the past
        URL walletWebServiceUrl = getWalletWebServiceUrl(request);
        Pass pass = passBuilder.createAppointmentPassForClient(client, appointment, walletWebServiceUrl);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(new MediaType("application", "vnd.apple.pkpass"));
        responseHeaders.setContentDispositionFormData("attachment", "appointment.pkpass");
        return new ResponseEntity<>(new InputStreamResource(pass.getInputStream()), responseHeaders, HttpStatus.OK);
    }

    private boolean isSupportedDevice(String userAgentValue) {
        UserAgent userAgent = new UserAgent(userAgentValue);
        if (userAgent.getOperatingSystem().getDeviceType() == DeviceType.MOBILE
                && userAgent.getOperatingSystem().getGroup() == OperatingSystem.IOS
                && userAgent.getOperatingSystem().getId() >= OperatingSystem.iOS6_IPHONE.getId()) {
            return true;
        }
        Version oldestSafariVersionWithGuaranteedSupport = new Version("6.2", "6", "2"); // Mac OS X 10.8.2 or later required to install this Safari version
        if (userAgent.getOperatingSystem() == OperatingSystem.MAC_OS_X
                && userAgent.getBrowser().getGroup() == Browser.SAFARI
                && userAgent.getBrowserVersion().compareTo(oldestSafariVersionWithGuaranteedSupport) >= 0) {
            return true;
        }
        return false;
    }

    URL getWalletWebServiceUrl(HttpServletRequest request) throws URISyntaxException, MalformedURLException {
        URI requestUri = new URI(request.getRequestURL().toString());
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUri(requestUri.resolve("/wallet"));
        if (request.isSecure()) {
            uriComponentsBuilder.scheme("https");
        }
        return new URL(uriComponentsBuilder.toUriString());
    }
}
