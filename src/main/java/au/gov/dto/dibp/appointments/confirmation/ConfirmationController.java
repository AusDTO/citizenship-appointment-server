package au.gov.dto.dibp.appointments.confirmation;


import au.gov.dto.dibp.appointments.appointmentdetails.AppointmentDetails;
import au.gov.dto.dibp.appointments.appointmentdetails.AppointmentDetailsService;
import au.gov.dto.dibp.appointments.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;

@RestController
public class ConfirmationController {

    private static final DateTimeFormatter APPOINTMENT_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("EEEE dd MMMM, h:mm a");

    private final AppointmentDetailsService appointmentDetailsService;
    private final String trackingId;

    @Autowired
    public ConfirmationController(AppointmentDetailsService appointmentDetailsService,
                                  @Value("${analytics.tracking.id}") String trackingId){
        this.appointmentDetailsService = appointmentDetailsService;
        this.trackingId = trackingId;
    }

    @RequestMapping(value = "/confirmation", method = RequestMethod.GET, produces = "text/html")
    public ModelAndView getConfirmationPage(@AuthenticationPrincipal Client client) {
        AppointmentDetails appointmentDetails = appointmentDetailsService.getExpectedAppointmentForClientForNextYear(client);

        if(appointmentDetails == null){
            return new ModelAndView("redirect:/calendar?error", new HashMap<>());
        }

        HashMap<String, Object> model = new HashMap<>();
        model.put("trackingId", trackingId);
        model.put("location", appointmentDetails.getUnitAddress());
        model.put("clientId", client.getClientId());
        model.put("hasEmail", client.hasEmail());

        model.put("selected_appointment", appointmentDetails.getAppointmentDate().format(APPOINTMENT_DATE_TIME_FORMATTER));

        return new ModelAndView("confirmation_page", model);
    }

}
