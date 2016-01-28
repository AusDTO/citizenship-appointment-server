package au.gov.dto.dibp.appointments.errors;

import au.gov.dto.dibp.appointments.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

@Controller
public class ErrorsController implements ErrorController {
    private static final String ERROR_PATH = "/error";

    private final String trackingId;

    @Autowired
    public ErrorsController(@Value("${analytics.tracking.id}") String trackingId) {
        this.trackingId = trackingId;
    }

    @RequestMapping(value = ERROR_PATH, method = RequestMethod.GET, produces = "text/html")
    public ModelAndView getErrorPage(@AuthenticationPrincipal Client client, HttpServletResponse response) {
        if (response.getStatus() == 200) {
            response.setStatus(500);
        }
        HashMap<String, Object> model = new HashMap<>();
        model.put("trackingId", trackingId);
        if (client != null) {
            model.put("clientId", client.getClientId());
        }
        return new ModelAndView("error_page", model);
    }

    @Override
    public String getErrorPath() {
        return ERROR_PATH;
    }
}
