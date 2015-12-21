package au.gov.dto.dibp.appointments.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

@RestController
public class BookingController {

    @RequestMapping(value = "/booking", method = RequestMethod.GET, produces = "text/html")
    public ModelAndView bookingHtml() {

        Map<String, Object> model = new HashMap<>();

        model.put("location", "Ground Floor, 26 Lee St");
        model.put("todayDate", LocalDate.now(ZoneId.of("Australia/Sydney")));

        return new ModelAndView("booking_page", model);
    }

}
