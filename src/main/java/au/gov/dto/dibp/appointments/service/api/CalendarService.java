package au.gov.dto.dibp.appointments.service.api;

import au.gov.dto.dibp.appointments.model.CalendarEntry;
import au.gov.dto.dibp.appointments.model.Client;
import au.gov.dto.dibp.appointments.util.NodeParser;
import au.gov.dto.dibp.appointments.util.ResponseParser;
import com.squareup.okhttp.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@Service
public class CalendarService {

    @Autowired
    private ApiCallsSenderService senderService;

    @Value("${SERVICE.ADDRESS.SERVICE}")
    private String SERVICE_ADDRESS_SERVICE;

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

    public SortedMap<String, CalendarEntry> getAvailabilityForNextYear(Client client) {
        //TODO: Dates according to the timezone of the unit!
        LocalDate today =  LocalDate.now(ZoneId.of("Australia/Sydney"));
        LocalDate endDate = today.plusYears(1L);

        try {
            return this.getCalendars(client.getServiceId(), today, endDate);
        } catch (ParserConfigurationException | SAXException | IOException | XPathExpressionException e) {
            throw new RuntimeException("Error when retrieving calendar for serviceId " + client.getServiceId(), e);
        }
    }

    public SortedMap<String, CalendarEntry> getCalendars(String serviceId, LocalDate startDate, LocalDate endDate) throws ParserConfigurationException, SAXException, XPathExpressionException, IOException {
        Map<String, String> data = new HashMap<>();
        data.put("serviceId", serviceId);
        data.put("startDate", startDate.toString()+"T00:00:00");
        data.put("endDate", endDate.toString()+"T00:00:00");

        Response response = senderService.sendRequest(GetCalendars.REQUEST_TEMPLATE_PATH, data, SERVICE_ADDRESS_SERVICE);
        return parseGetCalendarsResponse(response);
    }

    private SortedMap<String, CalendarEntry> parseGetCalendarsResponse(Response response) throws IOException, SAXException, ParserConfigurationException, XPathExpressionException {
        ResponseParser parser = new ResponseParser(response.body().byteStream());
        SortedMap<String, CalendarEntry> calendarEntries = new TreeMap<>();

        NodeList calendarNodes = parser.getNodeListAttribute(GetCalendars.CALENDARS);

        for(int i=0; i < calendarNodes.getLength(); i++) {
            CalendarEntry newCalendarEntry = getCalendarEntryDetails(calendarNodes.item(i));
            calendarEntries.put(newCalendarEntry.getCalendarDate(), newCalendarEntry);
        }

        return calendarEntries;
    }

    private CalendarEntry getCalendarEntryDetails(Node calendarNode) throws XPathExpressionException {
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
