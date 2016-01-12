package au.gov.dto.dibp.appointments.organisation;

import au.gov.dto.dibp.appointments.qflowintegration.ApiCallsSenderService;
import au.gov.dto.dibp.appointments.util.ResponseWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class UnitDetailsService {

    private final ServiceDetailsService serviceDetailsService;
    private final ApiCallsSenderService senderService;
    private final String serviceAddress;

    @Autowired
    public UnitDetailsService(ServiceDetailsService serviceDetailsService,
                              ApiCallsSenderService senderService,
                              @Value("${SERVICE.ADDRESS.UNIT}") String serviceAddress){
        this.serviceDetailsService = serviceDetailsService;
        this.senderService = senderService;
        this.serviceAddress = serviceAddress;
    }

    public String getUnitAddress(String unitId){
        Map<String, String> data = new HashMap<>();
        data.put("unitId", unitId);

        ResponseWrapper response = senderService.sendRequest(GetUnit.REQUEST_TEMPLATE_PATH, data, serviceAddress);
        return response.getStringAttribute(GetUnit.UNIT_ADDRESS);
    }

    public LocalDateTime getUnitCurrentLocalTime(String unitId){
        Map<String, String> data = new HashMap<>();
        data.put("unitId", unitId);

        ResponseWrapper response = senderService.sendRequest(GetUnitLocalTime.REQUEST_TEMPLATE_PATH, data, serviceAddress);
        return LocalDateTime.parse(response.getStringAttribute(GetUnitLocalTime.LOCAL_TIME));
    }

    public String getUnitAddressByServiceId(String serviceId){
        String unitId = serviceDetailsService.getUnitIdForService(serviceId);
        return getUnitAddress(unitId);
    }

    public LocalDateTime getUnitCurrentLocalTimeByServiceId(String serviceId){
        String unitId = serviceDetailsService.getUnitIdForService(serviceId);
        return getUnitCurrentLocalTime(unitId);
    }

    private class GetUnit {
        static final String REQUEST_TEMPLATE_PATH = "GetUnit.mustache";
        static final String UNIT_ADDRESS = "//GetResponse/GetResult/Address";
    }

    private class GetUnitLocalTime {
        static final String REQUEST_TEMPLATE_PATH = "GetUnitLocalTime.mustache";
        static final String LOCAL_TIME = "//GetLocalTimeResponse/GetLocalTimeResult";
    }
}
