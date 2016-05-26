package au.gov.dto.dibp.appointments.wallet;

import au.gov.dto.dibp.appointments.client.Client;
import au.gov.dto.dibp.appointments.qflowintegration.ApiCallsSenderService;
import au.gov.dto.dibp.appointments.util.ResponseWrapper;
import au.gov.dto.dibp.appointments.util.TemplateLoader;
import com.samskivert.mustache.Template;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Add and remove devices for sending push notifications on pass updates
 */
@Service
class DeviceRegistrationService {
    private static final Logger LOG = LoggerFactory.getLogger(DeviceRegistrationService.class);
    private static final String PROPERTY_NAME = "IOSDEVICELIBID";

    private final ApiCallsSenderService apiService;
    private final String serviceAddressCustomer;
    private final Template templateGetCustomPropertyByName;
    private final Template templateSetCustomProperty;

    @Autowired
    public DeviceRegistrationService(ApiCallsSenderService apiService,
                                     TemplateLoader templateLoader,
                                     @Value("${service.address.customer}") String serviceAddressCustomer) {
        this.apiService = apiService;
        this.serviceAddressCustomer = serviceAddressCustomer;
        this.templateGetCustomPropertyByName = templateLoader.loadRequestTemplate(GetCustomPropertyByName.REQUEST_TEMPLATE_PATH);
        this.templateSetCustomProperty = templateLoader.loadRequestTemplate(SetCustomProperty.REQUEST_TEMPLATE_PATH);
    }

    public void addDeviceForClient(Client client, String deviceLibraryIdentifier, String pushToken) {
        Map<String, String> deviceRegistrations = getDevicesForClient(client);
        deviceRegistrations.put(deviceLibraryIdentifier, pushToken);
        setDevicesForClient(client, deviceRegistrations);
    }

    public void removeDeviceForClient(Client client, String deviceLibraryIdentifier) {
        Map<String, String> deviceRegistrations = getDevicesForClient(client);
        deviceRegistrations.remove(deviceLibraryIdentifier);
        setDevicesForClient(client, deviceRegistrations);
    }

    public Map<String, String> getDevicesForClient(Client client) {
        ResponseWrapper response = apiService.sendRequest(templateGetCustomPropertyByName, new HashMap<String, String>() {{
            put("customerId", client.getCustomerId());
            put("propertyName", PROPERTY_NAME);
        }}, serviceAddressCustomer);
        String propertyValue = response.getStringAttribute(GetCustomPropertyByName.PROPERTY_VALUE);
        return deserialiseDeviceRegistrations(propertyValue);
    }

    void setDevicesForClient(Client client, Map<String, String> deviceRegistrations) {
        String propertyValue = serialiseDeviceRegistrations(deviceRegistrations);
        apiService.sendRequest(templateSetCustomProperty, new HashMap<String, String>() {{
            put("customerId", client.getCustomerId());
            put("propertyName", PROPERTY_NAME);
            put("propertyValue", propertyValue);
        }}, serviceAddressCustomer);
    }

    Map<String, String> deserialiseDeviceRegistrations(String deviceRegistrations) {
        Map<String, String> result = new HashMap<>();
        Arrays.stream(StringUtils.defaultString(deviceRegistrations).split("\\|")).forEach(deviceRegistration -> {
            String[] keyValue = deviceRegistration.split(":");
            if (keyValue.length == 2) {
                result.put(keyValue[0], keyValue[1]);
            }
        });
        return result;
    }

    String serialiseDeviceRegistrations(Map<String, String> deviceRegistrations) {
        String[] resultArray = deviceRegistrations
                .entrySet()
                .stream()
                .map(entry -> entry.getKey() + ":" + entry.getValue())
                .collect(Collectors.toList())
                .toArray(new String[deviceRegistrations.size()]);
        return StringUtils.join(resultArray, "|");
    }

    private class GetCustomPropertyByName {
        static final String REQUEST_TEMPLATE_PATH = "GetCustomPropertyByName.mustache";
        static final String PROPERTY_VALUE = "//GetCustomPropertyByNameResponse/GetCustomPropertyByNameResult/PropertyValue";
    }

    private class SetCustomProperty {
        static final String REQUEST_TEMPLATE_PATH = "SetCustomProperty.mustache";
    }
}
