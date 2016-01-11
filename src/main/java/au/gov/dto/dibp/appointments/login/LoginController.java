package au.gov.dto.dibp.appointments.login;

import au.gov.dto.dibp.appointments.client.ClientIdValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
public class LoginController {

    @Autowired
    private ClientIdValidator clientIdValidator;

    @RequestMapping(value = "/login", method = RequestMethod.GET, produces = "text/html")
    public ModelAndView loginHtml(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "id", required = false) String clientId,
            HttpServletRequest request) {

        Map<String, Object> model = new HashMap<>();
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (csrfToken != null) {
            model.put("_csrf", csrfToken);
        }
        if (error != null) {
            model.put("error", true);
        }
        if(clientIdValidator.isClientIdValid(clientId)){
            model.put("clientId", clientId);
        }
        return new ModelAndView("login_page", model);
    }

}
