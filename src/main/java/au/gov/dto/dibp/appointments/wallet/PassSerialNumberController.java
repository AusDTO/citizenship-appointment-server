package au.gov.dto.dibp.appointments.wallet;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
class PassSerialNumberController {
    /**
     * Reference: https://developer.apple.com/library/ios/documentation/PassKit/Reference/PassKit_WebService/WebService.html#//apple_ref/doc/uid/TP40011988-CH0-SW4
     */
    @RequestMapping(value = "/wallet/v1/devices/{deviceLibraryIdentifier}/registrations/${wallet.pass.type.identifier}", method = RequestMethod.GET, produces = "application/json")
    public Map<String, Object> retrievePassSerialNumbers() {
        return new HashMap<String, Object>() {{
            put("lastUpdated", UUID.randomUUID().toString());
            put("serialNumbers", Collections.singletonList("citizenship"));
        }};
    }
}
