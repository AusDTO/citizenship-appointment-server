package au.gov.dto.dibp.appointments.confirmation;


import au.gov.dto.dibp.appointments.appointmentdetails.AppointmentDetails;
import au.gov.dto.dibp.appointments.appointmentdetails.AppointmentDetailsService;
import au.gov.dto.dibp.appointments.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
public class ConfirmationController {

    private final AppointmentDetailsService appointmentDetailsService;

    @Autowired
    public ConfirmationController(AppointmentDetailsService appointmentDetailsService){
        this.appointmentDetailsService = appointmentDetailsService;
    }

    @RequestMapping(value = "/confirmation", method = RequestMethod.GET, produces = "text/html")
    public ModelAndView getConfirmationPage(@AuthenticationPrincipal Client client) {

        final AppointmentDetails appointmentDetails = appointmentDetailsService.getExpectedAppointmentForClientForNextYear(client);

        if(appointmentDetails == null){
            return new ModelAndView("redirect:/booking?error",  new HashMap<>());
        }

        Map<String, Object> model = new HashMap<>();

        model.put("location", appointmentDetails.getUnitAddress());
        model.put("level", "Level 4"); // TODO how to get it?

        model.put("clientId", client.getClientId());
        model.put("hasEmail", client.hasEmail());

        model.put("selected_appointment", appointmentDetails.getAppointmentDate().format(DateTimeFormatter.ofPattern("EEEE dd MMMM, h:mm a")));

        return new ModelAndView("confirmation_page", model);
    }

}
