package au.gov.dto.dibp.appointments.calendar;

import au.gov.dto.dibp.appointments.appointmentdetails.AppointmentDetails;
import au.gov.dto.dibp.appointments.appointmentdetails.AppointmentDetailsService;
import au.gov.dto.dibp.appointments.client.Client;
import au.gov.dto.dibp.appointments.unit.UnitDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
public class CalendarController {

    private final AppointmentDetailsService appointmentDetailsService;
    private final UnitDetailsService unitDetailsService;

    @Autowired
    public CalendarController(AppointmentDetailsService appointmentDetailsService, UnitDetailsService unitDetailsService){
        this.appointmentDetailsService = appointmentDetailsService;
        this.unitDetailsService = unitDetailsService;
    }

    @RequestMapping(value = "/calendar", method = RequestMethod.GET, produces = "text/html")
    public ModelAndView bookingHtml(@AuthenticationPrincipal Client client,
                                    @RequestParam(value = "error", required = false) String error,
                                    HttpServletRequest request) {

        Map<String, Object> model = new HashMap<>();

        //TODO: refactor not to make 2 calls for unitId & details
        model.put("location", getUnitLocation(client));
        model.put("todayDate", getUnitCurrentDate(client));
        model.put("current_appointment", getCurrentAppointmentDetails(client));

        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (csrfToken != null) {
            model.put("_csrf", csrfToken);
        }
        if (error != null) {
            model.put("error", true);
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
        return unitDetailsService.getUnitAddressByServiceId(client.getServiceId());
    }

    private String getUnitCurrentDate(Client client){
        return unitDetailsService.getUnitCurrentLocalTimeByServiceId(client.getServiceId());
    }

}
