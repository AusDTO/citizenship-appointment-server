package au.gov.dto.dibp.appointments.errors;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;

@Controller
public class ErrorsController {

    @RequestMapping(value = "/error", method = RequestMethod.GET, produces = "text/html")
    public ModelAndView getErrorPage() {
        return new ModelAndView("error_page", new HashMap<>());
    }
}
