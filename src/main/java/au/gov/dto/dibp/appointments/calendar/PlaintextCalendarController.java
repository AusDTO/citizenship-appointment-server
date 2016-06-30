package au.gov.dto.dibp.appointments.calendar;

import au.gov.dto.dibp.appointments.appointmentdetails.AppointmentDetails;
import au.gov.dto.dibp.appointments.appointmentdetails.AppointmentDetailsService;
import au.gov.dto.dibp.appointments.availabledates.AvailableDatesController;
import au.gov.dto.dibp.appointments.availabletimes.AvailableTimesController;
import au.gov.dto.dibp.appointments.client.Client;
import au.gov.dto.dibp.appointments.organisation.UnitDetailsService;
import au.gov.dto.dibp.appointments.util.CalendarIdValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class PlaintextCalendarController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlaintextCalendarController.class);
    private static final String CALENDAR_NOJS_PAGE_NAME = "Select Date";
    private static final String DAYTIME_NOJS_PAGE_NAME = "Select Time";
    private static final String SELECTION_NOJS_PAGE_NAME = "Confirm Selection";
    private final AvailableDatesController availableDatesController;
    private final AvailableTimesController availableTimesController;
    private final AppointmentDetailsService appointmentDetailsService;
    private final UnitDetailsService unitDetailsService;
    private final CalendarIdValidator calendarIdValidator;

    @Autowired
    public PlaintextCalendarController(AvailableDatesController availableDatesController,
                                       AvailableTimesController availableTimesController,
                                       AppointmentDetailsService appointmentDetailsService,
                                       UnitDetailsService unitDetailsService,
                                       CalendarIdValidator calendarIdValidator){
        this.availableDatesController = availableDatesController;
        this.availableTimesController = availableTimesController;
        this.appointmentDetailsService = appointmentDetailsService;
        this.unitDetailsService = unitDetailsService;
        this.calendarIdValidator = calendarIdValidator;
    }

    @RequestMapping(value = "/calendar/text", method = RequestMethod.GET, produces = "text/html")
    public ModelAndView getPlaintextCalendar(@AuthenticationPrincipal Client client,
                                    @RequestParam(value = "error", required = false) String error,
                                    HttpServletRequest request) {

        Map<String, Object> model = getCommonModelData(client, error);

        model.put("page_name", CALENDAR_NOJS_PAGE_NAME);
        model.put("is_logged_in", true);

        final List availableDates = availableDatesController.getAvailableDatesForPlaintext(client);
        if(availableDates != null && !availableDates.isEmpty()){
            model.put("available_dates", availableDates);
        }
        return new ModelAndView("calendar_nojs", model);
    }

    @RequestMapping(value = "/calendar/text/{calendarId}", method = RequestMethod.GET, produces = "text/html")
    public ModelAndView getPlaintextTimes(@AuthenticationPrincipal Client client,
                                    @PathVariable("calendarId") String calendarId,
                                    @RequestParam(value = "error", required = false) String error,
                                    HttpServletRequest request) {
        if(!calendarIdValidator.isCalendarIdValid(calendarId)){
            throw new RuntimeException("Invalid format of the calendarId!");
        }

        Map<String, Object> model = getCommonModelData(client, error);
        model.put("page_name", DAYTIME_NOJS_PAGE_NAME);
        model.put("is_logged_in", true);

        final Map<String, Object> availableTimesWithLabel = availableTimesController.getAvailableTimesWithLabel(client, calendarId);
        model.put("available_times", availableTimesWithLabel.get("times"));

        LocalDateTime date = LocalDateTime.parse((String) availableTimesWithLabel.get("date"));

        model.put("display_date", date.format(DateTimeFormatter.ofPattern("EEEE dd MMMM yyyy")));
        model.put("date", date.format(DateTimeFormatter.ofPattern("yyyy'-'MM'-'dd")));
        return new ModelAndView("daytimes_nojs", model);
    }

    @RequestMapping(value = "/calendar/text/{date}/{time}", method = RequestMethod.GET, produces = "text/html")
    public ModelAndView getPlaintextSelectionConfirmation(@AuthenticationPrincipal Client client,
                                          @PathVariable("date") String dateString,
                                          @PathVariable("time") String timeString,
                                          @RequestParam(value = "error", required = false) String error,
                                          HttpServletRequest request) {

        Map<String, Object> model = getCommonModelData(client, error);

        model.put("page_name", SELECTION_NOJS_PAGE_NAME);
        model.put("is_logged_in", true);

        LocalDate selectedAppointmentDate = LocalDate.parse(dateString);
        LocalTime selectedAppointmentTime = LocalTime.parse(timeString);
        LocalDateTime selectedAppointment = LocalDateTime.of(selectedAppointmentDate, selectedAppointmentTime);

        model.put("selected_appointment", selectedAppointment.format(DateTimeFormatter.ofPattern("yyyy'-'MM'-'dd'T'HH':'mm':'ss")));
        model.put("display_appointment_datetime", selectedAppointment.format(DateTimeFormatter.ofPattern("EEEE dd MMMM, h:mm a")));
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (csrfToken != null) {
            model.put("_csrf", csrfToken);
        }

        return new ModelAndView("selection_nojs", model);
    }

    private Map<String, Object> getCommonModelData(Client client, String error){
        Map<String, Object> model = new HashMap<>();

        String unitAddress = getUnitLocation(client);
        model.put("location", unitAddress);
        try {
            model.put("locationURL", URLEncoder.encode(unitAddress, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            //should never happen as UTF-8 is supported
            throw new RuntimeException("UTF-8 encoding not supported", e);
        }
        model.put("current_appointment", getCurrentAppointmentDetails(client));

        if (error != null) {
            model.put("error", true);
        }
        return model;
    }

    private String getCurrentAppointmentDetails(Client client){
        AppointmentDetails appointment = appointmentDetailsService.getExpectedAppointmentForClientForNextYear(client);
        if (appointment != null) {
            return appointment.getAppointmentDate().format(DateTimeFormatter.ofPattern("EEEE dd MMMM, h:mm a"));
        }
        return null;
    }

    private String getUnitLocation(Client client){
        return unitDetailsService.getUnitAddress(client.getUnitId());
    }

    @ExceptionHandler(RuntimeException.class)
    public ModelAndView handleRuntimeException(HttpServletRequest req, RuntimeException exception){
        LOGGER.error("Unhandled RuntimeException", exception);
        return new ModelAndView("redirect:/calendar?error", new HashMap<>());
    }

}
