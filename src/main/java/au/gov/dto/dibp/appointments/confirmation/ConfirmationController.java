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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

@RestController
public class ConfirmationController {

    private static final DateTimeFormatter APPOINTMENT_DATE_FORMATTER = DateTimeFormatter.ofPattern("EEEE dd MMMM yyyy");
    private static final DateTimeFormatter APPOINTMENT_TIME_FORMATTER = DateTimeFormatter.ofPattern("h:mm a");

    private final AppointmentDetailsService appointmentDetailsService;

    @Autowired
    public ConfirmationController(AppointmentDetailsService appointmentDetailsService){
        this.appointmentDetailsService = appointmentDetailsService;
    }

    @RequestMapping(value = "/confirmation", method = RequestMethod.GET, produces = "text/html")
    public ModelAndView getConfirmationPage(@AuthenticationPrincipal Client client) {
        AppointmentDetails appointmentDetails = appointmentDetailsService.getExpectedAppointmentForClientForNextYear(client);

        if(appointmentDetails == null){
            return new ModelAndView("redirect:/calendar?error", new HashMap<>());
        }

        HashMap<String, Object> model = new HashMap<>();

        String unitAddress = appointmentDetails.getUnitAddress();
        model.put("location", unitAddress);
        try {
            model.put("locationURL", URLEncoder.encode(unitAddress, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            //should never happen as UTF-8 is supported
            throw new RuntimeException("UTF-8 encoding not supported", e);
        }
        model.put("clientId", client.getClientId());
        model.put("hasEmail", client.isEmail());
        model.put("hasMobile", client.isMobile());

        model.put("appointment_date", appointmentDetails.getAppointmentDate().format(APPOINTMENT_DATE_FORMATTER));
        model.put("appointment_time", appointmentDetails.getAppointmentDate().format(APPOINTMENT_TIME_FORMATTER));

        return new ModelAndView("confirmation_page", model);
    }

}
