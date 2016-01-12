package au.gov.dto.dibp.appointments.booking;

import au.gov.dto.dibp.appointments.client.Client;
import org.junit.Test;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class BookingControllerTest {

    @Test
    public void test_bookAnAppointment_should_redirectToConfirmationPageIfBookedSuccessfully(){
        final String bookedDate = "2015-12-30T13:00:00";

        BookingController controller = new BookingController(new BookingService(null, null, null, null){
            public String bookAnAppointment(Client client, LocalDateTime appointmentTime) {
                return bookedDate;
            }
        });

        ModelAndView modelViewResult = controller.bookAnAppointment(getStandardClient(), "2015-12-30T13:00:00");
        assertThat(modelViewResult.getViewName(), is("redirect:/confirmation"));
        assertThat(modelViewResult.getModel(), is(Collections.emptyMap()));
    }

    private Client getStandardClient (){
        return new Client("123", "Surname", "40404", false,  "3", "5", "3", true);
    }

    @Test
    public void test_bookAnAppointment_should_redirectToBookingPageIfBookingFailed(){
        BookingController controller = new BookingController(new BookingService(null, null, null, null){
            public String bookAnAppointment(Client client, LocalDateTime appointmentTime) {
                throw new RuntimeException("Something failed");
            }
        });

        ModelAndView modelViewResult = controller.bookAnAppointment(getStandardClient(), "2015-12-30T13:00:00");
        assertThat(modelViewResult.getViewName(), is("redirect:/calendar?error"));
        assertThat(modelViewResult.getModel().get("bookedDateTime"), is(nullValue()));
    }
}
