package au.gov.dto.dibp.appointments.service.api;

import au.gov.dto.dibp.appointments.model.CalendarEntry;
import au.gov.dto.dibp.appointments.util.NodeParser;
import au.gov.dto.dibp.appointments.util.ResponseWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@Service
public class CalendarService {

    private final ApiCallsSenderService senderService;
    private final String serviceAddressService;

    @Autowired
    public CalendarService(ApiCallsSenderService senderService, @Value("${SERVICE.ADDRESS.SERVICE}") String serviceAddressService) {
        this.senderService = senderService;
        this.serviceAddressService = serviceAddressService;
    }

    private class GetCalendars {
        static final String REQUEST_TEMPLATE_PATH = "GetCalendars.mustache";

        static final String CALENDARS = "//GetCalendarsResponse/GetCalendarsResult/Calendar";
        static final String CALENDAR_DATE = "CalendarDate";
        static final String CALENDAR_ID = "Id";
        static final String VACANT_SLOTS_AFTERNOON = "VacantSlotsAfternoon";
        static final String VACANT_SLOTS_EVENING = "VacantSlotsEvening";
        static final String VACANT_SLOTS_MORNING = "VacantSlotsMorning";
        static final String VACANT_SLOTS_NIGHT = "VacantSlotsNight";
        static final String VACANT_SLOTS_NOON = "VacantSlotsNoon";
    }

    public SortedMap<String, CalendarEntry> getAvailabilityForNextYear(String serviceId) {
        //TODO: Dates according to the timezone of the unit!
        LocalDate today =  LocalDate.now(ZoneId.of("Australia/Sydney"));
        LocalDate endDate = today.plusYears(1L);

        return this.getCalendars(serviceId, today, endDate);
    }

    public SortedMap<String, CalendarEntry> getCalendars(String serviceId, LocalDate startDate, LocalDate endDate) {
        Map<String, String> data = new HashMap<>();
        data.put("serviceId", serviceId);
        data.put("startDate", startDate.toString()+"T00:00:00");
        data.put("endDate", endDate.toString()+"T00:00:00");

        ResponseWrapper response = senderService.sendRequest(GetCalendars.REQUEST_TEMPLATE_PATH, data, serviceAddressService);
        return parseGetCalendarsResponse(response);
    }

    private SortedMap<String, CalendarEntry> parseGetCalendarsResponse(ResponseWrapper response) {
        SortedMap<String, CalendarEntry> calendarEntries = new TreeMap<>();

        NodeList calendarNodes = response.getNodeListAttribute(GetCalendars.CALENDARS);

        for(int i=0; i < calendarNodes.getLength(); i++) {
            CalendarEntry newCalendarEntry = getCalendarEntryDetails(calendarNodes.item(i));
            calendarEntries.put(newCalendarEntry.getCalendarDate(), newCalendarEntry);
        }

        return calendarEntries;
    }

    private CalendarEntry getCalendarEntryDetails(Node calendarNode) {
        NodeParser nodeParser = new NodeParser(calendarNode);

        String calendarDate = nodeParser.getStringAttribute(GetCalendars.CALENDAR_DATE);
        String id = nodeParser.getStringAttribute(GetCalendars.CALENDAR_ID);
        int vacantSlotsAfternoon = nodeParser.getIntegerAttribute(GetCalendars.VACANT_SLOTS_AFTERNOON);
        int vacantSlotsEvening = nodeParser.getIntegerAttribute(GetCalendars.VACANT_SLOTS_EVENING);
        int vacantSlotsMorning = nodeParser.getIntegerAttribute(GetCalendars.VACANT_SLOTS_MORNING);
        int vacantSlotsNight = nodeParser.getIntegerAttribute(GetCalendars.VACANT_SLOTS_NIGHT);
        int vacantSlotsNoon = nodeParser.getIntegerAttribute(GetCalendars.VACANT_SLOTS_NOON);

        int vacantSlotsTotal = vacantSlotsAfternoon + vacantSlotsEvening + vacantSlotsMorning + vacantSlotsNight + vacantSlotsNoon;

        return new CalendarEntry(id, calendarDate.substring(0, calendarDate.indexOf('T')), vacantSlotsTotal);
    }
}
