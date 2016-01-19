package au.gov.dto.dibp.appointments.booking;

import au.gov.dto.dibp.appointments.appointmentdetails.AppointmentDetails;
import au.gov.dto.dibp.appointments.appointmentdetails.AppointmentDetailsService;
import au.gov.dto.dibp.appointments.client.Client;
import au.gov.dto.dibp.appointments.qflowintegration.ApiCallsSenderService;
import au.gov.dto.dibp.appointments.util.ResponseWrapper;
import au.gov.dto.dibp.appointments.util.TemplateLoader;
import com.samskivert.mustache.Template;
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

    private final ApiCallsSenderService senderService;
    private final AppointmentDetailsService appointmentDetailsService;

    private final Logger LOGGER = LoggerFactory.getLogger(BookingService.class);

    private final String serviceAddress;
    private final String serviceAddressProcess;

    private final Template setAppointmentTemplate;
    private final Template rescheduleAppointmentTemplate;

    @Autowired
    public BookingService(ApiCallsSenderService senderService,
                          AppointmentDetailsService appointmentDetailsService,
                          TemplateLoader templateLoader,
                          @Value("${SERVICE.ADDRESS.SERVICE}") String serviceAddress,
                          @Value("${SERVICE.ADDRESS.PROCESS}") String serviceAddressProcess) {
        this.senderService = senderService;
        this.appointmentDetailsService = appointmentDetailsService;
        this.serviceAddress = serviceAddress;
        this.serviceAddressProcess = serviceAddressProcess;

        setAppointmentTemplate = templateLoader.loadRequestTemplate(SetAppointment.REQUEST_TEMPLATE_PATH);
        rescheduleAppointmentTemplate = templateLoader.loadRequestTemplate(RescheduleAppointment.REQUEST_TEMPLATE_PATH);
    }

    public String bookAnAppointment(Client client, LocalDateTime appointmentTime) {
        final AppointmentDetails appointment = appointmentDetailsService.getExpectedAppointmentForClientForNextYear(client);

        if(appointment!=null){
            LOGGER.info("Client "+ client.getClientId()+ " has an appointment set for "+ appointment.getAppointmentDate().toString()
                    +" \nRescheduling the appointment to "+ appointmentTime.toString());
            return rescheduleAppointment(client, appointmentTime, appointment);
        }
        else{
            LOGGER.info("Client "+ client.getClientId()+ " has no appointment set"
                    +" \nBooking the appointment for "+ appointmentTime.toString());
            return bookInitialAppointment(client, appointmentTime);
        }
    }

    private String rescheduleAppointment(Client client, LocalDateTime newAppointmentTime, AppointmentDetails appointment) {
        Map<String, String> data = new HashMap<>();
        data.put("processId", appointment.getProcessId());
        data.put("serviceId", client.getServiceId());
        data.put("appointmentDateTime", newAppointmentTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        data.put("appointmentTypeId", client.getAppointmentTypeId());
        data.put("clientId", client.getClientId());

        ResponseWrapper response = senderService.sendRequest(rescheduleAppointmentTemplate, data, serviceAddressProcess);
        return getScheduledAppointmentTime(response, RescheduleAppointment.APPOINTMENT_DATE);
    }

    private String bookInitialAppointment(Client client, LocalDateTime appointmentTime) {
        Map<String, String> data = new HashMap<>();
        data.put("dateAndTime", appointmentTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        data.put("customerId", client.getCustomerId());
        data.put("clientId", client.getClientId());
        data.put("serviceId", client.getServiceId());
        data.put("appointmentTypeId", client.getAppointmentTypeId());

        ResponseWrapper response = senderService.sendRequest(setAppointmentTemplate, data, serviceAddress);
        return getScheduledAppointmentTime(response, SetAppointment.APPOINTMENT_DATE);
    }

    private String getScheduledAppointmentTime(ResponseWrapper response, String appointmentDatePath){
        try {
            return response.getStringAttribute(appointmentDatePath);
        }catch(RuntimeException e){
            throw new BookingResponseInvalidException("Response did not contain the appointment date.");
        }
    }

    private class SetAppointment {
        public static final String REQUEST_TEMPLATE_PATH = "SetAppointment.mustache";
        public static final String APPOINTMENT_DATE = "//SetAppointmentResponse/SetAppointmentResult/SetAppointmentData/DateAndTime";
    }

    private class RescheduleAppointment {
        public static final String REQUEST_TEMPLATE_PATH = "RescheduleAppointment.mustache";
        public static final String APPOINTMENT_DATE = "//RescheduleAppointmentResponse/RescheduleAppointmentResult/RescheduleAppointmentData/DateAndTime";
    }
}
