package au.gov.dto.dibp.appointments.availabledates;

import au.gov.dto.dibp.appointments.organisation.UnitDetailsService;
import au.gov.dto.dibp.appointments.qflowintegration.ApiCallsSenderService;
import au.gov.dto.dibp.appointments.util.NodeParser;
import au.gov.dto.dibp.appointments.util.ResponseWrapper;
import au.gov.dto.dibp.appointments.util.TemplateLoader;
import com.samskivert.mustache.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@Service
class AvailableDatesService {

    private final ApiCallsSenderService senderService;
    private final UnitDetailsService unitDetailsService;
    private final String serviceAddressService;

    private final Template getCalendarsTemplate;

    @Autowired
    public AvailableDatesService(ApiCallsSenderService senderService,
                                 UnitDetailsService unitDetailsService,
                                 TemplateLoader templateLoader,
                                 @Value("${SERVICE.ADDRESS.SERVICE}") String serviceAddressService) {
        this.senderService = senderService;
        this.unitDetailsService = unitDetailsService;
        this.serviceAddressService = serviceAddressService;

        this.getCalendarsTemplate = templateLoader.loadTemplateByPath(GetCalendars.REQUEST_TEMPLATE_PATH);
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

    public AvailableDates getAvailabilityForNextYear(String serviceId) {
        LocalDate today =  unitDetailsService.getUnitCurrentLocalTimeByServiceId(serviceId).toLocalDate();
        LocalDate endDate = today.plusYears(1L);

        Map<String, AvailableDate> availabilityForDateRange = getAvailabilityForDateRange(serviceId, today, endDate);
        return new AvailableDates(today, availabilityForDateRange);
    }

    SortedMap<String, AvailableDate> getAvailabilityForDateRange(String serviceId, LocalDate startDate, LocalDate endDate) {
        Map<String, String> data = new HashMap<>();
        data.put("serviceId", serviceId);
        data.put("startDate", startDate.toString()+"T00:00:00");
        data.put("endDate", endDate.toString()+"T00:00:00");

        ResponseWrapper response = senderService.sendRequest(this.getCalendarsTemplate, data, serviceAddressService);
        return parseGetCalendarsResponse(response);
    }

    private SortedMap<String, AvailableDate> parseGetCalendarsResponse(ResponseWrapper response) {
        SortedMap<String, AvailableDate> availableDates = new TreeMap<>();

        NodeList calendarNodes = response.getNodeListAttribute(GetCalendars.CALENDARS);

        for(int i=0; i < calendarNodes.getLength(); i++) {
            AvailableDate newAvailableDate = getAvailableDateDetails(calendarNodes.item(i));
            availableDates.put(newAvailableDate.getCalendarDate(), newAvailableDate);
        }

        return availableDates;
    }

    private AvailableDate getAvailableDateDetails(Node calendarNode) {
        NodeParser nodeParser = new NodeParser(calendarNode);

        String calendarDate = nodeParser.getStringAttribute(GetCalendars.CALENDAR_DATE);
        String id = nodeParser.getStringAttribute(GetCalendars.CALENDAR_ID);
        int vacantSlotsAfternoon = nodeParser.getIntegerAttribute(GetCalendars.VACANT_SLOTS_AFTERNOON);
        int vacantSlotsEvening = nodeParser.getIntegerAttribute(GetCalendars.VACANT_SLOTS_EVENING);
        int vacantSlotsMorning = nodeParser.getIntegerAttribute(GetCalendars.VACANT_SLOTS_MORNING);
        int vacantSlotsNight = nodeParser.getIntegerAttribute(GetCalendars.VACANT_SLOTS_NIGHT);
        int vacantSlotsNoon = nodeParser.getIntegerAttribute(GetCalendars.VACANT_SLOTS_NOON);

        int vacantSlotsTotal = vacantSlotsAfternoon + vacantSlotsEvening + vacantSlotsMorning + vacantSlotsNight + vacantSlotsNoon;

        return new AvailableDate(id, calendarDate.substring(0, calendarDate.indexOf('T')), vacantSlotsTotal);
    }
}
