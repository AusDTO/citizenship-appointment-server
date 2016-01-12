package au.gov.dto.dibp.appointments.login;

import au.gov.dto.dibp.appointments.client.Client;
import au.gov.dto.dibp.appointments.client.ClientIdValidator;
import au.gov.dto.dibp.appointments.organisation.ServiceDetails;
import au.gov.dto.dibp.appointments.organisation.ServiceDetailsService;
import au.gov.dto.dibp.appointments.qflowintegration.ApiCallsSenderService;
import au.gov.dto.dibp.appointments.util.ResponseWrapper;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class LoginClientServiceTest {

    private static final String UNIT_ID ="12";
    private static final String SERVICE_ID ="23";
    private static final String SERVICE_EXT_REF ="SYD_CI_20m";
    private static final String APPOINTMENT_TYPE_ID ="34";
    private static final String APPOINTMENT_TYPE_EXT_REF ="34";

    @Test
    public void getCustomerByExternalReference_shouldConvertResponseIntoCustomerObject() throws Exception {
        LoginClientService service = new LoginClientService(
                (String requestTemplatePath, Map<String, String> messageParams, String serviceAddress) -> getStandardResponse(requestTemplatePath, getCustomerCustomPropertiesBothValuesFilled()),
                getServiceDetailsService(),
                getAppointmentTypeService(),
                new ClientIdValidator(), "serviceUrl");

        Client client = service.loadUserByUsername("91919191919");

        assertThat(client.isEnabled(), is(true));
        assertThat(client.getUsername(), is("91919191919"));
        assertThat(client.getClientId(), is("91919191919"));
        assertThat(client.getPassword(), is("Smith"));
        assertThat(client.getCustomerId(), is("6"));
        assertThat(client.getServiceId(), is(SERVICE_ID));
        assertThat(client.getUnitId(), is(UNIT_ID));
        assertThat(client.getAppointmentTypeId(), is(APPOINTMENT_TYPE_ID));
        assertThat(client.hasEmail(), is(true));
    }

    @Test(expected = RuntimeException.class)
    public void getCustomerByExternalReference_shouldThrowAnExceptionIfUsernameIsNot11Digits() throws Exception {
        LoginClientService service = new LoginClientService(
                (String requestTemplatePath, Map<String, String> messageParams, String serviceAddress) -> getStandardResponse(requestTemplatePath, getCustomerCustomPropertiesBothValuesFilled()),
                getServiceDetailsService(),
                getAppointmentTypeService(),
                new ClientIdValidator(), "serviceUrl");

        service.loadUserByUsername("919191");
    }

    @Test(expected = UserDetailsNotFilledException.class)
    public void getCustomerByExternalReference_shouldThrowAnExceptionIfServiceReferenceIsNotSetOnClient() throws Exception {
        LoginClientService service = new LoginClientService(
                (String requestTemplatePath, Map<String, String> messageParams, String serviceAddress)
                        -> getStandardResponse(requestTemplatePath, getCustomerCustomPropertiesAppointmentTypeValueOnlyFilled()),
                getServiceDetailsService(),
                getAppointmentTypeService(),
                new ClientIdValidator(), "serviceUrl");

        service.loadUserByUsername("91919191919");
    }

    @Test(expected = UserDetailsNotFilledException.class)
    public void getCustomerByExternalReference_shouldThrowAnExceptionIfAppointmentTypeReferenceIsNotSetOnClient() throws Exception {
        LoginClientService service = new LoginClientService(
                (String requestTemplatePath, Map<String, String> messageParams, String serviceAddress)
                        -> getStandardResponse(requestTemplatePath, getCustomerCustomPropertiesServiceValueOnlyFilled()),
                getServiceDetailsService(),
                getAppointmentTypeService(),
                new ClientIdValidator(), "serviceUrl");

        service.loadUserByUsername("91919191919");
    }

    private ServiceDetailsService getServiceDetailsService(){
        return new ServiceDetailsService(null, "someOtherServiceUrl"){
            @Override
            public ServiceDetails getServiceByExternalReference(String externalReference){
                assertThat(SERVICE_EXT_REF, is(externalReference));

                return new ServiceDetails(UNIT_ID, SERVICE_ID, SERVICE_EXT_REF);
            }
        };
    }

    private AppointmentTypeService getAppointmentTypeService(){
        return new AppointmentTypeService(null, "someServiceUrl"){
            @Override
            public String getAppointmentTypeIdByExternalReference(String extRef){
                assertThat(APPOINTMENT_TYPE_EXT_REF, is(extRef));

                return APPOINTMENT_TYPE_ID;
            }
        };
    }

    private ResponseWrapper getStandardResponse(String requestTemplatePath, ResponseWrapper propertiesResponse) {
        if(requestTemplatePath.contains("GetByExtRef")) {
            String response =
                "<s:Envelope xmlns:s=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:a=\"http://www.w3.org/2005/08/addressing\">\n" +
                "   <s:Header/>\n" +
                "   <s:Body>\n" +
                "      <GetByExtRefResponse xmlns=\"http://www.qnomy.com/Services\">\n" +
                "         <GetByExtRefResult xmlns:b=\"http://schemas.datacontract.org/2004/07/QFlow.Library\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
                "            <b:Customer>\n" +
                "               <b:Active>true</b:Active>\n" +
                "               <b:ExtRef>91919191919</b:ExtRef>\n" +
                "               <b:Id>6</b:Id>\n" +
                "               <b:Name>Smith Martin</b:Name>\n" +
                "               <b:EMail>2323@test.com</b:EMail>\n" +
                "               <b:FirstName>Martin</b:FirstName>\n" +
                "               <b:LastName>Smith</b:LastName>\n" +
                "            </b:Customer>\n" +
                "         </GetByExtRefResult>\n" +
                "      </GetByExtRefResponse>\n" +
                "   </s:Body>\n" +
                "</s:Envelope>";

            return new ResponseWrapper(200, response);

        } else if( requestTemplatePath.contains("GetCustomerCustomProperties")){
            return propertiesResponse;
        }
        return null;
    }

    private ResponseWrapper getCustomerCustomPropertiesBothValuesFilled(){
        String response =
        "<s:Body>\n" +
        "      <GetCustomPropertiesResponse xmlns=\"http://www.qnomy.com/Services\">\n" +
        "         <GetCustomPropertiesResult xmlns:b=\"http://schemas.datacontract.org/2004/07/QFlow.Library\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
        "            <b:CustomProperty>\n" +
        "               <b:Active>false</b:Active>\n" +
        "               <b:ExtRef i:nil=\"true\"/>\n" +
        "               <b:Id>2</b:Id>\n" +
        "               <b:Mandatory>true</b:Mandatory>\n" +
        "               <b:Name>Appointment Types</b:Name>\n" +
        "               <b:ObjectType>User</b:ObjectType>\n" +
        "               <b:PropertyValue>" + APPOINTMENT_TYPE_EXT_REF + "</b:PropertyValue>\n" +
        "               <b:ValueList>STD_CIT_APPT</b:ValueList>\n" +
        "               <b:ValueType>List</b:ValueType>\n" +
        "            </b:CustomProperty>\n" +
        "            <b:CustomProperty>\n" +
        "               <b:Active>false</b:Active>\n" +
        "               <b:ExtRef i:nil=\"true\"/>\n" +
        "               <b:Id>3</b:Id>\n" +
        "               <b:Mandatory>true</b:Mandatory>\n" +
        "               <b:Name>InitialServiceExtRef</b:Name>\n" +
        "               <b:ObjectType>User</b:ObjectType>\n" +
        "               <b:PropertyValue>" + SERVICE_EXT_REF + "</b:PropertyValue>\n" +
        "               <b:ValueList>SYD_CI_20m, MEL_CI_20m</b:ValueList>\n" +
        "               <b:ValueType>List</b:ValueType>\n" +
        "            </b:CustomProperty>\n" +
        "         </GetCustomPropertiesResult>\n" +
        "      </GetCustomPropertiesResponse>\n" +
        "   </s:Body>";
        return new ResponseWrapper(200, response);
    }

    private ResponseWrapper getCustomerCustomPropertiesServiceValueOnlyFilled(){
        String response =
        "<s:Body>\n" +
        "      <GetCustomPropertiesResponse xmlns=\"http://www.qnomy.com/Services\">\n" +
        "         <GetCustomPropertiesResult xmlns:b=\"http://schemas.datacontract.org/2004/07/QFlow.Library\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
        "            <b:CustomProperty>\n" +
        "               <b:Active>false</b:Active>\n" +
        "               <b:ExtRef i:nil=\"true\"/>\n" +
        "               <b:Id>3</b:Id>\n" +
        "               <b:Mandatory>true</b:Mandatory>\n" +
        "               <b:Name>InitialServiceExtRef</b:Name>\n" +
        "               <b:ObjectType>User</b:ObjectType>\n" +
        "               <b:PropertyValue>" + SERVICE_EXT_REF + "</b:PropertyValue>\n" +
        "               <b:ValueList>SYD_CI_20m, MEL_CI_20m</b:ValueList>\n" +
        "               <b:ValueType>List</b:ValueType>\n" +
        "            </b:CustomProperty>\n" +
        "         </GetCustomPropertiesResult>\n" +
        "      </GetCustomPropertiesResponse>\n" +
        "   </s:Body>";
        return new ResponseWrapper(200, response);
    }

    private ResponseWrapper getCustomerCustomPropertiesAppointmentTypeValueOnlyFilled(){
        String response =
        "<s:Body>\n" +
        "      <GetCustomPropertiesResponse xmlns=\"http://www.qnomy.com/Services\">\n" +
        "         <GetCustomPropertiesResult xmlns:b=\"http://schemas.datacontract.org/2004/07/QFlow.Library\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
        "            <b:CustomProperty>\n" +
        "               <b:Active>false</b:Active>\n" +
        "               <b:ExtRef i:nil=\"true\"/>\n" +
        "               <b:Id>2</b:Id>\n" +
        "               <b:Mandatory>true</b:Mandatory>\n" +
        "               <b:Name>Appointment Types</b:Name>\n" +
        "               <b:ObjectType>User</b:ObjectType>\n" +
        "               <b:PropertyValue>" + APPOINTMENT_TYPE_EXT_REF + "</b:PropertyValue>\n" +
        "               <b:ValueList>STD_CIT_APPT</b:ValueList>\n" +
        "               <b:ValueType>List</b:ValueType>\n" +
        "            </b:CustomProperty>\n" +
        "         </GetCustomPropertiesResult>\n" +
        "      </GetCustomPropertiesResponse>\n" +
        "   </s:Body>";
        return new ResponseWrapper(200, response);
    }
}
