package au.gov.dto.dibp.appointments.confirmation;

import au.gov.dto.dibp.appointments.appointmentdetails.AppointmentDetails;
import au.gov.dto.dibp.appointments.appointmentdetails.AppointmentDetailsService;
import au.gov.dto.dibp.appointments.client.Client;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class ConfirmationControllerTest {

    private ConfirmationController controller;

    private static final String BOOKED_DATE = "2016-01-18T13:20:00";
    private static final String CLIENT_ID = "123";
    private static final String CUSTOMER_ID = "3333";
    private static final String SERVICE_ID = "AAA";
    private static final String UNTI_ADDRESS = "Some Street 12";
    private static final boolean HAS_EMAIL = true;

    @Before
    public void setUp(){
        controller = new ConfirmationController(new AppointmentDetailsService(null, null, null){
            @Override
            public AppointmentDetails getExpectedAppointmentForClientForNextYear(Client client) {
                return getBasicAppointmentDetails();
            }
        });
    }

    @Test
    public void test_getConfirmationPage_should_passTheLocationToTheModel(){
        final ModelAndView result = controller.getConfirmationPage(getStandardClient());

        assertThat(result.getModel().get("location"), is(UNTI_ADDRESS));
    }

    @Test
    public void test_getConfirmationPage_should_passTheOfficeLevelToTheModel(){
        final ModelAndView result = controller.getConfirmationPage(getStandardClient());

        assertThat(result.getModel().get("level"), is("Level 4"));
    }

    @Test
    public void test_getConfirmationPage_should_passTheClientIdToTheModel(){
        final ModelAndView result = controller.getConfirmationPage(getStandardClient());

        assertThat(result.getModel().get("clientId"), is(CLIENT_ID));
    }

    @Test
    public void test_getConfirmationPage_should_passIfUserHasEmailToTheModel(){
        final ModelAndView result = controller.getConfirmationPage(getStandardClient());

        assertThat(result.getModel().get("hasEmail"), is(HAS_EMAIL));
    }

    @Test
    public void test_getConfirmationPage_should_passTheAppointmentTimeToTheModel(){
        final ModelAndView result = controller.getConfirmationPage(getStandardClient());

        assertThat(result.getModel().get("selected_appointment"), is("Monday 18 January, 1:20 PM"));
    }

    @Test
    public void test_getConfirmationPage_should_setConfirmationPageAsTheView(){
        final ModelAndView result = controller.getConfirmationPage(getStandardClient());

        assertThat(result.getViewName(), is("confirmation_page"));
    }

    @Test
    public void test_getConfirmationPage_should_redirectToBookingPageIfNoAppointmentsFound(){
        controller = new ConfirmationController(new AppointmentDetailsService(null, null, null){
            @Override
            public AppointmentDetails getExpectedAppointmentForClientForNextYear(Client client) {
                return null;
            }
        });

        final ModelAndView result = controller.getConfirmationPage(getStandardClient());

        assertThat(result.getViewName(), is("redirect:/booking?error"));
    }

    private Client getStandardClient (){
        return new Client(CLIENT_ID, "Surname", "40404", HAS_EMAIL, true);
    }

    private AppointmentDetails getBasicAppointmentDetails(){
        return new AppointmentDetails(LocalDateTime.parse(BOOKED_DATE), 20, SERVICE_ID, CUSTOMER_ID, "Sydney", UNTI_ADDRESS);
    }
}
