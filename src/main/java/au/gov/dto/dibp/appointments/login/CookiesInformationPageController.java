package au.gov.dto.dibp.appointments.login;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;

@RestController
public class CookiesInformationPageController {

    @RequestMapping(value = "/cookies", method = RequestMethod.GET, produces = "text/html")
    public ModelAndView getCookiesInformationPage() {
        return new ModelAndView("cookies", new HashMap<>());
    }
}
