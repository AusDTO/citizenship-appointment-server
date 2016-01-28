package au.gov.dto.dibp.appointments.appointmentdetails;

import au.gov.dto.dibp.appointments.client.Client;
import au.gov.dto.dibp.appointments.organisation.TimeZoneDictionaryForTests;
import au.gov.dto.dibp.appointments.organisation.ServiceDetailsService;
import au.gov.dto.dibp.appointments.organisation.UnitDetailsService;
import au.gov.dto.dibp.appointments.qflowintegration.ApiCallsSenderService;
import au.gov.dto.dibp.appointments.util.FakeTemplateLoader;
import au.gov.dto.dibp.appointments.util.ResponseWrapper;
import au.gov.dto.dibp.appointments.util.TemplateLoader;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class AddToCalendarControllerTest {
    private final static String APPOINTMENT_DATE = "2016-02-06T13:00:00";
    private final static String UNIT_ID = "12";
    private final static String SERVICE_ID = "11";
    private final static String UNIT_LOCALTIME = "2016-01-06T13:00:00";
    private final static String UNIT_ADDRESS = "51 Pitt Street, North Sydney 2060";

    private AddToCalendarController controller;

    @Before
    public void setUp(){
        final ApiCallsSenderService senderService = getApiCallsSenderService();
        final TemplateLoader fakeTemplateLoader = new FakeTemplateLoader();
        final ServiceDetailsService serviceDetailsService = new ServiceDetailsService(senderService, fakeTemplateLoader, "some Url");
        final UnitDetailsService unitDetailsService = new UnitDetailsService(serviceDetailsService, senderService, new TimeZoneDictionaryForTests(), fakeTemplateLoader, "someUrl");

        controller = new AddToCalendarController(
                new AppointmentDetailsService(senderService, unitDetailsService, fakeTemplateLoader, "someServiceUrl"),
                new TemplateLoader(Mustache.compiler(), new DefaultResourceLoader()));
    }

    @Test
    public void test_addToGoogleCalendar_should_returnRedirectUrlWithAppointmentDetailsFilled(){
        ModelAndView modelAndView = controller.addToGoogleCalendar(getStandardClient());
        assertThat("redirect:http://www.google.com/calendar/event?" +
                "action=TEMPLATE" +
                "&text=Citizenship appointment" +
                "&dates=20160206T020000Z/20160206T040000Z" +
                "&location=51 Pitt Street, North Sydney 2060" +
                "&details=For details please refer to your citizenship appointment email/letter.%0A%0A%0Ahttps://www.border.gov.au&trp=false", is(modelAndView.getViewName()));
    }

    @Test
    public void test_addToYahooCalendar_should_returnRedirectUrlWithAppointmentDetailsFilled(){
        ModelAndView modelAndView = controller.addToYahooCalendar(getStandardClient());
        assertThat("redirect:http://calendar.yahoo.com/?" +
                "v=60" +
                "&DUR=0200" +
                "&TITLE=Citizenship appointment" +
                "&ST=20160206T020000Z" +
                "&in_loc=51 Pitt Street, North Sydney 2060" +
                "&DESC=For details please refer to your citizenship appointment email/letter.%0A%0A%0Ahttps://www.border.gov.au", is(modelAndView.getViewName()));
    }

    @Test
    public void test_addToOutlookOnlineCalendar_should_returnRedirectUrlWithAppointmentDetailsFilled(){
        ModelAndView modelAndView = controller.addToOutlookOnlineCalendar(getStandardClient());
        assertThat("redirect:http://calendar.live.com/calendar/calendar.aspx?" +
                "rru=addevent" +
                "&summary=Citizenship appointment" +
                "&dtstart=20160206T020000Z" +
                "&dtend=20160206T040000Z" +
                "&location=51 Pitt Street, North Sydney 2060" +
                "&description=For details please refer to your citizenship appointment email/letter.%0A%0A%0Ahttps://www.border.gov.au", is(modelAndView.getViewName()));
    }

    @Test
    public void test_getCalendarIcsFile_should_returnResponseEntityWithHttpStatusCodeOK(){
        ResponseEntity<String> responseEntity = controller.getCalendarIcsFile(getStandardClient());
        assertThat(HttpStatus.OK, is(responseEntity.getStatusCode()));
    }

    @Test
    public void test_getCalendarIcsFile_should_returnResponseEntityWithCorrectResponseHeaders(){
        ResponseEntity<String> responseEntity = controller.getCalendarIcsFile(getStandardClient());
        final HttpHeaders headers = responseEntity.getHeaders();
        assertThat("text/calendar", is(headers.get("Content-Type").get(0)));
        assertThat("attachment; filename=appointment.ics", is(headers.get("content-disposition").get(0)));
    }

    @Test
    public void test_getCalendarIcsFile_should_returnResponseEntityWithFilledResponseBody(){
        ResponseEntity<String> responseEntity = controller.getCalendarIcsFile(getStandardClient());
        String responseBody = responseEntity.getBody();
        assertTrue("Should contain begin statement", responseBody.contains("BEGIN:VCALENDAR"));
        assertTrue("Should contain version", responseBody.contains("VERSION:2.0"));
        assertTrue("Should contain prodid", responseBody.contains("PRODID:-//border.gov.au//Citizenship Appointment Service//EN"));
        assertTrue("Should contain begin event statement", responseBody.contains("BEGIN:VEVENT"));
        assertTrue("Should contain start time", responseBody.contains("DTSTART;TZID=Australia/Sydney:20160206T130000"));
        assertTrue("Should contain end time", responseBody.contains("DTEND;TZID=Australia/Sydney:20160206T150000"));
        assertTrue("Should contain summary", responseBody.contains("SUMMARY:Citizenship appointment"));
        assertTrue("Should contain location", responseBody.contains("LOCATION:51 Pitt Street, North Sydney 2060"));
        assertTrue("Should contain description", responseBody.contains("DESCRIPTION:For details please refer to your citizenship appointment email/letter.\\n\\n\\nhttps://www.border.gov.au"));
        assertTrue("Should contain end event statement", responseBody.contains("END:VEVENT"));
        assertTrue("Should contain end statement", responseBody.contains("END:VCALENDAR"));
    }

    private Client getStandardClient (){
        return new Client("123", "Surname", "40404", false, false, UNIT_ID, SERVICE_ID, "3", true);
    }

    private ApiCallsSenderService getApiCallsSenderService(){
        return (Template requestTemplate, Map<String, String> messageParams, String serviceAddress) -> {
            String response = null;
            if(requestTemplate.toString().contains("GetExpectedAppointments.")){
                response = getExpectedAppointmentsResponse();
            } else if (requestTemplate.toString().contains("GetUnit.")){
                response = getUnitDetailsResponse(messageParams);
            } else if (requestTemplate.toString().contains("GetUnitLocalTime.")){
                response = getUnitLocalTimeResponse(messageParams);
            } else if (requestTemplate.toString().contains("GetService.")){
                response = getServiceDetailsResponse(messageParams);
            }

            return new ResponseWrapper(200, response);
        };
    }

    private String getExpectedAppointmentsResponse(){
        return
            " <s:Body>\n" +
            "      <GetExpectedAppointmentsResponse xmlns=\"http://www.qnomy.com/Services\">\n" +
            "         <GetExpectedAppointmentsResult xmlns:b=\"http://schemas.datacontract.org/2004/07/QFlow.Library\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
            "            <b:CustomerGetExpectedAppointmentsResults>\n" +
            "               <b:AppointmentDate>" + APPOINTMENT_DATE + "</b:AppointmentDate>\n" +
            "               <b:AppointmentDuration>20</b:AppointmentDuration>\n" +
            "               <b:AppointmentId>117</b:AppointmentId>\n" +
            "               <b:AppointmentTypeId>3</b:AppointmentTypeId>\n" +
            "               <b:AppointmentTypeName>Standard Citizenship Appointment</b:AppointmentTypeName>\n" +
            "               <b:CustomerId>40404</b:CustomerId>\n" +
            "               <b:ProcessId>125</b:ProcessId>\n" +
            "               <b:ServiceId>" + SERVICE_ID + "</b:ServiceId>\n" +
            "               <b:ServiceName>Citizenship Interview 20min</b:ServiceName>\n" +
            "               <b:UnitName>Sydney CBD</b:UnitName>\n" +
            "            </b:CustomerGetExpectedAppointmentsResults>\n" +
            "         </GetExpectedAppointmentsResult>\n" +
            "      </GetExpectedAppointmentsResponse>\n" +
            "   </s:Body>";
    }

    private String getServiceDetailsResponse(Map<String, String> messageParams){
        assertThat(messageParams.get("serviceId"), is(SERVICE_ID));

        return
        "   <s:Body>\n" +
        "      <GetResponse xmlns=\"http://www.qnomy.com/Services\">\n" +
        "         <GetResult xmlns:b=\"http://schemas.datacontract.org/2004/07/QFlow.Library\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
        "            <b:Active>true</b:Active>\n" +
        "            <b:Description/>\n" +
        "            <b:ExtRef/>\n" +
        "            <b:Id>" + SERVICE_ID + "</b:Id>\n" +
        "            <b:Name>Citizenship Interview 20min</b:Name>\n" +
        "            <b:ServicePrefix/>\n" +
        "            <b:LocalTime>2016-01-06T10:41:25.46</b:LocalTime>\n" +
        "            <b:MaxLockDuration>60</b:MaxLockDuration>\n" +
        "            <b:MaxSeats>0</b:MaxSeats>\n" +
        "            <b:ServiceLocationId>0</b:ServiceLocationId>\n" +
        "            <b:ServiceProfileId>2</b:ServiceProfileId>\n" +
        "            <b:ServiceTime>0</b:ServiceTime>\n" +
        "            <b:ServiceTypeId>1</b:ServiceTypeId>\n" +
        "            <b:ShowEntityId>false</b:ShowEntityId>\n" +
        "            <b:UnitId>" + UNIT_ID + "</b:UnitId>\n" +
        "            <b:WaitingAreaLocationId>0</b:WaitingAreaLocationId>\n" +
        "            <b:WorkingHoursId>0</b:WorkingHoursId>\n" +
        "         </GetResult>\n" +
        "      </GetResponse>\n" +
        "   </s:Body>";
    }

    private String getUnitDetailsResponse(Map<String, String> messageParams){
        assertThat(messageParams.get("unitId"), is(UNIT_ID));

        return
        "<s:Body>\n" +
        "      <GetResponse xmlns=\"http://www.qnomy.com/Services\">\n" +
        "         <GetResult xmlns:b=\"http://schemas.datacontract.org/2004/07/QFlow.Library\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
        "            <b:Active>true</b:Active>\n" +
        "            <b:Description/>\n" +
        "            <b:ExtRef/>\n" +
        "            <b:Id>" + UNIT_ID + "</b:Id>\n" +
        "            <b:Name>Sydney</b:Name>\n" +
        "            <b:ActiveDirectoryOU/>\n" +
        "            <b:Address>" + UNIT_ADDRESS + "</b:Address>\n" +
        "            <b:CustomerPatience>0</b:CustomerPatience>\n" +
        "            <b:Level>2</b:Level>\n" +
        "            <b:MaxSeats>0</b:MaxSeats>\n" +
        "            <b:ParentUnitId>2</b:ParentUnitId>\n" +
        "            <b:Path>\\DIBP\\NSW\\Sydney</b:Path>\n" +
        "            <b:ServiceTime>0</b:ServiceTime>\n" +
        "            <b:SubTreeActive>True</b:SubTreeActive>\n" +
        "            <b:TelNumber/>\n" +
        "            <b:TimeZoneId>51</b:TimeZoneId>\n" +
        "            <b:TypeId>3</b:TypeId>\n" +
        "            <b:WaitingTimeTarget>0</b:WaitingTimeTarget>\n" +
        "            <b:WorkingHoursId>1</b:WorkingHoursId>\n" +
        "         </GetResult>\n" +
        "      </GetResponse>\n" +
        "   </s:Body>";
    }

    private String getUnitLocalTimeResponse(Map<String, String> messageParams){
        assertThat(messageParams.get("unitId"), is(UNIT_ID));

        return
        "   <s:Body>\n" +
        "      <GetLocalTimeResponse xmlns=\"http://www.qnomy.com/Services\">\n" +
        "         <GetLocalTimeResult>" + UNIT_LOCALTIME + "</GetLocalTimeResult>\n" +
        "      </GetLocalTimeResponse>\n" +
        "   </s:Body>";
    }

}
