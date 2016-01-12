package au.gov.dto.dibp.appointments.login;

import au.gov.dto.dibp.appointments.qflowintegration.ApiCallsSenderService;
import au.gov.dto.dibp.appointments.util.ResponseWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
class AppointmentTypeService {

    private final ApiCallsSenderService senderService;
    private final String serviceAddressAppointmentType;

    @Autowired
    public AppointmentTypeService(ApiCallsSenderService senderService,
                                     @Value("${SERVICE.ADDRESS.APPOINTMENT.TYPE}") String serviceAddressAppointmentType) {
        this.senderService = senderService;
        this.serviceAddressAppointmentType = serviceAddressAppointmentType;
    }

    public String getAppointmentTypeIdByExternalReference(String extRef){
        Map<String, String> data = new HashMap<>();
        data.put("externalReference", extRef);

        ResponseWrapper response = senderService.sendRequest(GetAppointmentTypeByExtRef.REQUEST_TEMPLATE_PATH, data, serviceAddressAppointmentType);
        return response.getStringAttribute(GetAppointmentTypeByExtRef.APPOINTMENT_TYPE_ID);
    }

    private class GetAppointmentTypeByExtRef {
        static final String REQUEST_TEMPLATE_PATH = "GetAppointmentTypeByExternalReference.mustache";
        static final String APPOINTMENT_TYPE_ID = "//GetByExtRefResponse/GetByExtRefResult/AppointmentType/Id";
    }
}
