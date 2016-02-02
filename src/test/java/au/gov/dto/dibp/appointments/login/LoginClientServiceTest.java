package au.gov.dto.dibp.appointments.login;

import au.gov.dto.dibp.appointments.client.Client;
import au.gov.dto.dibp.appointments.client.ClientIdValidator;
import au.gov.dto.dibp.appointments.organisation.ServiceDetailsService;
import au.gov.dto.dibp.appointments.qflowintegration.ApiCallsSenderService;
import au.gov.dto.dibp.appointments.util.FakeTemplateLoader;
import au.gov.dto.dibp.appointments.util.ResponseWrapper;
import au.gov.dto.dibp.appointments.util.TemplateLoader;
import com.samskivert.mustache.Template;
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
    public void getCustomerByPersonalId_shouldConvertResponseIntoClientObject() throws Exception {
        ApiCallsSenderService senderService = (Template requestTemplate, Map<String, String> messageParams, String serviceAddress)
                -> getCallsResponse(requestTemplate, getCustomerCustomPropertiesBothValuesFilled());
        TemplateLoader templateLoader = new FakeTemplateLoader();

        ServiceDetailsService serviceDetailsService = new ServiceDetailsService(senderService, templateLoader, "SomeUrl");
        AppointmentTypeService appointmentTypeService = new AppointmentTypeService(senderService, templateLoader, "SomeUrl");

        LoginClientService service = new LoginClientService( senderService, serviceDetailsService, appointmentTypeService,
                templateLoader, new ClientIdValidator(), "serviceUrl");

        Client client = service.loadUserByUsername("91919191919");

        assertThat(client.isEnabled(), is(true));
        assertThat(client.getUsername(), is("91919191919"));
        assertThat(client.getClientId(), is("91919191919"));
        assertThat(client.getPassword(), is("Smith"));
        assertThat(client.getCustomerId(), is("6"));
        assertThat(client.getServiceId(), is(SERVICE_ID));
        assertThat(client.getUnitId(), is(UNIT_ID));
        assertThat(client.getAppointmentTypeId(), is(APPOINTMENT_TYPE_ID));
        assertThat(client.isEmail(), is(true));
        assertThat(client.isMobile(), is(true));
    }

    @Test(expected = RuntimeException.class)
    public void getCustomerByPersonalId_shouldThrowAnExceptionIfUsernameIsNot11Digits() throws Exception {
        ApiCallsSenderService senderService = (Template requestTemplate, Map<String, String> messageParams, String serviceAddress)
                -> getCallsResponse(requestTemplate, getCustomerCustomPropertiesBothValuesFilled());
        TemplateLoader templateLoader = new FakeTemplateLoader();

        ServiceDetailsService serviceDetailsService = new ServiceDetailsService(senderService, templateLoader, "SomeUrl");
        AppointmentTypeService appointmentTypeService = new AppointmentTypeService(senderService, templateLoader, "SomeUrl");

        LoginClientService service = new LoginClientService( senderService, serviceDetailsService, appointmentTypeService,
                templateLoader, new ClientIdValidator(), "serviceUrl");

        service.loadUserByUsername("919191");
    }

    @Test(expected = UserDetailsNotFilledException.class)
    public void getCustomerByPersonalId_shouldThrowAnExceptionIfServiceReferenceIsNotSetOnClient() throws Exception {
        ApiCallsSenderService senderService = (Template requestTemplate, Map<String, String> messageParams, String serviceAddress)
                -> getCallsResponse(requestTemplate, getCustomerCustomPropertiesAppointmentTypeValueOnlyFilled());
        TemplateLoader templateLoader = new FakeTemplateLoader();

        ServiceDetailsService serviceDetailsService = new ServiceDetailsService(senderService, templateLoader, "SomeUrl");
        AppointmentTypeService appointmentTypeService = new AppointmentTypeService(senderService, templateLoader, "SomeUrl");

        LoginClientService service = new LoginClientService( senderService, serviceDetailsService, appointmentTypeService,
                templateLoader, new ClientIdValidator(), "serviceUrl");

        service.loadUserByUsername("91919191919");
    }

    @Test(expected = UserDetailsNotFilledException.class)
    public void getCustomerByPersonalId_shouldThrowAnExceptionIfAppointmentTypeReferenceIsNotSetOnClient() throws Exception {
        ApiCallsSenderService senderService = (Template requestTemplate, Map<String, String> messageParams, String serviceAddress)
                -> getCallsResponse(requestTemplate, getCustomerCustomPropertiesServiceValueOnlyFilled());
        TemplateLoader templateLoader = new FakeTemplateLoader();

        ServiceDetailsService serviceDetailsService = new ServiceDetailsService(senderService, templateLoader, "SomeUrl");
        AppointmentTypeService appointmentTypeService = new AppointmentTypeService(senderService, templateLoader, "SomeUrl");

        LoginClientService service = new LoginClientService( senderService, serviceDetailsService, appointmentTypeService,
                templateLoader, new ClientIdValidator(), "serviceUrl");

        service.loadUserByUsername("91919191919");
    }

    private ResponseWrapper getCallsResponse(Template requestTemplate, String customPropertiesResponse){
        String response = null;
        if(requestTemplate.toString().contains("GetByPersonalId.")){
            response = getCustomerByPersonalIdResponse();
        } else if(requestTemplate.toString().contains("GetCustomerCustomProperties.")){
            response = customPropertiesResponse;
        } else if (requestTemplate.toString().contains("GetServiceByExternalReference.")){
            response = getServiceByExternalReferenceResponse();
        } else if(requestTemplate.toString().contains("GetAppointmentTypeByExternalReference.")){
            response = getAppointmentTypeByExternalReferenceResponse();
        }
        return new ResponseWrapper(200, response);
    }

    private String getCustomerByPersonalIdResponse() {
        return
        "<s:Envelope xmlns:s=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:a=\"http://www.w3.org/2005/08/addressing\">\n" +
        "   <s:Header/>\n" +
        "   <s:Body>\n" +
        "      <GetByPersonalIdResponse xmlns=\"http://www.qnomy.com/Services\">\n" +
        "         <GetByPersonalIdResult xmlns:b=\"http://schemas.datacontract.org/2004/07/QFlow.Library\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
        "            <b:Active>true</b:Active>\n" +
        "            <b:PersonalId>91919191919</b:PersonalId>\n" +
        "            <b:Id>6</b:Id>\n" +
        "            <b:Name>Smith Martin</b:Name>\n" +
        "            <b:EMail>2323@test.com</b:EMail>\n" +
        "            <b:TelNumber1>0483737373</b:TelNumber1>" +
        "            <b:FirstName>Martin</b:FirstName>\n" +
        "            <b:LastName>Smith</b:LastName>\n" +
        "         </GetByPersonalIdResult>\n" +
        "      </GetByPersonalIdResponse>\n" +
        "   </s:Body>\n" +
        "</s:Envelope>";
    }

    private String getCustomerCustomPropertiesBothValuesFilled(){
        return
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
    }

    private String getCustomerCustomPropertiesServiceValueOnlyFilled(){
        return
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
    }

    private String getCustomerCustomPropertiesAppointmentTypeValueOnlyFilled(){
        return
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
    }

    private String getServiceByExternalReferenceResponse(){
        return
        "<s:Body>\n" +
        "      <GetByExtRefResponse xmlns=\"http://www.qnomy.com/Services\">\n" +
        "         <GetByExtRefResult xmlns:b=\"http://schemas.datacontract.org/2004/07/QFlow.Library\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
        "            <b:Service>\n" +
        "               <b:Active>true</b:Active>\n" +
        "               <b:ExtRef>" + SERVICE_EXT_REF + "</b:ExtRef>\n" +
        "               <b:Id>" + SERVICE_ID + "</b:Id>\n" +
        "               <b:Name>Citizenship Interview 20min</b:Name>\n" +
        "               <b:UnitId>" + UNIT_ID + "</b:UnitId>\n" +
        "            </b:Service>\n" +
        "         </GetByExtRefResult>\n" +
        "      </GetByExtRefResponse>\n" +
        "   </s:Body>";
    }

    private String getAppointmentTypeByExternalReferenceResponse(){
        return
        " <s:Body>\n" +
        "      <GetByExtRefResponse xmlns=\"http://www.qnomy.com/Services\">\n" +
        "         <GetByExtRefResult xmlns:b=\"http://schemas.datacontract.org/2004/07/QFlow.Library\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
        "            <b:AppointmentType>\n" +
        "               <b:Active>true</b:Active>\n" +
        "               <b:ExtRef>" + APPOINTMENT_TYPE_EXT_REF + "</b:ExtRef>\n" +
        "               <b:Id>" + APPOINTMENT_TYPE_ID + "</b:Id>\n" +
        "               <b:Name>Standard Citizenship Appointment</b:Name>\n" +
        "               <b:Duration>20</b:Duration>\n" +
        "               <b:IsRestricted>false</b:IsRestricted>\n" +
        "               <b:ServiceTypeId>1</b:ServiceTypeId>\n" +
        "               <b:ServiceTypeName/>\n" +
        "            </b:AppointmentType>\n" +
        "         </GetByExtRefResult>\n" +
        "      </GetByExtRefResponse>\n" +
        "   </s:Body>";
    }
}
