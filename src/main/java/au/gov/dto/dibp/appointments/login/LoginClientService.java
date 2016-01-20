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

    private final Template getClientByPersonalIdTemplate;
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

        getClientByPersonalIdTemplate = templateLoader.loadRequestTemplate(GetClientByPersonalId.REQUEST_TEMPLATE_PATH);
        getClientCustomPropertiesTemplate = templateLoader.loadRequestTemplate(GetClientCustomProperties.REQUEST_TEMPLATE_PATH);
    }

    @Override
    public Client loadUserByUsername(String username) throws UsernameNotFoundException {
        if(!clientIdValidator.isClientIdValid(username)){
            throw new UsernameNotFoundException("Invalid format of the username");
        }

        Map<String, String> data = new HashMap<>();
        data.put("personalId", username);

        ResponseWrapper response = senderService.sendRequest(getClientByPersonalIdTemplate, data, serviceAddressCustomer);
        return parseGetCustomerByClientIdResponse(response);
    }

    private Client parseGetCustomerByClientIdResponse(ResponseWrapper response) {
        String clientId = response.getStringAttribute(GetClientByPersonalId.CUSTOMER_CLIENT_ID);
        String lastName = response.getStringAttribute(GetClientByPersonalId.CUSTOMER_LAST_NAME);
        String customerId = response.getStringAttribute(GetClientByPersonalId.CUSTOMER_ID);
        boolean hasEmail = StringUtil.isNotBlank(response.getStringAttribute(GetClientByPersonalId.CUSTOMER_EMAIL));
        boolean isActive = "true".equals(response.getStringAttribute(GetClientByPersonalId.CUSTOMER_ACTIVE));

        ResponseWrapper customPropertiesResponse = getCustomPropertiesResponse(customerId);
        ServiceDetails serviceDetails = getServiceDetails(customPropertiesResponse, customerId);
        String appointmentTypeId = getAppointmentTypeId(customPropertiesResponse, customerId);
        checkUserConfiguredCorrectly(serviceDetails, appointmentTypeId);

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

    private void checkUserConfiguredCorrectly(ServiceDetails serviceDetails, String appointmentTypeId){
        if(StringUtils.isEmpty(serviceDetails.getServiceId()) ||
                StringUtils.isEmpty(serviceDetails.getUnitId()) ||
                StringUtils.isEmpty(appointmentTypeId)){
            throw new UserDetailsNotFilledException("ServiceId or UnitId or AppointmentTypeId is not filled for the user.");
        }
    }

    private class GetClientByPersonalId {
        static final String REQUEST_TEMPLATE_PATH = "GetByPersonalId.mustache";
        private static final String CUSTOMER_EMAIL = "//GetByPersonalIdResponse/GetByPersonalIdResult/EMail";
        private static final String CUSTOMER_CLIENT_ID = "//GetByPersonalIdResponse/GetByPersonalIdResult/PersonalId";
        private static final String CUSTOMER_ACTIVE = "//GetByPersonalIdResponse/GetByPersonalIdResult/Active";
        private static final String CUSTOMER_LAST_NAME = "//GetByPersonalIdResponse/GetByPersonalIdResult/LastName";
        private static final String CUSTOMER_ID = "//GetByPersonalIdResponse/GetByPersonalIdResult/Id";
    }

    private class GetClientCustomProperties{
        static final String REQUEST_TEMPLATE_PATH = "GetCustomerCustomProperties.mustache";

        private static final String APPOINTMENT_TYPE_REF="//GetCustomPropertiesResponse/GetCustomPropertiesResult/CustomProperty[./Name='Appointment Types']/PropertyValue";
        private static final String SERVICE_REF="//GetCustomPropertiesResponse/GetCustomPropertiesResult/CustomProperty[./Name='InitialServiceExtRef']/PropertyValue";
    }

}
