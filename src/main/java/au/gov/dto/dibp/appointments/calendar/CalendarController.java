package au.gov.dto.dibp.appointments.calendar;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

@RestController
public class CalendarController {

    @RequestMapping(value = "/booking", method = RequestMethod.GET, produces = "text/html")
    public ModelAndView bookingHtml(@RequestParam(value = "error", required = false) String error, HttpServletRequest request) {

        Map<String, Object> model = new HashMap<>();

        model.put("location", "Ground Floor, 26 Lee St");
        model.put("todayDate", LocalDate.now(ZoneId.of("Australia/Sydney")));
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (csrfToken != null) {
            model.put("_csrf", csrfToken);
        }
        if (error != null) {
            model.put("error", true);
        }
        return new ModelAndView("booking_page", model);
    }

}
