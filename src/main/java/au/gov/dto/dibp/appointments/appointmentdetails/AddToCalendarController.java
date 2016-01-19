package au.gov.dto.dibp.appointments.appointmentdetails;

import au.gov.dto.dibp.appointments.client.Client;
import au.gov.dto.dibp.appointments.util.TemplateLoader;
import com.samskivert.mustache.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
public class AddToCalendarController {

    private final AppointmentDetailsService appointmentDetailsService;
    private final DateTimeFormatter calendarDateFormat;
    private final TemplateLoader templateLoader;

    @Autowired
    public AddToCalendarController(AppointmentDetailsService appointmentDetailsService, TemplateLoader templateLoader){
        this.appointmentDetailsService = appointmentDetailsService;
        this.calendarDateFormat = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");
        this.templateLoader = templateLoader;
    }

    @RequestMapping(value = "/calendar.ics", method = RequestMethod.GET, produces = "text/calendar")
    public @ResponseBody
    ResponseEntity<String> getCalendarIcsFile(@AuthenticationPrincipal Client client) {
        final AppointmentDetails appointmentDetails = appointmentDetailsService.getExpectedAppointmentForClientForNextYear(client);

        Map<String, Object> model = new HashMap<>();
        model.put("id", UUID.randomUUID().toString());
        model.put("timeZone", "");

        model.put("startTime", appointmentDetails.getAppointmentDate().format(calendarDateFormat));
        model.put("endTime", appointmentDetails.getAppointmentDate().plusHours(2L).format(calendarDateFormat));
        model.put("location", appointmentDetails.getUnitAddress());

        Template template = templateLoader.loadTemplate("calendar_ics.mustache");
        String requestBody = template.execute(model);

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("content-disposition", "attachment; filename=appointment.ics");
        responseHeaders.add("Content-Type","text/calendar");

        return new ResponseEntity<>(requestBody, responseHeaders,  HttpStatus.OK);
    }

    @RequestMapping(value = "/googlecalendar", method = RequestMethod.GET, produces = "text/calendar")
    public ModelAndView addToGoogleCalendar(@AuthenticationPrincipal Client client){
        final AppointmentDetails appointmentDetails = appointmentDetailsService.getExpectedAppointmentForClientForNextYear(client);

        String startTime = appointmentDetails.getAppointmentDate().format(calendarDateFormat);
        String endTime = appointmentDetails.getAppointmentDate().plusHours(2L).format(calendarDateFormat);

        String queryParams = "action=TEMPLATE" +
                "&text=Citizenship Appointment" +
                "&dates=" + startTime + "/" + endTime +
                "&czt=Australia/Melbourne" +
                "&location=" + appointmentDetails.getUnitAddress() +
                "&details=" + "Australian Citizenship Appointment please bring all the required documents and make sure you are prepared to sit the test" +
                "&trp=false";

        return new ModelAndView("redirect:http://www.google.com/calendar/event?" + queryParams);
    }

    @RequestMapping(value = "/yahoocalendar", method = RequestMethod.GET, produces = "text/calendar")
    public ModelAndView addToYahooCalendar(@AuthenticationPrincipal Client client){
        final AppointmentDetails appointmentDetails = appointmentDetailsService.getExpectedAppointmentForClientForNextYear(client);

        String startTime = appointmentDetails.getAppointmentDate().format(calendarDateFormat);

        String queryParams =
                "v=60" +
                "&DUR=0200" +
                "&TITLE=Citizenship Appointment" +
                "&ST=" + startTime +
                "&in_loc=" + appointmentDetails.getUnitAddress() +
                "&DESC=" + "Australian Citizenship Appointment please bring all the required documents and make sure you are prepared to sit the test";
        return new ModelAndView("redirect:http://calendar.yahoo.com/?" + queryParams);
    }

    @RequestMapping(value = "/outlookonline", method = RequestMethod.GET, produces = "text/calendar")
    public ModelAndView addToOutlookOnlineCalendar(@AuthenticationPrincipal Client client){
        final AppointmentDetails appointmentDetails = appointmentDetailsService.getExpectedAppointmentForClientForNextYear(client);

        String startTime = appointmentDetails.getAppointmentDate().format(calendarDateFormat);
        String endtTime = appointmentDetails.getAppointmentDate().plusHours(2L).format(calendarDateFormat);

        String queryParams =
                "rru=addevent" +
                "&summary=Citizenship Appointment" +
                "&dtstart=" + startTime +
                "&dtend=" + endtTime +
                "&location=" + appointmentDetails.getUnitAddress() +
                "&description=" + "Australian Citizenship Appointment please bring all the required documents and make sure you are prepared to sit the test";
        return new ModelAndView("redirect:http://calendar.live.com/calendar/calendar.aspx?" + queryParams);
    }
}
