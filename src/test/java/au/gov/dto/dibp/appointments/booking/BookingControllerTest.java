package au.gov.dto.dibp.appointments.booking;

import au.gov.dto.dibp.appointments.client.Client;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class BookingControllerTest {

    @Test
    public void test_bookAnAppointment_should_redirectToConfirmationPageIfBookedSuccessfully(){
        final String bookedDate = "2015-12-30T13:00:00";

        BookingController controller = new BookingController(new BookingService(null, null){
            public String bookAnAppointment(Client client, LocalDateTime appointmentTime) {
                return bookedDate;
            }
        });

        ModelAndView modelViewResult = controller.bookAnAppointment(getStandardClient(), "2015-12-30", "1:00PM");
        assertThat(modelViewResult.getViewName(), is("redirect:/confirmation"));
        assertThat(modelViewResult.getModel().get("bookedDateTime"), is(bookedDate));
    }

    private Client getStandardClient (){
        return new Client("123", "Surname", "40404", false, true);
    }

    @Test
    public void test_bookAnAppointment_should_redirectToBookingPageIfBookingFailed(){
        BookingController controller = new BookingController(new BookingService(null, null){
            public String bookAnAppointment(Client client, LocalDateTime appointmentTime) {
                throw new RuntimeException("Something failed");
            }
        });

        ModelAndView modelViewResult = controller.bookAnAppointment(getStandardClient(), "2015-12-30", "1:00PM");
        assertThat(modelViewResult.getViewName(), is("redirect:/booking?error"));
        assertThat(modelViewResult.getModel().get("bookedDateTime"), is(nullValue()));
    }
}
