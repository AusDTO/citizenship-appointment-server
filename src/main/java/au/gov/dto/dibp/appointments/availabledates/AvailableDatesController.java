package au.gov.dto.dibp.appointments.availabledates;

import au.gov.dto.dibp.appointments.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AvailableDatesController {

    @Autowired
    private AvailableDatesService availableDatesService;

    @RequestMapping(value = "/get_available_dates.json", method = RequestMethod.GET, produces = "application/json")
    public Map<String, AvailableDate> getAvailableDates(@AuthenticationPrincipal Client client) {
        return availableDatesService.getAvailabilityForNextYear(client.getServiceId());
    }

}
