package au.gov.dto.dibp.appointments.calendar;

import au.gov.dto.dibp.appointments.appointmentdetails.AppointmentDetails;
import au.gov.dto.dibp.appointments.appointmentdetails.AppointmentDetailsService;
import au.gov.dto.dibp.appointments.availabledates.AvailableDate;
import au.gov.dto.dibp.appointments.availabledates.AvailableDatesController;
import au.gov.dto.dibp.appointments.availabletimes.AvailableTimesController;
import au.gov.dto.dibp.appointments.availabletimes.TimeWithLabel;
import au.gov.dto.dibp.appointments.client.Client;
import au.gov.dto.dibp.appointments.organisation.UnitDetailsService;
import au.gov.dto.dibp.appointments.util.CalendarIdValidator;
import au.gov.dto.dibp.appointments.util.FakeTemplateLoader;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpUpgradeHandler;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class PlaintextCalendarControllerTest {

    public static final String UNIT_ADDRESS = "23 ABC Road, Someplace";
    public static final String UNIT_ADDRESS_URL = "23+ABC+Road%2C+Someplace";
    public static final String CURRENT_APPOINTMENT = "Tuesday 02 February, 1:00 PM";

    private PlaintextCalendarController controller;

    @Before
    public void setUp(){
        controller = new PlaintextCalendarController(
                getAvailableDatesController(),
                getAvailableTimesController(),
                getAppointmentDetailsService(),
                getUnitDetailsService(),
                new CalendarIdValidator()
        );
    }

    @Test
    public void test_getPlaintextCalendar_should_containAllNecessaryModelAttributes(){
        final ModelAndView modelAndView = controller.getPlaintextCalendar(getStandardClient(), "Some error", null);

        final Map<String, Object> model = modelAndView.getModel();
        assertThat(model.get("location"), is(UNIT_ADDRESS));
        assertThat(model.get("locationURL"), is(UNIT_ADDRESS_URL));
        assertThat(model.get("current_appointment"), is(CURRENT_APPOINTMENT));
        assertThat(model.get("error"), is(true));

        final List<AvailableDate> available_dates = (List<AvailableDate>) model.get("available_dates");
        assertThat(available_dates.size(), is(5));

        final AvailableDate availableDate = available_dates.get(0);
        assertThat(availableDate.getDisplayDate(), is("Monday 04 April 2016"));
        assertThat(availableDate.getAvailableTimesCount(), is(12));
        assertThat(availableDate.getId(), is("1312"));
        assertThat(availableDate.getCalendarDate(), is("2016-04-04"));
        assertThat(availableDate.getAvailableTimes(), is(nullValue()));
    }

    @Test
    public void test_getPlaintextCalendar_should_returnCorrectView(){
        final ModelAndView modelAndView = controller.getPlaintextCalendar(getStandardClient(), null, null);
        assertThat(modelAndView.getViewName(), is("calendar_nojs"));
    }

    @Test
    public void test_getPlaintextTimes_should_containAllNecessaryModelAttributes(){
        final ModelAndView modelAndView = controller.getPlaintextTimes(getStandardClient(), "1212", null, null);

        final Map<String, Object> model = modelAndView.getModel();
        assertThat(model.get("location"), is(UNIT_ADDRESS));
        assertThat(model.get("locationURL"), is(UNIT_ADDRESS_URL));
        assertThat(model.get("current_appointment"), is(CURRENT_APPOINTMENT));
        assertThat(model.get("error"), is(nullValue()));

        final List<TimeWithLabel> available_times = (List<TimeWithLabel>) model.get("available_times");
        assertThat(available_times.size(), is(3));
        TimeWithLabel availableTime = available_times.get(0);
        assertThat(availableTime.getDisplayTime(), is("10:00 AM"));
        assertThat(availableTime.getTime(), is("10:00"));

        availableTime = available_times.get(2);
        assertThat(availableTime.getDisplayTime(), is("3:00 PM"));
        assertThat(availableTime.getTime(), is("15:00"));


        assertThat(model.get("display_date"), is("Wednesday 02 March 2016"));
        assertThat(model.get("date"), is("2016-03-02"));
    }

    @Test
    public void test_getPlaintextTimes_should_returnCorrectView(){
        final ModelAndView modelAndView = controller.getPlaintextTimes(getStandardClient(), "1212", null, null);
        assertThat(modelAndView.getViewName(), is("daytimes_nojs"));
    }

    @Test(expected = RuntimeException.class)
    public void test_getPlaintextTimes_should_throwAnExceptionIdInvalidCalendarIdIsProvided(){
        controller.getPlaintextTimes(getStandardClient(), "AA", null, null);
        fail();
    }

    @Test
    public void test_getPlaintextSelectionConfirmation_should_containAllNecessaryModelAttributes(){
        final ModelAndView modelAndView = controller.getPlaintextSelectionConfirmation(getStandardClient(), "2016-01-01", "12:00", null, new MockHttpServletRequest(){
            @Override
            public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) throws IOException, ServletException {
                return null;
            }

            @Override
            public Object getAttribute(String name) {
                if(CsrfToken.class.getName().equals(name)){
                    return new CsrfToken() {
                        @Override
                        public String getHeaderName() {
                            return null;
                        }

                        @Override
                        public String getParameterName() {
                            return null;
                        }

                        @Override
                        public String getToken() {
                            return null;
                        }
                    };
                }
                return super.getAttribute(name);
            }
        });

        final Map<String, Object> model = modelAndView.getModel();
        assertThat(model.get("location"), is(UNIT_ADDRESS));
        assertThat(model.get("locationURL"), is(UNIT_ADDRESS_URL));
        assertThat(model.get("current_appointment"), is(CURRENT_APPOINTMENT));
        assertThat(model.get("error"), is(nullValue()));

        assertThat(model.get("selected_appointment"), is("2016-01-01T12:00:00"));
        assertThat(model.get("display_appointment_datetime"), is("Friday 01 January, 12:00 PM"));
        assertThat(model.get("_csrf"), is(not(nullValue())));
    }

    @Test
    public void test_getPlaintextSelectionConfirmation_should_returnCorrectView(){
        final ModelAndView modelAndView = controller.getPlaintextSelectionConfirmation(getStandardClient(), "2016-01-01", "12:00", null, new MockHttpServletRequest());
        assertThat(modelAndView.getViewName(), is("selection_nojs"));
    }

    @Test(expected = RuntimeException.class)
    public void test_getPlaintextSelectionConfirmation_should_throwAnExceptionWhenInvalidDateFormatIsProvided(){
        controller.getPlaintextSelectionConfirmation(getStandardClient(), "Script", "12:00", null, new MockHttpServletRequest());
        fail();
    }

    @Test(expected = RuntimeException.class)
    public void test_getPlaintextSelectionConfirmation_should_throwAnExceptionWhenInvalidTimeFormatIsProvided(){
        controller.getPlaintextSelectionConfirmation(getStandardClient(), "2016-01-01", "Script", null, new MockHttpServletRequest());
        fail();
    }

    private Client getStandardClient (){
        return new Client("123", "Surname", "40404", false, false, "1212", "5", "3", true);
    }

    private AvailableDatesController getAvailableDatesController(){
        return new AvailableDatesController(null, null){
            @Override
            public List<AvailableDate> getAvailableDatesForPlaintext(Client client) {
                return Arrays.asList(new AvailableDate("1312", "2016-04-04", 12),
                        new AvailableDate("1312", "2016-04-05", 1),
                        new AvailableDate("1412", "2016-04-06", 4),
                        new AvailableDate("1512", "2016-04-07", 23),
                        new AvailableDate("1112", "2016-04-08", 5));
            }
        };
    }

    private AvailableTimesController getAvailableTimesController(){
        return new AvailableTimesController(null, null){
            @Override
            public Map<String, Object> getAvailableTimesWithLabel(Client client, String calendarId) {
                Map<String, Object> result = new HashMap<>();
                result.put("date", "2016-03-02T00:00:00");
                result.put("times", Arrays.asList(new TimeWithLabel("10:00", "10:00 AM"),
                        new TimeWithLabel("12:00", "12:00 PM"),
                        new TimeWithLabel("15:00", "3:00 PM")));
                return result;
            }
        };
    }

    private AppointmentDetailsService getAppointmentDetailsService(){
        return new AppointmentDetailsService(null, null, new FakeTemplateLoader(), ""){
            @Override
            public AppointmentDetails getExpectedAppointmentForClientForNextYear(Client client) {
                return new AppointmentDetails(LocalDateTime.parse("2016-02-02T13:00:00"),
                        20, "1", "1", "11111", "Some unit", "3939 Street, Place", "51");
                }
        };
    }

    private UnitDetailsService getUnitDetailsService(){
        return new UnitDetailsService(null, null, null, new FakeTemplateLoader(), ""){
            @Override
            public String getUnitAddress(String unitId){
                return UNIT_ADDRESS;
            }
        };
    }
}
