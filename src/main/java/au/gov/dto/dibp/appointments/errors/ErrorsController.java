package au.gov.dto.dibp.appointments.errors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

@Controller
public class ErrorsController {

    private final String trackingId;

    @Autowired
    public ErrorsController(@Value("${analytics.tracking.id}") String trackingId) {
        this.trackingId = trackingId;
    }

    @RequestMapping(value = "/error", method = RequestMethod.GET, produces = "text/html")
    public ModelAndView getErrorPage(HttpServletResponse response) {
        response.setStatus(500);
        HashMap<String, Object> model = new HashMap<>();
        model.put("trackingId", trackingId);
        return new ModelAndView("error_page", model);
    }
}
