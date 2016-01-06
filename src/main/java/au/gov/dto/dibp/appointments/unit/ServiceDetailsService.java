package au.gov.dto.dibp.appointments.unit;

import au.gov.dto.dibp.appointments.qflowintegration.ApiCallsSenderService;
import au.gov.dto.dibp.appointments.util.ResponseWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
class ServiceDetailsService {

    private final ApiCallsSenderService senderService;
    private final String serviceAddress;

    @Autowired
    public ServiceDetailsService(ApiCallsSenderService senderService,
                                 @Value("${SERVICE.ADDRESS.SERVICE}") String serviceAddress) {
        this.senderService = senderService;
        this.serviceAddress = serviceAddress;
    }

    public String getUnitIdForService(String serviceId){
        Map<String, String> data = new HashMap<>();
        data.put("serviceId", serviceId);

        ResponseWrapper response = senderService.sendRequest(GetService.REQUEST_TEMPLATE_PATH, data, serviceAddress);
        return getUnitIdFromGetServiceResponse(response);
    }

    private String getUnitIdFromGetServiceResponse(ResponseWrapper responseWrapper){
        return responseWrapper.getStringAttribute(GetService.UNIT_ID);
    }

    private class GetService {
        static final String REQUEST_TEMPLATE_PATH = "GetService.mustache";
        static final String UNIT_ID = "//GetResponse/GetResult/UnitId";
    }
}
