package au.gov.dto.dibp.appointments.errors;

import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

@Controller
public class ErrorsController implements ErrorController {
    private static final String ERROR_PATH = "/error";

    @RequestMapping(value = ERROR_PATH, method = RequestMethod.GET, produces = "text/html")
    public ModelAndView getErrorPage(HttpServletResponse response) {
        if (response.getStatus() == 200) {
            response.setStatus(500);
        }
        return new ModelAndView("error_page", new HashMap<>());
    }

    @Override
    public String getErrorPath() {
        return ERROR_PATH;
    }
}
