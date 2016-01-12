package au.gov.dto.dibp.appointments.calendar;

import au.gov.dto.dibp.appointments.appointmentdetails.AppointmentDetails;
import au.gov.dto.dibp.appointments.appointmentdetails.AppointmentDetailsService;
import au.gov.dto.dibp.appointments.client.Client;
import au.gov.dto.dibp.appointments.organisation.UnitDetailsService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class CalendarControllerTest {

    private static final String SERVICE_ID = "5";
    private static final String UNIT_ID = "3";
    private static final String UNIT_ADDRESS = "Some Street 12, Melbourne";
    private static final String UNIT_LOCALTIME = "2016-01-10T11:20";
    private static final String CURRENT_APPOINTMENT_TIME = "2016-01-18T13:20:00";

    private CalendarController controller;

    @Before
    public void setUp(){
        controller = new CalendarController(new AppointmentDetailsService(null, null, null){
            @Override
            public AppointmentDetails getExpectedAppointmentForClientForNextYear(Client client) {
                return getBasicAppointmentDetails();
            }
        }, new UnitDetailsService(null, null, null){

            @Override
            public LocalDateTime getUnitCurrentLocalTime(String unitId){
                assertThat(unitId, is(UNIT_ID));
                return  LocalDateTime.parse(UNIT_LOCALTIME);
            }

            @Override
            public String getUnitAddress(String unitId){
                assertThat(unitId, is(UNIT_ID));
                return UNIT_ADDRESS;
            }
        });
    }

    @Test
    public void test_bookingHtml_should_passTheLocationToTheModel(){
        final ModelAndView result = controller.bookingHtml(getStandardClient(), null, mock(HttpServletRequest.class));

        assertThat(result.getModel().get("location"), is(UNIT_ADDRESS));
    }

    @Test
    public void test_bookingHtml_should_passTodayDateToTheModel(){
        final ModelAndView result = controller.bookingHtml(getStandardClient(), null, mock(HttpServletRequest.class));

        assertThat(result.getModel().get("todayDate"), is(UNIT_LOCALTIME));
    }

    @Test
    public void test_bookingHtml_should_passCurrentUserAppointmentToTheModel(){
        final ModelAndView result = controller.bookingHtml(getStandardClient(), null, mock(HttpServletRequest.class));

        assertThat(result.getModel().get("current_appointment"), is("Monday 18 January, 1:20 PM"));
    }

    @Test
    public void test_bookingHtml_should_passErrorToTheModelIfPresent(){
        final ModelAndView result = controller.bookingHtml(getStandardClient(), "Some error", mock(HttpServletRequest.class));

        assertThat(result.getModel().get("error"), is(true));
    }

    @Test
    public void test_bookingHtml_should_notPopulateErrorInTheModelIfNotPresent(){
        final ModelAndView result = controller.bookingHtml(getStandardClient(), null, mock(HttpServletRequest.class));

        assertThat(result.getModel().get("error"), is(nullValue()));
    }

    private Client getStandardClient (){
        return new Client("123", "Surname", "40404", true,  UNIT_ID, SERVICE_ID, "3", true);
    }

    private AppointmentDetails getBasicAppointmentDetails(){
        return new AppointmentDetails(LocalDateTime.parse(CURRENT_APPOINTMENT_TIME), 20, "121212", SERVICE_ID, "3333", "Sydney", "Some Street 12");
    }
}
