package au.gov.dto.dibp.appointments.errors;

import au.gov.dto.dibp.appointments.session.LogoutCookieService;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Controller
public class ErrorsController implements ErrorController {
    private static final String ERROR_PATH = "/error";
    private static final String ERROR_PAGE_NAME = "Error";

    @RequestMapping(value = ERROR_PATH, method = RequestMethod.GET, produces = "text/html")
    public ModelAndView getErrorPage(HttpServletRequest request, HttpServletResponse response) {

        Map<String, Object> model = new HashMap<>();

        model.put("page_name", ERROR_PAGE_NAME);

        if (response.getStatus() == 200) {
            response.setStatus(500);
        }

        response.addCookie(LogoutCookieService.getLogoutCookie(request));
        return new ModelAndView("error_page", model);
    }

    @Override
    public String getErrorPath() {
        return ERROR_PATH;
    }
}
