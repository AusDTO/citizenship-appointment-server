package au.gov.dto.dibp.appointments.booking;

import au.gov.dto.dibp.appointments.client.Client;
import au.gov.dto.dibp.appointments.qflowintegration.ApiCallsSenderService;
import au.gov.dto.dibp.appointments.util.ResponseWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class BookingService {
    public static final String REQUEST_TEMPLATE_PATH = "SetAppointment.mustache";
    public static final String APPOINTMENT_DATE = "//SetAppointmentResponse/SetAppointmentResult/SetAppointmentData/DateAndTime";

    private final ApiCallsSenderService senderService;
    private final String serviceAddress;

    @Autowired
    public BookingService(ApiCallsSenderService senderService, @Value("${SERVICE.ADDRESS.SERVICE}") String serviceAddress) {
        this.senderService = senderService;
        this.serviceAddress = serviceAddress;
    }

    public String bookAnAppointment(Client client, LocalDateTime appointmentTime) {
        Map<String, String> data = new HashMap<>();
        data.put("dateAndTime", appointmentTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        data.put("customerId", client.getCustomerId());
        data.put("clientId", client.getClientId());
        data.put("serviceId", client.getServiceId());
        data.put("appointmentTypeId", client.getAppointmentTypeId());

        ResponseWrapper response = senderService.sendRequest(REQUEST_TEMPLATE_PATH, data, serviceAddress);
        return getScheduledAppointmentTime(response);
    }

    private String getScheduledAppointmentTime(ResponseWrapper response){
        return response.getStringAttribute(APPOINTMENT_DATE);
    }
}
