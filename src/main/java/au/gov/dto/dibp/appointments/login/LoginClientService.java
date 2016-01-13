package au.gov.dto.dibp.appointments.login;

import au.gov.dto.dibp.appointments.client.Client;
import au.gov.dto.dibp.appointments.client.ClientIdValidator;
import au.gov.dto.dibp.appointments.organisation.ServiceDetails;
import au.gov.dto.dibp.appointments.qflowintegration.ApiCallsSenderService;
import au.gov.dto.dibp.appointments.organisation.ServiceDetailsService;
import au.gov.dto.dibp.appointments.util.ResponseWrapper;
import au.gov.dto.dibp.appointments.util.TemplateLoader;
import com.samskivert.mustache.Template;
import org.eclipse.jetty.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Service
public class LoginClientService implements UserDetailsService {

    private final ApiCallsSenderService senderService;
    private final ServiceDetailsService serviceDetailsService;
    private final AppointmentTypeService appointmentTypeService;

    private final String serviceAddressCustomer;
    private final ClientIdValidator clientIdValidator;

    private final Template getClientByExternalReferenceTemplate;
    private final Template getClientCustomPropertiesTemplate;


    @Autowired
    public LoginClientService(ApiCallsSenderService senderService,
                              ServiceDetailsService serviceDetailsService,
                              AppointmentTypeService appointmentTypeService,
                              TemplateLoader templateLoader,
                              ClientIdValidator clientIdValidator,
                              @Value("${SERVICE.ADDRESS.CUSTOMER}") String serviceAddressCustomer) {
        this.senderService = senderService;
        this.clientIdValidator = clientIdValidator;
        this.serviceDetailsService = serviceDetailsService;
        this.appointmentTypeService = appointmentTypeService;
        this.serviceAddressCustomer = serviceAddressCustomer;

        getClientByExternalReferenceTemplate = templateLoader.loadTemplateByPath(GetClientByExternalReference.REQUEST_TEMPLATE_PATH);
        getClientCustomPropertiesTemplate = templateLoader.loadTemplateByPath(GetClientCustomProperties.REQUEST_TEMPLATE_PATH);
    }

    @Override
    public Client loadUserByUsername(String username) throws UsernameNotFoundException {
        if(!clientIdValidator.isClientIdValid(username)){
            throw new RuntimeException("Invalid format of the username");
        }

        Map<String, String> data = new HashMap<>();
        data.put("externalReference", username);

        ResponseWrapper response = senderService.sendRequest(getClientByExternalReferenceTemplate, data, serviceAddressCustomer);
        return parseGetCustomerByClientIdResponse(response);
    }

    private Client parseGetCustomerByClientIdResponse(ResponseWrapper response) {
        String clientId = response.getStringAttribute(GetClientByExternalReference.CUSTOMER_CLIENT_ID);
        String lastName = response.getStringAttribute(GetClientByExternalReference.CUSTOMER_LAST_NAME);
        String customerId = response.getStringAttribute(GetClientByExternalReference.CUSTOMER_ID);
        boolean hasEmail = StringUtil.isNotBlank(response.getStringAttribute(GetClientByExternalReference.CUSTOMER_EMAIL));
        boolean isActive = "true".equals(response.getStringAttribute(GetClientByExternalReference.CUSTOMER_ACTIVE));

        ResponseWrapper customPropertiesResponse = getCustomPropertiesResponse(customerId);
        ServiceDetails serviceDetails = getServiceDetails(customPropertiesResponse, customerId);
        String appointmentTypeId = getAppointmentTypeId(customPropertiesResponse, customerId);

        return new Client(clientId, lastName, customerId, hasEmail, serviceDetails.getUnitId(), serviceDetails.getServiceId(), appointmentTypeId, isActive);
    }

    private ResponseWrapper getCustomPropertiesResponse(String customerId){
        Map<String, String> data = new HashMap<>();
        data.put("customerId", customerId);

        return senderService.sendRequest(getClientCustomPropertiesTemplate, data, serviceAddressCustomer);
    }

    private String getAppointmentTypeId(ResponseWrapper response, String customerId){
        final String appointmentTypeReference = response.getStringAttribute(GetClientCustomProperties.APPOINTMENT_TYPE_REF);
        if(StringUtils.isEmpty(appointmentTypeReference)){
            throw new UserDetailsNotFilledException("Appointment type reference is missing for the user " + customerId);
        }

        return appointmentTypeService.getAppointmentTypeIdByExternalReference(appointmentTypeReference);
    }

    private ServiceDetails getServiceDetails(ResponseWrapper response, String customerId){
        final String serviceReference = response.getStringAttribute(GetClientCustomProperties.SERVICE_REF);
        if(StringUtils.isEmpty(serviceReference)){
            throw new UserDetailsNotFilledException("Service reference is missing for the user " + customerId);
        }

        return serviceDetailsService.getServiceByExternalReference(response.getStringAttribute(GetClientCustomProperties.SERVICE_REF));
    }

    private class GetClientByExternalReference {
        static final String REQUEST_TEMPLATE_PATH = "GetByExtRef.mustache";
        private static final String CUSTOMER_EMAIL = "//GetByExtRefResponse/GetByExtRefResult/Customer/EMail";
        private static final String CUSTOMER_CLIENT_ID = "//GetByExtRefResponse/GetByExtRefResult/Customer/ExtRef";
        private static final String CUSTOMER_ACTIVE = "//GetByExtRefResponse/GetByExtRefResult/Customer/Active";
        private static final String CUSTOMER_LAST_NAME = "//GetByExtRefResponse/GetByExtRefResult/Customer/LastName";
        private static final String CUSTOMER_ID = "//GetByExtRefResponse/GetByExtRefResult/Customer/Id";
    }

    private class GetClientCustomProperties{
        static final String REQUEST_TEMPLATE_PATH = "GetCustomerCustomProperties.mustache";

        private static final String APPOINTMENT_TYPE_REF="//GetCustomPropertiesResponse/GetCustomPropertiesResult/CustomProperty[./Name='Appointment Types']/PropertyValue";
        private static final String SERVICE_REF="//GetCustomPropertiesResponse/GetCustomPropertiesResult/CustomProperty[./Name='InitialServiceExtRef']/PropertyValue";
    }

}
