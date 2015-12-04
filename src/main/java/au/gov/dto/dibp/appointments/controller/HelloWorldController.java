package au.gov.dto.dibp.appointments.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HelloWorldController {
    @RequestMapping(value = "/hello.txt", method = RequestMethod.GET, produces = "text/plain")
    public String hello() {
        return "Hello world!";
    }

    @RequestMapping(value = "/hello.json", method = RequestMethod.GET, produces = "application/json")
    public Map<String, Object> helloJson() {
        return new HashMap<String, Object>() {{
            put("hello", "world!");
        }};
    }

    @RequestMapping(value = "/hello.html", method = RequestMethod.GET, produces = "text/html")
    public ModelAndView helloHtml() {
        return new ModelAndView("helloworld", new HashMap<String, Object>() {{
            put("hello", "world!");
        }});
    }
}
