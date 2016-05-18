package au.gov.dto.dibp.appointments.wallet;

import au.gov.dto.dibp.appointments.appointmentdetails.AppointmentDetailsService;
import au.gov.dto.dibp.appointments.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
class PassController {
    private static final Logger LOG = LoggerFactory.getLogger(PassController.class);

    private final AppointmentDetailsService appointmentDetailsService;

    @Autowired
    public PassController(AppointmentDetailsService appointmentDetailsService){
        this.appointmentDetailsService = appointmentDetailsService;
    }

    @RequestMapping(value = "/wallet/pass", method = RequestMethod.GET, produces = "application/vnd.apple.pkpass")
    public void createPass(@AuthenticationPrincipal Client client) {
    }

    @RequestMapping(value = "/wallet/v1/passes/test", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Map<String, Object> test(@AuthenticationPrincipal Client client) {
        return new HashMap<String, Object>() {{
           put("id", client.getClientId());
           put("otherid", client.getCustomerId());
        }};
    }
}
