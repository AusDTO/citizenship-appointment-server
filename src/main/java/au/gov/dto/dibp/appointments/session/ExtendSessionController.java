package au.gov.dto.dibp.appointments.session;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class ExtendSessionController {
    @RequestMapping(value = "/extend_session", method = RequestMethod.GET, produces = "application/json")
    public Map<String, Object> extendSession() {
        return new HashMap<>();
    }
}
