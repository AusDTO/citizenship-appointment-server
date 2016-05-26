package au.gov.dto.dibp.appointments.booking;

import au.gov.dto.dibp.appointments.client.Client;
import au.gov.dto.dibp.appointments.util.FakeTemplateLoader;
import org.junit.Test;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class BookingControllerTest {
    @Test
    public void test_bookAnAppointment_should_redirectToConfirmationPageIfBookedSuccessfully() {
        String bookedDate = "2015-12-30T13:00:00";

        BookingController controller = new BookingController(new BookingService(null, null, null, new FakeTemplateLoader(), null, null) {
            public String bookAnAppointment(Client client, LocalDateTime appointmentTime) {
                return bookedDate;
            }
        });

        Client client = new Client("clientId", "familyName", "customerId", true, true, "unitId", "serviceId", "appointmentTypeId", true);
        ModelAndView modelViewResult = controller.bookAnAppointment(client, "2015-12-30T13:00:00");
        assertThat(modelViewResult.getViewName(), is("redirect:/confirmation"));
        assertThat(modelViewResult.getModel(), is(Collections.emptyMap()));
    }
}
