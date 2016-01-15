package au.gov.dto.dibp.appointments.booking;

import au.gov.dto.dibp.appointments.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;

@RestController
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService){
        this.bookingService = bookingService;
    }

    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);

    @RequestMapping(value = "/book_appointment", method = RequestMethod.POST)
    public ModelAndView bookAnAppointment(@AuthenticationPrincipal Client client,
                                    @RequestParam(value="selected_appointment", required=true) String dateTime){
            LocalDateTime selectedAppointment = LocalDateTime.parse(dateTime);

            bookingService.bookAnAppointment(client, selectedAppointment);
            logger.info("Appointment booked for "+ client.getClientId() + " on "+ selectedAppointment);

            return new ModelAndView("redirect:/confirmation",  new HashMap<>());
    }

    @ExceptionHandler(BookingResponseInvalidException.class)
    public ModelAndView handleAppointmentNotBookedError(HttpServletRequest req, BookingResponseInvalidException exception){
        return new ModelAndView("redirect:/calendar?unavailable", new HashMap<>());
    }

    @ExceptionHandler(RuntimeException.class)
    public ModelAndView handleRuntimeException(HttpServletRequest req, RuntimeException exception){
        return new ModelAndView("redirect:/calendar?error", new HashMap<>());
    }
}
