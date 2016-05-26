package au.gov.dto.dibp.appointments.booking;

import au.gov.dto.dibp.appointments.appointmentdetails.AppointmentDetails;
import au.gov.dto.dibp.appointments.appointmentdetails.AppointmentDetailsService;
import au.gov.dto.dibp.appointments.booking.exceptions.BookingResponseInvalidException;
import au.gov.dto.dibp.appointments.booking.exceptions.NoCalendarExistsException;
import au.gov.dto.dibp.appointments.booking.exceptions.SlotAlreadyTakenException;
import au.gov.dto.dibp.appointments.booking.exceptions.UserNotEligibleToBookException;
import au.gov.dto.dibp.appointments.client.Client;
import au.gov.dto.dibp.appointments.qflowintegration.ApiCallsSenderService;
import au.gov.dto.dibp.appointments.qflowintegration.ApiResponseNotSuccessfulException;
import au.gov.dto.dibp.appointments.util.ResponseWrapper;
import au.gov.dto.dibp.appointments.util.TemplateLoader;
import au.gov.dto.dibp.appointments.wallet.PassUpdateService;
import com.samskivert.mustache.Template;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class BookingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BookingService.class);

    private final ApiCallsSenderService apiService;
    private final AppointmentDetailsService appointmentDetailsService;
    private final PassUpdateService passUpdateService;
    private final String serviceAddressService;
    private final String serviceAddressProcess;
    private final Template setAppointmentTemplate;
    private final Template rescheduleAppointmentTemplate;

    @Autowired
    public BookingService(ApiCallsSenderService apiService,
                          AppointmentDetailsService appointmentDetailsService,
                          PassUpdateService passUpdateService,
                          TemplateLoader templateLoader,
                          @Value("${service.address.service}") String serviceAddressService,
                          @Value("${service.address.process}") String serviceAddressProcess) {
        this.apiService = apiService;
        this.appointmentDetailsService = appointmentDetailsService;
        this.passUpdateService = passUpdateService;
        this.serviceAddressService = serviceAddressService;
        this.serviceAddressProcess = serviceAddressProcess;
        this.setAppointmentTemplate = templateLoader.loadRequestTemplate(SetAppointment.REQUEST_TEMPLATE_PATH);
        this.rescheduleAppointmentTemplate = templateLoader.loadRequestTemplate(RescheduleAppointment.REQUEST_TEMPLATE_PATH);
    }

    public String bookAnAppointment(Client client, LocalDateTime newAppointmentDateTime) {
        AppointmentDetails existingAppointment = appointmentDetailsService.getExpectedAppointmentForClientForNextYear(client);
        String scheduledAppointmentDateTime;
        if (existingAppointment != null) {
            LOGGER.info("Client with clientId=[{}] has an existing appointment for oldAppointmentDate=[{}], rescheduling the appointment to new appointmentDate=[{}]", client.getClientId(), existingAppointment.getAppointmentDate().toString(), newAppointmentDateTime.toString());
            scheduledAppointmentDateTime = rescheduleAppointment(client, newAppointmentDateTime, existingAppointment);
        } else {
            LOGGER.info("Client with clientId=[{}] has no existing appointment. Booking the appointment for appointmentDate=[{}]", client.getClientId(), newAppointmentDateTime.toString());
            scheduledAppointmentDateTime = bookInitialAppointment(client, newAppointmentDateTime);
        }
        passUpdateService.sendPassUpdatesInBackground(client);
        return scheduledAppointmentDateTime;
    }

    private String rescheduleAppointment(Client client, LocalDateTime newAppointmentTime, AppointmentDetails existingAppointment) {
        Map<String, String> data = new HashMap<>();
        data.put("processId", existingAppointment.getProcessId());
        data.put("serviceId", client.getServiceId());
        data.put("appointmentDateTime", newAppointmentTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        data.put("appointmentTypeId", client.getAppointmentTypeId());
        data.put("clientId", client.getClientId());

        return setScheduledAppointmentTime(rescheduleAppointmentTemplate, data, serviceAddressProcess, RescheduleAppointment.APPOINTMENT_DATE);
    }

    private String bookInitialAppointment(Client client, LocalDateTime appointmentTime) {
        Map<String, String> data = new HashMap<>();
        data.put("dateAndTime", appointmentTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        data.put("customerId", client.getCustomerId());
        data.put("clientId", client.getClientId());
        data.put("serviceId", client.getServiceId());
        data.put("appointmentTypeId", client.getAppointmentTypeId());

        return setScheduledAppointmentTime(setAppointmentTemplate, data, serviceAddressService, SetAppointment.APPOINTMENT_DATE);
    }

    private String setScheduledAppointmentTime(Template template, Map<String, String> data, String serviceAddress, String appointmentDatePath) {
        try {
            ResponseWrapper response = apiService.sendRequest(template, data, serviceAddress);
            String scheduledAppointmentTime = response.getStringAttribute(appointmentDatePath);
            if (StringUtils.isBlank(scheduledAppointmentTime)) {
                checkForUserNotEligibleException(response);
            }
            return scheduledAppointmentTime;
        } catch (ApiResponseNotSuccessfulException e) {
            throw getMeaningfulExceptionFromFault(e);
        }
    }

    private RuntimeException getMeaningfulExceptionFromFault(ApiResponseNotSuccessfulException exception) {
        ResponseWrapper response = exception.getResponse();

        String errorCode = response.getErrorCode();
        if ("58725".equals(errorCode)) {
            return new SlotAlreadyTakenException("The slot was already taken.", exception);
        } else if ("58710".equals(errorCode)) {
            return new NoCalendarExistsException("The calendar does not exist for the selected date and service.", exception);
        }
        return new BookingResponseInvalidException("Unknown fault occurred. " + response.getMessage(), exception);
    }

    private void checkForUserNotEligibleException(ResponseWrapper response) {
        final String exceptionMessage = response.getStringAttribute(EXCEPTION_MESSAGE);
        if (exceptionMessage.contains("The appointment cannot be set or rescheduled")) {
            throw new UserNotEligibleToBookException("The user is not eligible to book an appointment. " + exceptionMessage);
        }
        throw new BookingResponseInvalidException("Unknown fault occurred while parsing response. Error message provided: " + exceptionMessage);
    }

    private class SetAppointment {
        static final String REQUEST_TEMPLATE_PATH = "SetAppointment.mustache";
        static final String APPOINTMENT_DATE = "//SetAppointmentResponse/SetAppointmentResult/SetAppointmentData/DateAndTime";
    }

    private static final String EXCEPTION_MESSAGE = "//ScriptResults/Messages/ScriptMessage/Message";

    private class RescheduleAppointment {
        static final String REQUEST_TEMPLATE_PATH = "RescheduleAppointment.mustache";
        static final String APPOINTMENT_DATE = "//RescheduleAppointmentResponse/RescheduleAppointmentResult/RescheduleAppointmentData/DateAndTime";
    }
}
