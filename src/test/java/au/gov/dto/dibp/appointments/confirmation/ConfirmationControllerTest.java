package au.gov.dto.dibp.appointments.confirmation;

import au.gov.dto.dibp.appointments.appointmentdetails.AppointmentDetails;
import au.gov.dto.dibp.appointments.appointmentdetails.AppointmentDetailsService;
import au.gov.dto.dibp.appointments.client.Client;
import au.gov.dto.dibp.appointments.organisation.ServiceDetailsService;
import au.gov.dto.dibp.appointments.organisation.TimeZoneDictionaryForTests;
import au.gov.dto.dibp.appointments.organisation.UnitDetailsService;
import au.gov.dto.dibp.appointments.qflowintegration.ApiCallsSenderService;
import au.gov.dto.dibp.appointments.util.FakeTemplateLoader;
import au.gov.dto.dibp.appointments.util.ResponseWrapper;
import au.gov.dto.dibp.appointments.util.TemplateLoader;
import com.samskivert.mustache.Template;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ConfirmationControllerTest {

    private ConfirmationController controller;

    private static final String BOOKED_DATE = "2016-01-18T13:20:00";
    private static final String CLIENT_ID = "123";
    private static final String CUSTOMER_ID = "3333";
    private static final String PROCESS_ID = "121212";
    private static final String SERVICE_ID = "AAA";
    private static final String UNIT_ADDRESS = "Some Street 12";
    private static final boolean HAS_EMAIL = true;
    private static final boolean HAS_MOBILE = true;

    @Before
    public void setUp(){
        TemplateLoader templateLoader = new FakeTemplateLoader();

        ApiCallsSenderService senderService = (Template requestTemplate, Map<String, String> messageParams, String serviceAddress)
                -> getCallsResponses(requestTemplate);

        ServiceDetailsService serviceDetailsService = new ServiceDetailsService(senderService, templateLoader, "someUrl");
        UnitDetailsService unitDetailsService = new UnitDetailsService(serviceDetailsService, senderService, new TimeZoneDictionaryForTests(), templateLoader, "someUrl");

        controller = new ConfirmationController(new AppointmentDetailsService(senderService, unitDetailsService, templateLoader, "SomeUrl"), "trackingId");
    }

    @Test
    public void test_getConfirmationPage_should_passTheLocationToTheModel(){
        final ModelAndView result = controller.getConfirmationPage(getStandardClient());

        assertThat(result.getModel().get("location"), is(UNIT_ADDRESS));
    }

    @Test
    public void test_getConfirmationPage_should_passTheClientIdToTheModel(){
        final ModelAndView result = controller.getConfirmationPage(getStandardClient());

        assertThat(result.getModel().get("clientId"), is(CLIENT_ID));
    }

    @Test
    public void test_getConfirmationPage_should_passIfUserHasEmailToTheModel(){
        final ModelAndView result = controller.getConfirmationPage(getStandardClient());

        assertThat(result.getModel().get("hasEmail"), is(HAS_EMAIL));
    }

    @Test
    public void test_getConfirmationPage_should_passIfUserHasMobileToTheModel(){
        final ModelAndView result = controller.getConfirmationPage(getStandardClient());

        assertThat(result.getModel().get("hasMobile"), is(HAS_MOBILE));
    }

    @Test
    public void test_getConfirmationPage_should_passTheAppointmentDateToTheModel(){
        final ModelAndView result = controller.getConfirmationPage(getStandardClient());

        assertThat(result.getModel().get("appointment_date"), is("Monday 18 January 2016"));
    }

    @Test
    public void test_getConfirmationPage_should_passTheAppointmentTimeToTheModel(){
        final ModelAndView result = controller.getConfirmationPage(getStandardClient());

        assertThat(result.getModel().get("appointment_time"), is("1:20 PM"));
    }

    @Test
    public void test_getConfirmationPage_should_setConfirmationPageAsTheView(){
        final ModelAndView result = controller.getConfirmationPage(getStandardClient());

        assertThat(result.getViewName(), is("confirmation_page"));
    }

    @Test
    public void test_getConfirmationPage_should_redirectToBookingPageIfNoAppointmentsFound(){
        TemplateLoader templateLoader = new FakeTemplateLoader();

        ApiCallsSenderService senderService = (Template requestTemplate, Map<String, String> messageParams, String serviceAddress)
                -> getCallsResponsesNoAppointments(requestTemplate);

        ServiceDetailsService serviceDetailsService = new ServiceDetailsService(senderService, templateLoader, "someUrl");
        UnitDetailsService unitDetailsService = new UnitDetailsService(serviceDetailsService, senderService, new TimeZoneDictionaryForTests(), templateLoader, "someUrl");

        controller = new ConfirmationController(new AppointmentDetailsService(senderService, unitDetailsService, templateLoader, "SomeUrl"), "trackingId");



        controller = new ConfirmationController(new AppointmentDetailsService(null, null, templateLoader, null){
            @Override
            public AppointmentDetails getExpectedAppointmentForClientForNextYear(Client client) {
                return null;
            }
        }, "trackingId");

        final ModelAndView result = controller.getConfirmationPage(getStandardClient());

        assertThat(result.getViewName(), is("redirect:/calendar?error"));
    }

    private Client getStandardClient (){
        return new Client(CLIENT_ID, "Surname", CUSTOMER_ID, HAS_EMAIL,  HAS_MOBILE, "3", "5", "3", true);
    }

    private ResponseWrapper getCallsResponses(Template template){
        String response = null;

        if(template.toString().contains("GetExpectedAppointments.")){
            response = getExpectedAppointmentResponse();

        } else if (template.toString().contains("GetUnitLocalTime.")){
            response =getUnitLocalTimeResponse();
        }
        else if (template.toString().contains("GetService.")){
            response = getServiceDetailsResponse();

        } else if(template.toString().contains("GetUnit.")){
            response = getUnitDetailsResponse();
        }

        return new ResponseWrapper(200, response);
    }

    private ResponseWrapper getCallsResponsesNoAppointments(Template template){
        if(template.toString().contains("GetExpectedAppointments.")){

            String response = getEmptyExpectedAppointmentsResponse();
            return new ResponseWrapper(200, response);
        }
        return getCallsResponses(template);
    }

    private String getUnitLocalTimeResponse(){
        return
        "   <s:Body>\n" +
        "      <GetLocalTimeResponse xmlns=\"http://www.qnomy.com/Services\">\n" +
        "         <GetLocalTimeResult>2016-01-10T13:20:00</GetLocalTimeResult>\n" +
        "      </GetLocalTimeResponse>\n" +
        "   </s:Body>";
    }

    private String getServiceDetailsResponse(){
        return
        "   <s:Body>\n" +
        "      <GetResponse xmlns=\"http://www.qnomy.com/Services\">\n" +
        "         <GetResult xmlns:b=\"http://schemas.datacontract.org/2004/07/QFlow.Library\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
        "            <b:Active>true</b:Active>\n" +
        "            <b:Id>5</b:Id>\n" +
        "            <b:Name>Citizenship Interview 20min</b:Name>\n" +
        "            <b:LocalTime>2016-01-06T10:41:25.46</b:LocalTime>\n" +
        "            <b:MaxLockDuration>60</b:MaxLockDuration>\n" +
        "            <b:MaxSeats>0</b:MaxSeats>\n" +
        "            <b:ServiceLocationId>0</b:ServiceLocationId>\n" +
        "            <b:ServiceProfileId>2</b:ServiceProfileId>\n" +
        "            <b:ServiceTime>0</b:ServiceTime>\n" +
        "            <b:ServiceTypeId>1</b:ServiceTypeId>\n" +
        "            <b:ShowEntityId>false</b:ShowEntityId>\n" +
        "            <b:UnitId>3</b:UnitId>\n" +
        "            <b:WaitingAreaLocationId>0</b:WaitingAreaLocationId>\n" +
        "            <b:WorkingHoursId>0</b:WorkingHoursId>\n" +
        "         </GetResult>\n" +
        "      </GetResponse>\n" +
        "   </s:Body>";
    }

    private String getUnitDetailsResponse(){
        return
        "<s:Body>\n" +
        "      <GetResponse xmlns=\"http://www.qnomy.com/Services\">\n" +
        "         <GetResult xmlns:b=\"http://schemas.datacontract.org/2004/07/QFlow.Library\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
        "            <b:Active>true</b:Active>\n" +
        "            <b:ExtRef/>\n" +
        "            <b:Id>3</b:Id>\n" +
        "            <b:Name>Sydney</b:Name>\n" +
        "            <b:Address>" + UNIT_ADDRESS + "</b:Address>\n" +
        "            <b:Level>2</b:Level>\n" +
        "         </GetResult>\n" +
        "      </GetResponse>\n" +
        "   </s:Body>";
    }

    private String getExpectedAppointmentResponse(){
        return
        " <s:Body>\n" +
        "      <GetExpectedAppointmentsResponse xmlns=\"http://www.qnomy.com/Services\">\n" +
        "         <GetExpectedAppointmentsResult xmlns:b=\"http://schemas.datacontract.org/2004/07/QFlow.Library\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
        "            <b:CustomerGetExpectedAppointmentsResults>\n" +
        "               <b:AppointmentDate>" + BOOKED_DATE + "</b:AppointmentDate>\n" +
        "               <b:AppointmentDuration>20</b:AppointmentDuration>\n" +
        "               <b:AppointmentId>117</b:AppointmentId>\n" +
        "               <b:AppointmentTypeId>3</b:AppointmentTypeId>\n" +
        "               <b:AppointmentTypeName>Standard Citizenship Appointment</b:AppointmentTypeName>\n" +
        "               <b:CustomerId>" + CUSTOMER_ID + "</b:CustomerId>\n" +
        "               <b:ProcessId>" + PROCESS_ID + "</b:ProcessId>\n" +
        "               <b:ServiceId>" + SERVICE_ID + "</b:ServiceId>\n" +
        "               <b:ServiceName>Citizenship Interview 20min</b:ServiceName>\n" +
        "               <b:UnitName>Sydney CBD</b:UnitName>\n" +
        "            </b:CustomerGetExpectedAppointmentsResults>\n" +
        "         </GetExpectedAppointmentsResult>\n" +
        "      </GetExpectedAppointmentsResponse>\n" +
        "   </s:Body>";
    }

    private String getEmptyExpectedAppointmentsResponse(){
       return
       "   <s:Body>\n" +
        "      <GetExpectedAppointmentsResponse xmlns=\"http://www.qnomy.com/Services\">\n" +
        "         <GetExpectedAppointmentsResult xmlns:b=\"http://schemas.datacontract.org/2004/07/QFlow.Library\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\"/>\n" +
        "      </GetExpectedAppointmentsResponse>\n" +
        "   </s:Body>";
    }
}
