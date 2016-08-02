package au.gov.dto.dibp.appointments.login;

import au.gov.dto.dibp.appointments.client.ClientIdValidator;
import au.gov.dto.dibp.appointments.session.LogoutCookieService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@RestController
public class LoginController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);
    private static final String LOGIN_PAGE_NAME = "Login";
    private static final String SESSION_TIMEOUT_PAGE_NAME = "Session Timeout";
    private final ClientIdValidator clientIdValidator;

    @Autowired
    public LoginController(ClientIdValidator clientIdValidator) {
        this.clientIdValidator = clientIdValidator;
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET, produces = "text/html")
    public ModelAndView loginHtml(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "system_error", required = false) String system_error,
            @RequestParam(value = "maintenance", required = false) String maintenance,
            @RequestParam(value = "expired", required = false) String expired,
            @RequestParam(value = "id", required = false) String clientId,
            HttpServletRequest request, HttpServletResponse response) {

        Map<String, Object> model = new HashMap<>();

        model.put("page_name", LOGIN_PAGE_NAME);
        model.put("is_login_page", true);

        if (error != null) {
            model.put("error", true);
        }
        if (system_error != null) {
            model.put("system_error", true);
        }
        if (maintenance != null) {
            model.put("maintenance", true);
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

    @RequestMapping(value = "/session_timeout", method = RequestMethod.GET, produces = "text/html")
    public ModelAndView sessionTimeoutHtml(HttpServletRequest request, HttpServletResponse response) {
        response.addCookie(LogoutCookieService.getLogoutCookie(request));
        Map<String, Object> model = new HashMap<>();
        model.put("page_name", SESSION_TIMEOUT_PAGE_NAME);
        return new ModelAndView("session_timeout", model);
    }

    @ExceptionHandler(RuntimeException.class)
    public ModelAndView handleRuntimeException(HttpServletRequest req, RuntimeException exception){
        LOGGER.error("Unhandled RuntimeException", exception);
        return new ModelAndView("redirect:/login?expired", new HashMap<>());
    }
}
