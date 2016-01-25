package au.gov.dto.dibp.appointments.login;

import au.gov.dto.dibp.appointments.client.ClientIdValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@RestController
public class LoginController {

    private final ClientIdValidator clientIdValidator;
    private final String trackingId;

    @Autowired
    public LoginController(ClientIdValidator clientIdValidator,
                           @Value("${analytics.tracking.id}") String trackingId) {
        this.clientIdValidator = clientIdValidator;
        this.trackingId = trackingId;
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET, produces = "text/html")
    public ModelAndView loginHtml(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "expired", required = false) String expired,
            @RequestParam(value = "id", required = false) String clientId,
            HttpServletRequest request, HttpServletResponse response) {

        Map<String, Object> model = new HashMap<>();
        model.put("trackingId", trackingId);
        if (error != null) {
            model.put("error", true);
        }
        if (expired != null) {
            model.put("expired", true);
            response.setStatus(401);
        }
        if (clientIdValidator.isClientIdValid(clientId)){
            model.put("clientId", clientId);
        }
        return new ModelAndView("login_page", model);
    }

    @RequestMapping(value = "/sessionExpired", method = RequestMethod.POST, produces = "text/html")
    public ModelAndView sessionExpiredHtml(HttpServletRequest request) {
        return new ModelAndView("redirect:/login?expired", new HashMap<>());
    }

    @ExceptionHandler(RuntimeException.class)
    public ModelAndView handleRuntimeException(HttpServletRequest req, RuntimeException exception){
        return new ModelAndView("redirect:/login?expired", new HashMap<>());
    }
}
