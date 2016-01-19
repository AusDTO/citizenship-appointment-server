package au.gov.dto.dibp.appointments.organisation;

import au.gov.dto.dibp.appointments.qflowintegration.ApiCallsSenderService;
import au.gov.dto.dibp.appointments.util.ResponseWrapper;
import au.gov.dto.dibp.appointments.util.TemplateLoader;
import com.samskivert.mustache.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ServiceDetailsService {

    private final ApiCallsSenderService senderService;
    private final String serviceAddress;

    private final Template getServiceTemplate;
    private final Template getServiceByExternalReferenceTemplate;

    @Autowired
    public ServiceDetailsService(ApiCallsSenderService senderService,
                                 TemplateLoader templateLoader,
                                 @Value("${SERVICE.ADDRESS.SERVICE}") String serviceAddress) {
        this.senderService = senderService;
        this.serviceAddress = serviceAddress;

        getServiceTemplate = templateLoader.loadRequestTemplate(GetService.REQUEST_TEMPLATE_PATH);
        getServiceByExternalReferenceTemplate = templateLoader.loadRequestTemplate(GetServiceByExternalReference.REQUEST_TEMPLATE_PATH);
    }

    public String getUnitIdForService(String serviceId){
        Map<String, String> data = new HashMap<>();
        data.put("serviceId", serviceId);

        ResponseWrapper response = senderService.sendRequest(getServiceTemplate, data, serviceAddress);
        return getUnitIdFromGetServiceResponse(response);
    }

    public ServiceDetails getServiceByExternalReference(String externalReference){
        Map<String, String> data = new HashMap<>();
        data.put("externalReference", externalReference);

        ResponseWrapper response = senderService.sendRequest(getServiceByExternalReferenceTemplate, data, serviceAddress);
        return getServiceFromGetServiceByExternalReferenceResponse(response);
    }

    private String getUnitIdFromGetServiceResponse(ResponseWrapper responseWrapper){
        return responseWrapper.getStringAttribute(GetService.UNIT_ID);
    }


    private ServiceDetails getServiceFromGetServiceByExternalReferenceResponse(ResponseWrapper responseWrapper) {
        String serviceId = responseWrapper.getStringAttribute(GetServiceByExternalReference.SERVICE_ID);
        String unitId = responseWrapper.getStringAttribute(GetServiceByExternalReference.UNIT_ID);
        String serviceExtRef = responseWrapper.getStringAttribute(GetServiceByExternalReference.EXTERNAL_REFERENCE);

        return new ServiceDetails(unitId, serviceId, serviceExtRef);
    }

    private class GetService {
        static final String REQUEST_TEMPLATE_PATH = "GetService.mustache";
        static final String UNIT_ID = "//GetResponse/GetResult/UnitId";
    }

    private class GetServiceByExternalReference {
        static final String REQUEST_TEMPLATE_PATH = "GetServiceByExternalReference.mustache";
        static final String SERVICE_ID = "//GetByExtRefResponse/GetByExtRefResult/Service/Id";
        static final String UNIT_ID = "//GetByExtRefResponse/GetByExtRefResult/Service/UnitId";
        static final String EXTERNAL_REFERENCE = "//GetByExtRefResponse/GetByExtRefResult/Service/ExtRef";
    }
}
