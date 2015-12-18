package au.gov.dto.dibp.appointments.controller;

import au.gov.dto.dibp.appointments.model.CalendarEntry;
import au.gov.dto.dibp.appointments.model.Client;
import au.gov.dto.dibp.appointments.service.CalendarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
public class CalendarController {

    @Autowired
    private CalendarService calendarService;

    @RequestMapping(value = "/getAvailableDates", method = RequestMethod.GET, produces = "application/json")
    public Map<String, CalendarEntry> getAvailableDates(@AuthenticationPrincipal Client client) {
        return calendarService.getAvailabilityForNextYear(client).stream().collect(Collectors.toMap(CalendarEntry::getCalendarDate,
                Function.identity()));
    }

}
