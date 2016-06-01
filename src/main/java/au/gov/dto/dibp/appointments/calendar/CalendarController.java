package au.gov.dto.dibp.appointments.calendar;

import au.gov.dto.dibp.appointments.appointmentdetails.AppointmentDetails;
import au.gov.dto.dibp.appointments.appointmentdetails.AppointmentDetailsService;
import au.gov.dto.dibp.appointments.client.Client;
import au.gov.dto.dibp.appointments.organisation.UnitDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
public class CalendarController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CalendarController.class);
    private final AppointmentDetailsService appointmentDetailsService;
    private final UnitDetailsService unitDetailsService;

    @Autowired
    public CalendarController(AppointmentDetailsService appointmentDetailsService,
                              UnitDetailsService unitDetailsService){
        this.appointmentDetailsService = appointmentDetailsService;
        this.unitDetailsService = unitDetailsService;
    }

    @RequestMapping(value = "/calendar", method = RequestMethod.GET, produces = "text/html")
    public ModelAndView bookingHtml(@AuthenticationPrincipal Client client,
                                    @RequestParam(value = "error", required = false) String error,
                                    @RequestParam(value = "unavailable", required = false) String unavailable,
                                    @RequestParam(value = "not_eligible", required = false) String notEligible,
                                    HttpServletRequest request) throws UnsupportedEncodingException {

        Map<String, Object> model = new HashMap<>();

        String unitAddress = getUnitLocation(client);
        model.put("location", unitAddress);
        model.put("locationURL", URLEncoder.encode("Visa and Citizenship Office, " + unitAddress, "UTF-8"));
        model.put("today_date", getUnitCurrentDate(client));
        model.put("current_appointment", getCurrentAppointmentDetails(client));

        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (csrfToken != null) {
            model.put("_csrf", csrfToken);
        }
        if (error != null) {
            model.put("error", true);
        }
        if (unavailable != null) {
            model.put("unavailable", true);
        }
        if (notEligible != null) {
            model.put("not_eligible", true);
        }
        return new ModelAndView("calendar_page", model);
    }

    private String getCurrentAppointmentDetails(Client client){
        AppointmentDetails appointment = appointmentDetailsService.getExpectedAppointmentForClientForNextYear(client);
        if (appointment != null) {
            return appointment.getAppointmentDate().format(DateTimeFormatter.ofPattern("EEEE dd MMMM, h:mm a"));
        }
        return null;
    }

    private String getUnitLocation(Client client){
        return unitDetailsService.getUnitAddress(client.getUnitId());
    }

    private String getUnitCurrentDate(Client client){
        return unitDetailsService.getUnitCurrentLocalTime(client.getUnitId()).toString();
    }

    @ExceptionHandler(RuntimeException.class)
    public ModelAndView handleRuntimeException(HttpServletRequest req, RuntimeException exception){
        LOGGER.error("Unhandled RuntimeException", exception);
        return new ModelAndView("redirect:/calendar?error", new HashMap<>());
    }
}
