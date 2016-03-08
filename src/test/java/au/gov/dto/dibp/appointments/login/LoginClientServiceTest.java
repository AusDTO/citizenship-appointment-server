package au.gov.dto.dibp.appointments.login;

import au.gov.dto.dibp.appointments.client.Client;
import au.gov.dto.dibp.appointments.client.ClientIdValidator;
import au.gov.dto.dibp.appointments.organisation.ServiceDetailsService;
import au.gov.dto.dibp.appointments.qflowintegration.ApiCallsSenderService;
import au.gov.dto.dibp.appointments.qflowintegration.ApiResponseNotSuccessfulException;
import au.gov.dto.dibp.appointments.util.FakeTemplateLoader;
import au.gov.dto.dibp.appointments.util.ResponseWrapper;
import au.gov.dto.dibp.appointments.util.TemplateLoader;
import com.samskivert.mustache.Template;
import org.junit.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class LoginClientServiceTest {

    private static final String UNIT_ID ="12";
    private static final String SERVICE_ID ="23";
    private static final String SERVICE_EXT_REF ="serviceExtRef";
    private static final String APPOINTMENT_TYPE_ID ="34";
    private static final String APPOINTMENT_TYPE_EXT_REF ="34";

    @Test
    public void getCustomerByPersonalId_shouldConvertResponseIntoClientObject() throws Exception {
        ApiCallsSenderService senderService = (Template requestTemplate, Map<String, String> messageParams, String serviceAddress) -> {
            if (requestTemplate.toString().contains("GetByPersonalId.")) {
                return new ResponseWrapper(200, getCustomerByPersonalIdResponse());
            } else if (requestTemplate.toString().contains("GetCustomerCustomProperties.")) {
                return new ResponseWrapper(200, getCustomerCustomPropertiesBothValuesFilled());
            } else if (requestTemplate.toString().contains("GetServiceByExternalReference.")) {
                return new ResponseWrapper(200, getServiceByExternalReferenceResponse());
            } else if (requestTemplate.toString().contains("GetAppointmentTypeByExternalReference.")) {
                return new ResponseWrapper(200, getAppointmentTypeByExternalReferenceResponse());
            } else {
                throw new RuntimeException("Should not get here");
            }
        };
        TemplateLoader templateLoader = new FakeTemplateLoader();

        ServiceDetailsService serviceDetailsService = new ServiceDetailsService(senderService, templateLoader, "SomeUrl");
        AppointmentTypeService appointmentTypeService = new AppointmentTypeService(senderService, templateLoader, "SomeUrl");

        LoginClientService service = new LoginClientService( senderService, serviceDetailsService, appointmentTypeService, templateLoader, new ClientIdValidator(), "serviceUrl");

        Client client = service.loadUserByUsername("11111111111");

        assertThat(client.isEnabled(), is(true));
        assertThat(client.getUsername(), is("11111111111"));
        assertThat(client.getClientId(), is("11111111111"));
        assertThat(client.getPassword(), is("Lastname"));
        assertThat(client.getCustomerId(), is("1"));
        assertThat(client.getServiceId(), is(SERVICE_ID));
        assertThat(client.getUnitId(), is(UNIT_ID));
        assertThat(client.getAppointmentTypeId(), is(APPOINTMENT_TYPE_ID));
        assertThat(client.isEmail(), is(true));
        assertThat(client.isMobile(), is(true));
    }

    @Test(expected = RuntimeException.class)
    public void getCustomerByPersonalId_shouldThrowAnExceptionIfUsernameIsNot11Digits() throws Exception {
        ApiCallsSenderService senderService = (Template requestTemplate, Map<String, String> messageParams, String serviceAddress)
                -> new ResponseWrapper(200, getCustomerCustomPropertiesBothValuesFilled());
        TemplateLoader templateLoader = new FakeTemplateLoader();

        ServiceDetailsService serviceDetailsService = new ServiceDetailsService(senderService, templateLoader, "SomeUrl");
        AppointmentTypeService appointmentTypeService = new AppointmentTypeService(senderService, templateLoader, "SomeUrl");

        LoginClientService service = new LoginClientService(senderService, serviceDetailsService, appointmentTypeService, templateLoader, new ClientIdValidator(), "serviceUrl");

        service.loadUserByUsername("919191");
    }

    @Test(expected = UsernameNotFoundException.class)
    public void loadByUsername_shouldThrowUsernameNotFoundExceptionIfClientIdNotFound() throws Exception {
        ApiCallsSenderService senderService = (Template requestTemplate, Map<String, String> messageParams, String serviceAddress) -> {
            throw new ApiResponseNotSuccessfulException("Client ID not found", new ResponseWrapper(500, getCustomerByPersonalIdResponseNotFound()));
        };
        TemplateLoader templateLoader = new FakeTemplateLoader();

        ServiceDetailsService serviceDetailsService = new ServiceDetailsService(senderService, templateLoader, "SomeUrl");
        AppointmentTypeService appointmentTypeService = new AppointmentTypeService(senderService, templateLoader, "SomeUrl");

        LoginClientService service = new LoginClientService( senderService, serviceDetailsService, appointmentTypeService,
                templateLoader, new ClientIdValidator(), "serviceUrl");

        service.loadUserByUsername("00000000000");
    }

    @Test(expected = UserDetailsNotFilledException.class)
    public void getCustomerByPersonalId_shouldThrowAnExceptionIfServiceReferenceIsNotSetOnClient() throws Exception {
        ApiCallsSenderService senderService = (Template requestTemplate, Map<String, String> messageParams, String serviceAddress)
                -> new ResponseWrapper(200, getCustomerCustomPropertiesAppointmentTypeValueOnlyFilled());
        TemplateLoader templateLoader = new FakeTemplateLoader();

        ServiceDetailsService serviceDetailsService = new ServiceDetailsService(senderService, templateLoader, "SomeUrl");
        AppointmentTypeService appointmentTypeService = new AppointmentTypeService(senderService, templateLoader, "SomeUrl");

        LoginClientService service = new LoginClientService( senderService, serviceDetailsService, appointmentTypeService, templateLoader, new ClientIdValidator(), "serviceUrl");

        service.loadUserByUsername("11111111111");
    }

    @Test(expected = UserDetailsNotFilledException.class)
    public void getCustomerByPersonalId_shouldThrowAnExceptionIfAppointmentTypeReferenceIsNotSetOnClient() throws Exception {
        ApiCallsSenderService senderService = (Template requestTemplate, Map<String, String> messageParams, String serviceAddress)
                -> new ResponseWrapper(200, getCustomerCustomPropertiesServiceValueOnlyFilled());
        TemplateLoader templateLoader = new FakeTemplateLoader();

        ServiceDetailsService serviceDetailsService = new ServiceDetailsService(senderService, templateLoader, "SomeUrl");
        AppointmentTypeService appointmentTypeService = new AppointmentTypeService(senderService, templateLoader, "SomeUrl");

        LoginClientService service = new LoginClientService( senderService, serviceDetailsService, appointmentTypeService, templateLoader, new ClientIdValidator(), "serviceUrl");

        service.loadUserByUsername("11111111111");
    }

    private String getCustomerByPersonalIdResponse() {
        return
        "<s:Envelope xmlns:s=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:a=\"http://www.w3.org/2005/08/addressing\">\n" +
        "   <s:Header/>\n" +
        "   <s:Body>\n" +
        "      <GetByPersonalIdResponse xmlns=\"http://www.qnomy.com/Services\">\n" +
        "         <GetByPersonalIdResult xmlns:b=\"http://schemas.datacontract.org/2004/07/QFlow.Library\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
        "            <b:Active>true</b:Active>\n" +
        "            <b:PersonalId>11111111111</b:PersonalId>\n" +
        "            <b:Id>1</b:Id>\n" +
        "            <b:Name>Lastname Firstname</b:Name>\n" +
        "            <b:EMail>noemail@test.com</b:EMail>\n" +
        "            <b:TelNumber1>0400000000</b:TelNumber1>" +
        "            <b:FirstName>Firstname</b:FirstName>\n" +
        "            <b:LastName>Lastname</b:LastName>\n" +
        "         </GetByPersonalIdResult>\n" +
        "      </GetByPersonalIdResponse>\n" +
        "   </s:Body>\n" +
        "</s:Envelope>";
    }

    private String getCustomerByPersonalIdResponseNotFound() {
        return
        "<s:Envelope xmlns:s=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:a=\"http://www.w3.org/2005/08/addressing\">\n" +
        "    <s:Header>\n" +
        "        <a:Action s:mustUnderstand=\"1\">\n" +
        "            http://www.qnomy.com/Services/IsvcCustomer/GetByPersonalIdQFlowAPIApplicationExceptionFault\n" +
        "        </a:Action>\n" +
        "        <a:RelatesTo>urn:uuid:819c8868-e4e8-11e5-a420-6c40089d4690</a:RelatesTo>\n" +
        "    </s:Header>\n" +
        "    <s:Body>\n" +
        "        <s:Fault>\n" +
        "            <s:Code>\n" +
        "                <s:Value>s:Sender</s:Value>\n" +
        "            </s:Code>\n" +
        "            <s:Reason>\n" +
        "                <s:Text xml:lang=\"en-US\">Record not found</s:Text>\n" +
        "            </s:Reason>\n" +
        "            <s:Detail>\n" +
        "                <QFlowAPIApplicationException xmlns=\"http://schemas.datacontract.org/2004/07/QFlow.Web.API\">\n" +
        "                    <ErrorNumber>51010</ErrorNumber>\n" +
        "                </QFlowAPIApplicationException>\n" +
        "            </s:Detail>\n" +
        "        </s:Fault>\n" +
        "    </s:Body>\n" +
        "</s:Envelope>\n";
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
        "               <b:ValueList>34</b:ValueList>\n" +
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
        "               <b:ValueList>serviceExtRef, anotherServiceExtRef</b:ValueList>\n" +
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
        "               <b:ValueList>serviceExtRef, anotherServiceExtRef</b:ValueList>\n" +
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
        "               <b:ValueList>34</b:ValueList>\n" +
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
