package au.gov.dto.dibp.appointments.monitoring;

import au.gov.dto.dibp.appointments.qflowintegration.ApiPingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BackendController {

    private final ApiPingService apiPingService;

    @Autowired
    public BackendController(ApiPingService apiPingService) {
        this.apiPingService = apiPingService;
    }

    @RequestMapping(value = "/monitoring/backend", method = RequestMethod.GET, produces="text/plain")
    public ResponseEntity<String> pingBackend() {
        try {
            apiPingService.sendRequest();
        } catch (Exception e) {
            return new ResponseEntity<>("Yeah, nah", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("Nah, yeah", HttpStatus.OK);
    }
}
