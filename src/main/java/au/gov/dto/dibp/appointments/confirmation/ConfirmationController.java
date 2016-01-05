package au.gov.dto.dibp.appointments.confirmation;


import au.gov.dto.dibp.appointments.client.Client;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
public class ConfirmationController {

    @RequestMapping(value = "/confirmation", method = RequestMethod.GET, produces = "text/html")
    public ModelAndView getConfirmationPage(@AuthenticationPrincipal Client client,
                                            @RequestParam(value="bookedDateTime") String bookedDateTime) {

        Map<String, Object> model = new HashMap<>();

        model.put("location", "2 Lonsdale Street, Melbourne VIC 3000");
        model.put("level", "Level 4"); // TODO how to get it?

        model.put("clientId", client.getClientId());
        model.put("hasEmail", client.hasEmail());

        LocalDateTime selectedDate = LocalDateTime.parse(bookedDateTime);
        model.put("selected_appointment", selectedDate.format(DateTimeFormatter.ofPattern("EEEE dd MMMM, h:mm a")));

        return new ModelAndView("confirmation_page", model);
    }

}
