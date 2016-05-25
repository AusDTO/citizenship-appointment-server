package au.gov.dto.dibp.appointments.wallet;

import au.gov.dto.dibp.appointments.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

@Controller
class DeviceRegistrationController {
    private static final Logger LOG = LoggerFactory.getLogger(DeviceRegistrationController.class);

    private final DeviceRegistrationService deviceRegistrationService;

    @Autowired
    public DeviceRegistrationController(DeviceRegistrationService deviceRegistrationService) {
        this.deviceRegistrationService = deviceRegistrationService;
    }

    /**
     * Reference: https://developer.apple.com/library/ios/documentation/PassKit/Reference/PassKit_WebService/WebService.html#//apple_ref/doc/uid/TP40011988-CH0-SW2
     */
    @RequestMapping(value = "/wallet/v1/devices/{deviceLibraryIdentifier}/registrations/${wallet.pass.type.identifier}/citizenship", method = RequestMethod.POST)
    public ResponseEntity<String> registerDevice(@AuthenticationPrincipal Client client,
                                                 @PathVariable String deviceLibraryIdentifier,
                                                 @RequestBody Map<String, Object> requestBody) {
        String pushToken = requestBody.get("pushToken").toString();
        // TODO validate
        LOG.info("Device with deviceLibraryIdentifier=[{}] registered for pass updates with pushToken=[{}]", deviceLibraryIdentifier, pushToken);
        deviceRegistrationService.addDeviceForClient(client, deviceLibraryIdentifier, pushToken);
        return new ResponseEntity<>("", HttpStatus.CREATED);
    }

    /**
     * Reference: https://developer.apple.com/library/ios/documentation/PassKit/Reference/PassKit_WebService/WebService.html#//apple_ref/doc/uid/TP40011988-CH0-SW5
     */
    @RequestMapping(value = "/wallet/v1/devices/{deviceLibraryIdentifier}/registrations/${wallet.pass.type.identifier}/citizenship", method = RequestMethod.DELETE)
    public void unregisterDevice(@AuthenticationPrincipal Client client,
                                 @PathVariable String deviceLibraryIdentifier) {
        // TODO validate
        LOG.info("Device with deviceLibraryIdentifier=[{}] unregistered for pass updates", deviceLibraryIdentifier);
        deviceRegistrationService.removeDeviceForClient(client, deviceLibraryIdentifier);
    }
}
