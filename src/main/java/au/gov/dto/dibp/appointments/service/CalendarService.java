package au.gov.dto.dibp.appointments.service;

import au.gov.dto.dibp.appointments.model.CalendarEntry;
import au.gov.dto.dibp.appointments.model.Customer;
import au.gov.dto.dibp.appointments.service.api.GetCalendarsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class CalendarService {

    @Autowired
    private GetCalendarsService getCalendarsService;

    public List<CalendarEntry> getAvailabilityForNextYear() {
        Customer principal = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        //TODO: Dates according to the timezone of the unit!
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusYears(1L);

        try {
            return getCalendarsService.getCalendars(principal.getServiceId(), today, endDate);
        } catch (ParserConfigurationException | SAXException | IOException | XPathExpressionException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
