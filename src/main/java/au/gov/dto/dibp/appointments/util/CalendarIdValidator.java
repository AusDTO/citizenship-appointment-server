package au.gov.dto.dibp.appointments.util;

import org.springframework.stereotype.Component;

@Component
public class CalendarIdValidator {
    public boolean isCalendarIdValid(String calendarId){
        return calendarId != null && calendarId.matches("[0-9]*");
    }
}
