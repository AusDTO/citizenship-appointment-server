package au.gov.dto.dibp.appointments.calendar;

import au.gov.dto.dibp.appointments.appointmentdetails.AppointmentDetails;
import au.gov.dto.dibp.appointments.appointmentdetails.AppointmentDetailsService;
import au.gov.dto.dibp.appointments.client.Client;
import au.gov.dto.dibp.appointments.organisation.TimeZoneDictionaryForTests;
import au.gov.dto.dibp.appointments.organisation.UnitDetailsService;
import au.gov.dto.dibp.appointments.util.FakeTemplateLoader;
import au.gov.dto.dibp.appointments.util.TemplateLoader;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
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
    private static final String UNIT_ADDRESS_WITH_OFFICE = "Department+of+Immigration+and+Border+Protection%2C+Some+Street+12%2C+Melbourne";

    private CalendarController controller;

    @Before
    public void setUp() throws Exception {
        TemplateLoader templateLoader = new FakeTemplateLoader();

        controller = new CalendarController(new AppointmentDetailsService(null, null, templateLoader, null) {
            @Override
            public AppointmentDetails getExpectedAppointmentForClientForNextYear(Client client) {
                return getBasicAppointmentDetails();
            }
        }, new UnitDetailsService(null, null, new TimeZoneDictionaryForTests(), templateLoader, null) {

            @Override
            public LocalDateTime getUnitCurrentLocalTime(String unitId) {
                assertThat(unitId, is(UNIT_ID));
                return LocalDateTime.parse(UNIT_LOCALTIME);
            }

            @Override
            public String getUnitAddress(String unitId) {
                assertThat(unitId, is(UNIT_ID));
                return UNIT_ADDRESS;
            }
        });
    }

    @Test
    public void test_bookingHtml_should_passTheLocationToTheModel() throws Exception {
        final ModelAndView result = controller.bookingHtml(getStandardClient(), null, null, null, mock(HttpServletRequest.class));

        assertThat(result.getModel().get("location"), is(UNIT_ADDRESS));
    }

    @Test
    public void test_bookingHtml_should_passTheLocatinoUrlToTheModel() throws Exception {
        ModelAndView modelAndView = controller.bookingHtml(getStandardClient(), null, null, null, new MockHttpServletRequest());

        assertThat(modelAndView.getModel().get("locationURL"), is(UNIT_ADDRESS_WITH_OFFICE));
    }

    @Test
    public void test_bookingHtml_should_passTodayDateToTheModel() throws Exception {
        final ModelAndView result = controller.bookingHtml(getStandardClient(), null, null, null, mock(HttpServletRequest.class));

        assertThat(result.getModel().get("today_date"), is(UNIT_LOCALTIME));
    }

    @Test
    public void test_bookingHtml_should_passCurrentUserAppointmentToTheModel() throws Exception {
        final ModelAndView result = controller.bookingHtml(getStandardClient(), null, null, null, mock(HttpServletRequest.class));

        assertThat(result.getModel().get("current_appointment"), is("Monday 18 January, 1:20 PM"));
    }

    @Test
    public void test_bookingHtml_should_passErrorToTheModelIfPresent() throws Exception {
        final ModelAndView result = controller.bookingHtml(getStandardClient(), "Some error", null, null, mock(HttpServletRequest.class));

        assertThat(result.getModel().get("error"), is(true));
    }

    @Test
    public void test_bookingHtml_should_notPopulateErrorInTheModelIfNotPresent() throws Exception {
        final ModelAndView result = controller.bookingHtml(getStandardClient(), null, null, null, mock(HttpServletRequest.class));

        assertThat(result.getModel().get("error"), is(nullValue()));
    }

    @Test
    public void test_bookingHtml_should_passUnavailableErrorToTheModelIfPresent() throws Exception {
        final ModelAndView result = controller.bookingHtml(getStandardClient(), null, "Some error", null, mock(HttpServletRequest.class));

        assertThat(result.getModel().get("unavailable"), is(true));
    }

    @Test
    public void test_bookingHtml_should_passUserNotEligibleErrorToTheModelIfPresent() throws Exception {
        final ModelAndView result = controller.bookingHtml(getStandardClient(), null, null, "some error", mock(HttpServletRequest.class));

        assertThat(result.getModel().get("not_eligible"), is(true));
    }

    @Test
    public void test_bookingHtml_should_notPopulateUnavailableErrorInTheModelIfNotPresent() throws Exception {
        final ModelAndView result = controller.bookingHtml(getStandardClient(), null, null, null, mock(HttpServletRequest.class));

        assertThat(result.getModel().get("unavailable"), is(nullValue()));
    }

    private Client getStandardClient() {
        return new Client("123", "Surname", "40404", true, false, UNIT_ID, SERVICE_ID, "3", true);
    }

    private AppointmentDetails getBasicAppointmentDetails() {
        return new AppointmentDetails(LocalDateTime.parse(CURRENT_APPOINTMENT_TIME), 20, "121212", SERVICE_ID, "3333", "Sydney", "Some Street 12", "Australia/Sydney");
    }
}
