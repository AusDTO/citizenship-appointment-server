package au.gov.dto.dibp.appointments.availabledates;

import au.gov.dto.dibp.appointments.qflowintegration.ApiCallsSenderService;
import au.gov.dto.dibp.appointments.util.ResponseWrapper;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Map;
import java.util.SortedMap;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class AvailableDatesServiceTest {

    @Test
    public void getCalendars_shouldConvertResponseIntoCalendarEntryObject() throws Exception {
        AvailableDatesService service = new AvailableDatesService(new ApiCallsSenderService() {
            @Override
            public ResponseWrapper sendRequest(String requestTemplatePath, Map<String, String> messageParams, String serviceAddress) {
                return getStandardResponse();
            }
        }, "serviceUrl");

        SortedMap<String, AvailableDate> calendarEntries = service.getAvailabilityForDateRange("serviceId", LocalDate.now(), LocalDate.now());

        AvailableDate availableDate = calendarEntries.get(calendarEntries.firstKey());

        assertThat(calendarEntries.size(), is(1));
        assertThat(availableDate.getId(), is("192"));
        assertThat(availableDate.getCalendarDate(), is("2015-12-16"));
        assertThat(availableDate.getAvailableTimesCount(), is(12));
    }

    // FIXME(Emily) test inactive entries are not included in response

    private ResponseWrapper getStandardResponse() {
        String response =
            "<s:Body>\n" +
            "      <GetCalendarsResponse xmlns=\"http://www.qnomy.com/Services\">\n" +
            "         <GetCalendarsResult xmlns:b=\"http://schemas.datacontract.org/2004/07/QFlow.Library\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
            "            <b:Calendar>\n" +
            "               <b:CalendarDate>2015-12-16T00:00:00</b:CalendarDate>\n" +
            "               <b:Id>192</b:Id>\n" +
            "               <b:Status>Active</b:Status>\n" +
            "               <b:VacantSlotsAfternoon>2</b:VacantSlotsAfternoon>\n" +
            "               <b:VacantSlotsEvening>1</b:VacantSlotsEvening>\n" +
            "               <b:VacantSlotsMorning>3</b:VacantSlotsMorning>\n" +
            "               <b:VacantSlotsNight>0</b:VacantSlotsNight>\n" +
            "               <b:VacantSlotsNoon>6</b:VacantSlotsNoon>\n" +
            "            </b:Calendar>\n" +
            "         </GetCalendarsResult>\n" +
            "      </GetCalendarsResponse>\n" +
            "   </s:Body>";
        return new ResponseWrapper(200, response);
    }

}
