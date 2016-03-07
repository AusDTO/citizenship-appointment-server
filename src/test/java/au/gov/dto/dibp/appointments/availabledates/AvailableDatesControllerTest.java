package au.gov.dto.dibp.appointments.availabledates;

import au.gov.dto.dibp.appointments.client.Client;
import au.gov.dto.dibp.appointments.organisation.TimeZoneDictionaryForTests;
import au.gov.dto.dibp.appointments.organisation.UnitDetailsService;
import au.gov.dto.dibp.appointments.util.FakeTemplateLoader;
import au.gov.dto.dibp.appointments.util.ResponseWrapper;
import com.samskivert.mustache.Template;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class AvailableDatesControllerTest {

    private AvailableDatesController controller;

    @Test
    public void testGetAvailableDatesForPlaintext_should_returnResultInDatesOrder() throws Exception {
        controller = new AvailableDatesController(getDatesService(), null);
        final List<AvailableDate> result = controller.getAvailableDatesForPlaintext(getStandardClient());

        assertThat(result.get(0).getCalendarDate(), is("2015-12-16"));
        assertThat(result.get(1).getCalendarDate(), is("2016-02-15"));
        assertThat(result.get(2).getCalendarDate(), is("2016-03-18"));
        assertThat(result.get(3).getCalendarDate(), is("2016-12-16"));
    }

    @Test
    public void testGetAvailableDatesForPlaintext_should_returnObjectsWithAllFields() throws Exception {
        controller = new AvailableDatesController(getDatesService(), null);
        final List<AvailableDate> result = controller.getAvailableDatesForPlaintext(getStandardClient());

        final AvailableDate availableDate = result.get(3);
        assertThat(availableDate.getCalendarDate(), is("2016-12-16"));
        assertThat(availableDate.getAvailableTimesCount(), is(12));
        assertThat(availableDate.getDisplayDate(), is("Friday 16 December 2016"));
        assertThat(availableDate.getId(), is("192"));
    }

    private AvailableDatesService getDatesService() {
        return new AvailableDatesService(
                (Template requestTemplate, Map<String, String> messageParams, String serviceAddress) -> getStandardResponse(),
                new UnitDetailsService(null, null, new TimeZoneDictionaryForTests(), new FakeTemplateLoader(), null){
                    @Override
                    public LocalDateTime getUnitCurrentLocalTimeByServiceId(String serviceId){
                        return LocalDateTime.parse("2015-11-02T12:12:12");
                    }
                },
                new FakeTemplateLoader(),
                "serviceUrl");
    }

    private Client getStandardClient (){
        return new Client("123", "Surname", "40404", false, false, "1212", "5", "3", true);
    }

    private ResponseWrapper getStandardResponse() {
        String response =
        "<s:Body>\n" +
        "      <GetCalendarsResponse xmlns=\"http://www.qnomy.com/Services\">\n" +
        "         <GetCalendarsResult xmlns:b=\"http://schemas.datacontract.org/2004/07/QFlow.Library\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
        "            <b:Calendar>\n" +
        "               <b:CalendarDate>2016-12-16T00:00:00</b:CalendarDate>\n" +
        "               <b:Id>192</b:Id>\n" +
        "               <b:Status>Active</b:Status>\n" +
        "               <b:VacantSlotsAfternoon>2</b:VacantSlotsAfternoon>\n" +
        "               <b:VacantSlotsEvening>1</b:VacantSlotsEvening>\n" +
        "               <b:VacantSlotsMorning>3</b:VacantSlotsMorning>\n" +
        "               <b:VacantSlotsNight>0</b:VacantSlotsNight>\n" +
        "               <b:VacantSlotsNoon>6</b:VacantSlotsNoon>\n" +
        "            </b:Calendar>\n" +
        "            <b:Calendar>\n" +
        "               <b:CalendarDate>2016-02-15T00:00:00</b:CalendarDate>\n" +
        "               <b:Id>193</b:Id>\n" +
        "               <b:Status>Active</b:Status>\n" +
        "               <b:VacantSlotsAfternoon>2</b:VacantSlotsAfternoon>\n" +
        "               <b:VacantSlotsEvening>2</b:VacantSlotsEvening>\n" +
        "               <b:VacantSlotsMorning>0</b:VacantSlotsMorning>\n" +
        "               <b:VacantSlotsNight>0</b:VacantSlotsNight>\n" +
        "               <b:VacantSlotsNoon>6</b:VacantSlotsNoon>\n" +
        "            </b:Calendar>\n" +
        "            <b:Calendar>\n" +
        "               <b:CalendarDate>2016-03-18T00:00:00</b:CalendarDate>\n" +
        "               <b:Id>194</b:Id>\n" +
        "               <b:Status>Active</b:Status>\n" +
        "               <b:VacantSlotsAfternoon>1</b:VacantSlotsAfternoon>\n" +
        "               <b:VacantSlotsEvening>0</b:VacantSlotsEvening>\n" +
        "               <b:VacantSlotsMorning>3</b:VacantSlotsMorning>\n" +
        "               <b:VacantSlotsNight>0</b:VacantSlotsNight>\n" +
        "               <b:VacantSlotsNoon>5</b:VacantSlotsNoon>\n" +
        "            </b:Calendar>\n" +
        "            <b:Calendar>\n" +
        "               <b:CalendarDate>2015-12-16T00:00:00</b:CalendarDate>\n" +
        "               <b:Id>195</b:Id>\n" +
        "               <b:Status>Active</b:Status>\n" +
        "               <b:VacantSlotsAfternoon>0</b:VacantSlotsAfternoon>\n" +
        "               <b:VacantSlotsEvening>0</b:VacantSlotsEvening>\n" +
        "               <b:VacantSlotsMorning>0</b:VacantSlotsMorning>\n" +
        "               <b:VacantSlotsNight>0</b:VacantSlotsNight>\n" +
        "               <b:VacantSlotsNoon>2</b:VacantSlotsNoon>\n" +
        "            </b:Calendar>\n" +
        "         </GetCalendarsResult>\n" +
        "      </GetCalendarsResponse>\n" +
        "   </s:Body>";
        return new ResponseWrapper(200, response);
    }
}
