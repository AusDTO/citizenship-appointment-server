package au.gov.dto.dibp.appointments.login;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@RestController
public class CookiesInformationPageController {

    private static final String COOKIE_PAGE_NAME = "About Cookies";

    @RequestMapping(value = "/cookies", method = RequestMethod.GET, produces = "text/html")
    public ModelAndView getCookiesInformationPage() {
        Map<String, Object> model = new HashMap<>();

        model.put("page_name", COOKIE_PAGE_NAME);

        return new ModelAndView("cookies", model);
    }
}
