package au.gov.dto.dibp.appointments.controller;

import au.gov.dto.dibp.appointments.model.CalendarEntry;
import au.gov.dto.dibp.appointments.model.Client;
import au.gov.dto.dibp.appointments.service.api.CalendarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class CalendarController {

    @Autowired
    private CalendarService calendarService;

    @RequestMapping(value = "/get_available_dates.json", method = RequestMethod.GET, produces = "application/json")
    public Map<String, CalendarEntry> getAvailableDates(@AuthenticationPrincipal Client client) {
        return calendarService.getAvailabilityForNextYear(client);
    }

}
