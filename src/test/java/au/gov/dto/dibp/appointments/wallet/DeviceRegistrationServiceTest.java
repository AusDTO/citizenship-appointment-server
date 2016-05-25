package au.gov.dto.dibp.appointments.wallet;

import au.gov.dto.dibp.appointments.client.Client;
import au.gov.dto.dibp.appointments.qflowintegration.ApiCallsSenderService;
import au.gov.dto.dibp.appointments.util.FakeTemplateLoader;
import au.gov.dto.dibp.appointments.util.ResponseWrapper;
import com.samskivert.mustache.Template;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class DeviceRegistrationServiceTest {
    @Test
    public void testGetDevicesForClientWhenEmptySet() throws Exception {
        DeviceRegistrationService service = new DeviceRegistrationService(getApiServiceReturningEmptyPropertyValue(), new FakeTemplateLoader(), "");

        Map<String, String> devices = service.getDevicesForClient(new Client("clientId", "familyName", "customerId", true, true, "unitId", "serviceId", "appointmentTypeId", true));

        assertThat(devices.size(), equalTo(0));
    }

    @Test
    public void testGetDevicesForClientWithSingleDevice() throws Exception {
        DeviceRegistrationService service = new DeviceRegistrationService(getApiServiceReturningPropertyValueWithSingleDeviceRegistration(), new FakeTemplateLoader(), "");

        Map<String, String> devices = service.getDevicesForClient(new Client("clientId", "familyName", "customerId", true, true, "unitId", "serviceId", "appointmentTypeId", true));

        assertThat(devices.size(), equalTo(1));
        assertThat(devices.get("deviceLibraryIdentifier"), equalTo("pushToken"));
    }

    @Test
    public void testGetDevicesForClientWithMultipleDevices() throws Exception {
        DeviceRegistrationService service = new DeviceRegistrationService(getApiServiceReturningPropertyValueWithTwoDeviceRegistrations(), new FakeTemplateLoader(), "");

        Map<String, String> devices = service.getDevicesForClient(new Client("clientId", "familyName", "customerId", true, true, "unitId", "serviceId", "appointmentTypeId", true));

        assertThat(devices.size(), equalTo(2));
        assertThat(devices.get("deviceLibraryIdentifier1"), equalTo("pushToken1"));
        assertThat(devices.get("deviceLibraryIdentifier2"), equalTo("pushToken2"));
    }

    @Test
    public void testDeviceRegistrationSerialisationForEmptySet() throws Exception {
        DeviceRegistrationService service = new DeviceRegistrationService(null, new FakeTemplateLoader(), null);

        String serialisedDeviceRegistrations = service.serialiseDeviceRegistrations(Collections.emptyMap());

        assertThat(serialisedDeviceRegistrations, equalTo(""));
    }

    @Test
    public void testDeviceRegistrationSerialisationForSingleReservation() throws Exception {
        DeviceRegistrationService service = new DeviceRegistrationService(null, new FakeTemplateLoader(), null);

        String serialisedDeviceRegistrations = service.serialiseDeviceRegistrations(new HashMap<String, String>() {{
            put("deviceLibraryIdentifier", "pushToken");
        }});

        assertThat(serialisedDeviceRegistrations, equalTo("deviceLibraryIdentifier:pushToken"));
    }

    @Test
    public void testDeviceRegistrationSerialisationForMultipleReservations() throws Exception {
        DeviceRegistrationService service = new DeviceRegistrationService(null, new FakeTemplateLoader(), null);

        String serialisedDeviceRegistrations = service.serialiseDeviceRegistrations(new HashMap<String, String>() {{
            put("deviceLibraryIdentifier1", "pushToken1");
            put("deviceLibraryIdentifier2", "pushToken2");
        }});

        assertThat(serialisedDeviceRegistrations, equalTo("deviceLibraryIdentifier1:pushToken1|deviceLibraryIdentifier2:pushToken2"));
    }

    @Test
    public void testDeviceRegistrationDeserialisationForEmptySet() throws Exception {
        DeviceRegistrationService service = new DeviceRegistrationService(null, new FakeTemplateLoader(), null);

        Map<String, String> deserialisedDeviceRegistrations = service.deserialiseDeviceRegistrations("");

        assertThat(deserialisedDeviceRegistrations.size(), equalTo(0));
    }

    @Test
    public void testDeviceRegistrationDeserialisationForSingleReservation() throws Exception {
        DeviceRegistrationService service = new DeviceRegistrationService(null, new FakeTemplateLoader(), null);

        Map<String, String> deserialisedDeviceRegistrations = service.deserialiseDeviceRegistrations("deviceLibraryIdentifier:pushToken");

        assertThat(deserialisedDeviceRegistrations.size(), equalTo(1));
        assertThat(deserialisedDeviceRegistrations.get("deviceLibraryIdentifier"), equalTo("pushToken"));
    }

    @Test
    public void testDeviceRegistrationDeserialisationForMultipleReservations() throws Exception {
        DeviceRegistrationService service = new DeviceRegistrationService(null, new FakeTemplateLoader(), null);

        Map<String, String> deserialisedDeviceRegistrations = service.deserialiseDeviceRegistrations("deviceLibraryIdentifier1:pushToken1|deviceLibraryIdentifier2:pushToken2");

        assertThat(deserialisedDeviceRegistrations.size(), equalTo(2));
        assertThat(deserialisedDeviceRegistrations.get("deviceLibraryIdentifier1"), equalTo("pushToken1"));
        assertThat(deserialisedDeviceRegistrations.get("deviceLibraryIdentifier2"), equalTo("pushToken2"));
    }

    private ApiCallsSenderService getApiServiceReturningSuccessfulPropertyValueSave(){
        return (Template requestTemplate, Map<String, String> messageParams, String serviceAddress) -> {
            String responseWithEmptyPropertyValue =
                    "   <s:Body>\n" +
                    "      <SetCustomPropertyResponse xmlns=\"http://www.qnomy.com/Services\"/>\n" +
                    "   </s:Body>";
            return new ResponseWrapper(200, responseWithEmptyPropertyValue);
        };
    }

    private ApiCallsSenderService getApiServiceReturningFailedPropertyValueSaveDueToExceedsLength(){
        return (Template requestTemplate, Map<String, String> messageParams, String serviceAddress) -> {
            String responseWithEmptyPropertyValue =
                    "   <s:Body>\n" +
                    "      <s:Fault>\n" +
                    "         <s:Code>\n" +
                    "            <s:Value>s:Sender</s:Value>\n" +
                    "         </s:Code>\n" +
                    "         <s:Reason>\n" +
                    "            <s:Text xml:lang=\"en-US\">Error message</s:Text>\n" +
                    "         </s:Reason>\n" +
                    "         <s:Detail>\n" +
                    "            <QFlowAPIApplicationException xmlns=\"http://schemas.datacontract.org/2004/07/QFlow.Web.API\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
                    "               <ErrorNumber>0</ErrorNumber>\n" +
                    "            </QFlowAPIApplicationException>\n" +
                    "         </s:Detail>\n" +
                    "      </s:Fault>\n" +
                    "   </s:Body>";
            return new ResponseWrapper(500, responseWithEmptyPropertyValue);
        };
    }

    private ApiCallsSenderService getApiServiceReturningEmptyPropertyValue(){
        return (Template requestTemplate, Map<String, String> messageParams, String serviceAddress) -> {
            String responseWithEmptyPropertyValue =
                    "   <s:Body>\n" +
                    "      <GetCustomPropertyByNameResponse xmlns=\"http://www.qnomy.com/Services\">\n" +
                    "         <GetCustomPropertyByNameResult xmlns:b=\"http://schemas.datacontract.org/2004/07/QFlow.Library\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
                    "            <b:AccessType>0</b:AccessType>\n" +
                    "            <b:Active>false</b:Active>\n" +
                    "            <b:ActiveDirectoryName i:nil=\"true\"/>\n" +
                    "            <b:DefaultValue i:nil=\"true\"/>\n" +
                    "            <b:Description i:nil=\"true\"/>\n" +
                    "            <b:ExtRef i:nil=\"true\"/>\n" +
                    "            <b:FunctionName i:nil=\"true\"/>\n" +
                    "            <b:GroupIds i:nil=\"true\" xmlns:c=\"http://schemas.microsoft.com/2003/10/Serialization/Arrays\"/>\n" +
                    "            <b:HideFromAgents>false</b:HideFromAgents>\n" +
                    "            <b:Id>10</b:Id>\n" +
                    "            <b:ImageSource i:nil=\"true\"/>\n" +
                    "            <b:Mandatory>false</b:Mandatory>\n" +
                    "            <b:Name>IOSDEVICELIBID</b:Name>\n" +
                    "            <b:ObjectType>User</b:ObjectType>\n" +
                    "            <b:PropertyValue/>\n" +
                    "            <b:ValueList i:nil=\"true\"/>\n" +
                    "            <b:ValueType>Text</b:ValueType>\n" +
                    "            <b:ValuesFunction i:nil=\"true\" xmlns:c=\"http://schemas.microsoft.com/2003/10/Serialization/Arrays\"/>\n" +
                    "         </GetCustomPropertyByNameResult>\n" +
                    "      </GetCustomPropertyByNameResponse>\n" +
                    "   </s:Body>";
            return new ResponseWrapper(200, responseWithEmptyPropertyValue);
        };
    }

    private ApiCallsSenderService getApiServiceReturningPropertyValueWithSingleDeviceRegistration(){
        return (Template requestTemplate, Map<String, String> messageParams, String serviceAddress) -> {
            String responseWithSingleDeviceRegistration =
                    "   <s:Body>\n" +
                    "      <GetCustomPropertyByNameResponse xmlns=\"http://www.qnomy.com/Services\">\n" +
                    "         <GetCustomPropertyByNameResult xmlns:b=\"http://schemas.datacontract.org/2004/07/QFlow.Library\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
                    "            <b:AccessType>0</b:AccessType>\n" +
                    "            <b:Active>false</b:Active>\n" +
                    "            <b:ActiveDirectoryName i:nil=\"true\"/>\n" +
                    "            <b:DefaultValue i:nil=\"true\"/>\n" +
                    "            <b:Description i:nil=\"true\"/>\n" +
                    "            <b:ExtRef i:nil=\"true\"/>\n" +
                    "            <b:FunctionName i:nil=\"true\"/>\n" +
                    "            <b:GroupIds i:nil=\"true\" xmlns:c=\"http://schemas.microsoft.com/2003/10/Serialization/Arrays\"/>\n" +
                    "            <b:HideFromAgents>false</b:HideFromAgents>\n" +
                    "            <b:Id>10</b:Id>\n" +
                    "            <b:ImageSource i:nil=\"true\"/>\n" +
                    "            <b:Mandatory>false</b:Mandatory>\n" +
                    "            <b:Name>IOSDEVICELIBID</b:Name>\n" +
                    "            <b:ObjectType>User</b:ObjectType>\n" +
                    "            <b:PropertyValue>deviceLibraryIdentifier:pushToken</b:PropertyValue>\n" +
                    "            <b:ValueList i:nil=\"true\"/>\n" +
                    "            <b:ValueType>Text</b:ValueType>\n" +
                    "            <b:ValuesFunction i:nil=\"true\" xmlns:c=\"http://schemas.microsoft.com/2003/10/Serialization/Arrays\"/>\n" +
                    "         </GetCustomPropertyByNameResult>\n" +
                    "      </GetCustomPropertyByNameResponse>\n" +
                    "   </s:Body>";
            return new ResponseWrapper(200, responseWithSingleDeviceRegistration);
        };
    }

    private ApiCallsSenderService getApiServiceReturningPropertyValueWithTwoDeviceRegistrations(){
        return (Template requestTemplate, Map<String, String> messageParams, String serviceAddress) -> {
            String responseWithTwoDeviceRegistrations =
                    "   <s:Body>\n" +
                    "      <GetCustomPropertyByNameResponse xmlns=\"http://www.qnomy.com/Services\">\n" +
                    "         <GetCustomPropertyByNameResult xmlns:b=\"http://schemas.datacontract.org/2004/07/QFlow.Library\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
                    "            <b:AccessType>0</b:AccessType>\n" +
                    "            <b:Active>false</b:Active>\n" +
                    "            <b:ActiveDirectoryName i:nil=\"true\"/>\n" +
                    "            <b:DefaultValue i:nil=\"true\"/>\n" +
                    "            <b:Description i:nil=\"true\"/>\n" +
                    "            <b:ExtRef i:nil=\"true\"/>\n" +
                    "            <b:FunctionName i:nil=\"true\"/>\n" +
                    "            <b:GroupIds i:nil=\"true\" xmlns:c=\"http://schemas.microsoft.com/2003/10/Serialization/Arrays\"/>\n" +
                    "            <b:HideFromAgents>false</b:HideFromAgents>\n" +
                    "            <b:Id>10</b:Id>\n" +
                    "            <b:ImageSource i:nil=\"true\"/>\n" +
                    "            <b:Mandatory>false</b:Mandatory>\n" +
                    "            <b:Name>IOSDEVICELIBID</b:Name>\n" +
                    "            <b:ObjectType>User</b:ObjectType>\n" +
                    "            <b:PropertyValue>deviceLibraryIdentifier1:pushToken1|deviceLibraryIdentifier2:pushToken2</b:PropertyValue>\n" +
                    "            <b:ValueList i:nil=\"true\"/>\n" +
                    "            <b:ValueType>Text</b:ValueType>\n" +
                    "            <b:ValuesFunction i:nil=\"true\" xmlns:c=\"http://schemas.microsoft.com/2003/10/Serialization/Arrays\"/>\n" +
                    "         </GetCustomPropertyByNameResult>\n" +
                    "      </GetCustomPropertyByNameResponse>\n" +
                    "   </s:Body>";
            return new ResponseWrapper(200, responseWithTwoDeviceRegistrations);
        };
    }
}
