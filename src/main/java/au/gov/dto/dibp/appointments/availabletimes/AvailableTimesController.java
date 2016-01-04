package au.gov.dto.dibp.appointments.availabletimes;

import au.gov.dto.dibp.appointments.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class AvailableTimesController {
    @Autowired
    private AvailableTimesService availableTimesService;

    @RequestMapping(value = "/get_available_times", method = RequestMethod.GET, produces = "application/json")
    public Map<String, Object> getAvailableTimes(@AuthenticationPrincipal Client client, @RequestParam(value="calendar_id", required=false) String calendarId) {
        Map<String, Object> map = new HashMap<>();
        map.put("times", availableTimesService.getAvailableTimes(client, calendarId));
        return map;
    }
}
