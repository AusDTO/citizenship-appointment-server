package au.gov.dto.dibp.appointments.availabledates;

import au.gov.dto.dibp.appointments.availabletimes.AvailableTimesService;
import au.gov.dto.dibp.appointments.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
public class AvailableDatesController {

    private final AvailableDatesService availableDatesService;
    private final AvailableTimesService availableTimesService;

    @Autowired
    public AvailableDatesController(AvailableDatesService availableDatesService, AvailableTimesService availableTimesService) {
        this.availableDatesService = availableDatesService;
        this.availableTimesService = availableTimesService;
    }

    @RequestMapping(value = "/get_available_dates.json", method = RequestMethod.GET, produces = "application/json")
    public Map<String, AvailableDate> getAvailableDates(@AuthenticationPrincipal Client client) {
        AvailableDates availableDates = availableDatesService.getAvailabilityForNextYear(client.getServiceId());
        String today = availableDates.getToday().format(DateTimeFormatter.ISO_LOCAL_DATE);
        AvailableDate availableDateToday = availableDates.getAvailableDates().get(today);
        if (availableDateToday != null) {
            String todayCalendarId = availableDateToday.getId();
            List<String> availableTimesToday = availableTimesService.getAvailableTimes(client, todayCalendarId);
            availableDateToday.setAvailableTimes(availableTimesToday);
        }
        return availableDates.getAvailableDates();
    }

    @RequestMapping(value = "/get_available_dates_for_plaintext.json", method = RequestMethod.GET, produces = "application/json")
    public List<AvailableDate> getAvailableDatesForPlaintext(@AuthenticationPrincipal Client client) {
        AvailableDates availableDates = availableDatesService.getAvailabilityForNextYear(client.getServiceId());
        List<AvailableDate> availableDateArray = new ArrayList<>(availableDates.getAvailableDates().values());
        Collections.sort(availableDateArray, (c1, c2) -> c2.getCalendarDate().compareTo(c2.getCalendarDate()));
        return availableDateArray;
    }
}
