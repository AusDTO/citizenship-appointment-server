package au.gov.dto.dibp.appointments.availabletimes;

import au.gov.dto.dibp.appointments.client.Client;
import au.gov.dto.dibp.appointments.util.CalendarIdValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class AvailableTimesController {

    private final AvailableTimesService availableTimesService;
    private final CalendarIdValidator calendarIdValidator;

    @Autowired
    public AvailableTimesController(AvailableTimesService availableTimesService, CalendarIdValidator calendarIdValidator){
        this.availableTimesService = availableTimesService;
        this.calendarIdValidator = calendarIdValidator;
    }

    @RequestMapping(value = "/get_available_times", method = RequestMethod.GET, produces = "application/json")
    public Map<String, Object> getAvailableTimes(@AuthenticationPrincipal Client client, @RequestParam(value="calendar_id", required=false) String calendarId) {

        if(!calendarIdValidator.isCalendarIdValid(calendarId)){
            throw new RuntimeException("Invalid format of the calendarId!");
        }

        Map<String, Object> map = new HashMap<>();
        map.put("times", availableTimesService.getAvailableTimes(client, calendarId));
        return map;
    }

    @RequestMapping(value = "/get_available_times_text_with_label", method = RequestMethod.GET, produces = "application/json")
    public Map<String, Object> getAvailableTimesWithLabel(@AuthenticationPrincipal Client client, @RequestParam(value="calendar_id", required=false) String calendarId) {

        if(!calendarIdValidator.isCalendarIdValid(calendarId)){
            throw new RuntimeException("Invalid format of the calendarId!");
        }

        final Map<String, List<String>> result = availableTimesService.getAvailableTimesWithCalendarDate(client, calendarId);
        String date = result.keySet().iterator().next();

        final List<String> availableTimes = result.get(date);
        List<TimeWithLabel> timesWithLabels = new ArrayList<>();
        for(String availableTime: availableTimes){
            LocalTime time = LocalTime.parse(availableTime);
            timesWithLabels.add(new TimeWithLabel(availableTime, time.format(DateTimeFormatter.ofPattern("h:mm a"))));
        }

        Map<String, Object> map = new HashMap<>();
        map.put("times", timesWithLabels);
        map.put("date", date);
        return map;
    }
}
