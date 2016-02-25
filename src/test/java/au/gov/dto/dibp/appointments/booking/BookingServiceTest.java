package au.gov.dto.dibp.appointments.booking;

import au.gov.dto.dibp.appointments.appointmentdetails.AppointmentDetails;
import au.gov.dto.dibp.appointments.appointmentdetails.AppointmentDetailsService;
import au.gov.dto.dibp.appointments.booking.exceptions.NoCalendarExistsException;
import au.gov.dto.dibp.appointments.booking.exceptions.SlotAlreadyTakenException;
import au.gov.dto.dibp.appointments.client.Client;
import au.gov.dto.dibp.appointments.qflowintegration.ApiCallsSenderService;
import au.gov.dto.dibp.appointments.qflowintegration.ApiResponseNotSuccessfulException;
import au.gov.dto.dibp.appointments.util.FakeTemplateLoader;
import au.gov.dto.dibp.appointments.util.ResponseWrapper;
import com.samskivert.mustache.Template;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class BookingServiceTest {

    private BookingService service;

    @Test
    public void test_bookAnAppointment_when_userHasNoAppointmentsYet_should_returnAppointmentDateIfBookingSuccessful(){
        service = new BookingService( getApiCallsSenderService(),
                getAppointmentDetailsServiceRespondingWithNoAppointment(),
                new FakeTemplateLoader(),
                "serviceUrl", "processUrl");

        LocalDateTime apptTime = LocalDateTime.of(2015, 12, 30, 13, 20, 0);
        String bookedDate = service.bookAnAppointment(getStandardClient(), apptTime);
        assertThat(bookedDate, is("2015-12-30T13:20:00"));
    }

    @Test
    public void test_bookAnAppointment_when_userHasAnAppointmentSet_should_returnNewAppointmentDateIfBookingSuccessful(){
        service = new BookingService( getApiCallsSenderService(),
                getAppointmentDetailsServiceRespondingWithAppointmentDetails(),
                new FakeTemplateLoader(),
                "serviceUrl", "processUrl");

        LocalDateTime apptTime = LocalDateTime.of(2016, 1, 22, 13, 20, 0);
        String bookedDate = service.bookAnAppointment(getStandardClient(), apptTime);
        assertThat(bookedDate, is("2016-01-22T13:20:00"));
    }

    @Test(expected = SlotAlreadyTakenException.class)
    public void test_bookAnAppointment_when_slotIsTakenInTheMeantime_should_throwSlotAlreadyTakenException(){
        service = new BookingService( getApiCallsSenderServiceReturningFaults(),
                getAppointmentDetailsServiceRespondingWithAppointmentDetails(),
                new FakeTemplateLoader(),
                "serviceUrl", "processUrl");

        LocalDateTime apptTime = LocalDateTime.of(2016, 1, 22, 13, 20, 0);
        service.bookAnAppointment(getStandardClient(), apptTime);
    }

    @Test(expected = NoCalendarExistsException.class)
    public void test_bookAnAppointment_when_calendarIsClosed_should_throwNoCalendarExistsException() {
        service = new BookingService(getApiCallsSenderServiceReturningFaults(),
                getAppointmentDetailsServiceRespondingWithNoAppointment(),
                new FakeTemplateLoader(),
                "serviceUrl", "processUrl");

        LocalDateTime apptTime = LocalDateTime.of(2015, 12, 30, 13, 20, 0);
        service.bookAnAppointment(getStandardClient(), apptTime);
    }


    private AppointmentDetailsService getAppointmentDetailsServiceRespondingWithNoAppointment(){
        return new AppointmentDetailsService(null, null, new FakeTemplateLoader(), null){

            @Override
            public AppointmentDetails getExpectedAppointmentForClientForNextYear(Client client) {
                return null;
            }
        };
    }

    private AppointmentDetailsService getAppointmentDetailsServiceRespondingWithAppointmentDetails(){
        return new AppointmentDetailsService(null, null, new FakeTemplateLoader(), null){

            @Override
            public AppointmentDetails getExpectedAppointmentForClientForNextYear(Client client) {
                return getBasicAppointmentDetails();
            }
        };
    }

    private ApiCallsSenderService getApiCallsSenderService(){
        return (Template requestTemplate, Map<String, String> messageParams, String serviceAddress) -> {
            if(requestTemplate.toString().startsWith("SetAppointment")){
                return getSuccessfulInitialBookingResponse();
            } else if(requestTemplate.toString().startsWith("RescheduleAppointment")){
                return getSuccessfulRescheduleBookingResponse();
            }
            return null;
        };
    }

    private static final String BOOKED_DATE = "2016-01-18T13:20:00";
    private static final String CUSTOMER_ID = "3333";
    private static final String PROCESS_ID = "121";
    private static final String SERVICE_ID = "AAA";
    private static final String UNTI_ADDRESS = "Some Street 12";

    private AppointmentDetails getBasicAppointmentDetails(){
        return new AppointmentDetails(LocalDateTime.parse(BOOKED_DATE), 20, PROCESS_ID, SERVICE_ID, CUSTOMER_ID, "Sydney", UNTI_ADDRESS, "Australia/Sydney");
    }

    private Client getStandardClient (){
        return new Client("123", "Surname", "40404", false, false, "3", "5", "3", true);
    }

    private ApiCallsSenderService getApiCallsSenderServiceReturningFaults(){
        return (Template requestTemplate, Map<String, String> messageParams, String serviceAddress) -> {
            if(requestTemplate.toString().startsWith("SetAppointment")){
                throw new ApiResponseNotSuccessfulException("", getFaultCalendarClosedResponse());
            } else if(requestTemplate.toString().startsWith("RescheduleAppointment")){
                throw new ApiResponseNotSuccessfulException("", getFaultSlotTakenResponse());
            }
            return null;
        };
    }

    private ResponseWrapper getSuccessfulInitialBookingResponse(){
        String response =
        "<s:Body>\n" +
        "  <SetAppointmentResponse xmlns=\"http://www.qnomy.com/Services\">\n" +
        "         <SetAppointmentResult xmlns:b=\"http://schemas.datacontract.org/2004/07/QFlow.Library\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
        "            <b:AppointmentId>113</b:AppointmentId>\n" +
        "            <b:CalendarId>1050</b:CalendarId>\n" +
        "            <b:CaseId>105</b:CaseId>\n" +
        "            <b:ProcessId>121</b:ProcessId>\n" +
        "            <b:SetAppointmentData>\n" +
        "               <b:AppointmentTypeId>2</b:AppointmentTypeId>\n" +
        "               <b:CustomerId>6</b:CustomerId>\n" +
        "               <b:DateAndTime>2015-12-30T13:20:00</b:DateAndTime>\n" +
        "               <b:Notes>This is generated by the API call</b:Notes>\n" +
        "               <b:UserId>3</b:UserId>\n" +
        "            </b:SetAppointmentData>\n" +
        "         </SetAppointmentResult>\n" +
        "      </SetAppointmentResponse>" +
        "</s:Body>";
        return new ResponseWrapper(200, response);
    }

    private ResponseWrapper getSuccessfulRescheduleBookingResponse(){
        String response =
        "<s:Body>\n" +
        "      <RescheduleAppointmentResponse xmlns=\"http://www.qnomy.com/Services\">\n" +
        "         <RescheduleAppointmentResult xmlns:b=\"http://schemas.datacontract.org/2004/07/QFlow.Library\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
        "            <b:AppointmentId>199</b:AppointmentId>\n" +
        "            <b:CalendarId>1242</b:CalendarId>\n" +
        "            <b:CaseId>105</b:CaseId>\n" +
        "            <b:CustomerId>6</b:CustomerId>\n" +
        "            <b:ProcessId>121</b:ProcessId>\n" +
        "            <b:QCode>C</b:QCode>\n" +
        "            <b:QNumber>0</b:QNumber>\n" +
        "            <b:RescheduleAppointmentData>\n" +
        "               <b:AppointmentTypeId>2</b:AppointmentTypeId>\n" +
        "               <b:DateAndTime>2016-01-22T13:20:00</b:DateAndTime>\n" +
        "               <b:ExtRef i:nil=\"true\"/>\n" +
        "               <b:Notes>Citizenship Appt</b:Notes>\n" +
        "               <b:OriginalProcessId>213</b:OriginalProcessId>\n" +
        "               <b:PreventAutoQueue>false</b:PreventAutoQueue>\n" +
        "               <b:Resources i:nil=\"true\"/>\n" +
        "               <b:ServiceId>5</b:ServiceId>\n" +
        "               <b:Subject>Citizenship Reschedule SOAP</b:Subject>\n" +
        "               <b:UserId>3</b:UserId>\n" +
        "            </b:RescheduleAppointmentData>\n" +
        "            <b:SetAppointmentData i:nil=\"true\"/>\n" +
        "         </RescheduleAppointmentResult>\n" +
        "      </RescheduleAppointmentResponse>\n" +
        "   </s:Body>";
        return new ResponseWrapper(200, response);
    }

    private ResponseWrapper getFaultSlotTakenResponse(){
        String response =
        "<s:Envelope xmlns:s=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:a=\"http://www.w3.org/2005/08/addressing\">\n" +
            "   <s:Header>\n" +
            "      <a:Action s:mustUnderstand=\"1\">http://www.qnomy.com/Services/IsvcService/SetAppointmentQFlowAPIApplicationExceptionFault</a:Action>\n" +
            "      <a:RelatesTo>uuid:ede4ca2c-6047-4244-96e4-774bd955078b</a:RelatesTo>\n" +
            "   </s:Header>\n" +
            "   <s:Body>\n" +
            "      <s:Fault>\n" +
            "         <s:Code>\n" +
            "            <s:Value>s:Sender</s:Value>\n" +
            "         </s:Code>\n" +
            "         <s:Reason>\n" +
            "            <s:Text xml:lang=\"en-US\">Not enough vacant time in the required segment</s:Text>\n" +
            "         </s:Reason>\n" +
            "         <s:Detail>\n" +
            "            <QFlowAPIApplicationException xmlns=\"http://schemas.datacontract.org/2004/07/QFlow.Web.API\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
            "               <ErrorNumber>58725</ErrorNumber>\n" +
            "            </QFlowAPIApplicationException>\n" +
            "         </s:Detail>\n" +
            "      </s:Fault>\n" +
            "   </s:Body>\n" +
            "</s:Envelope>";
        return new ResponseWrapper(500, response);
    }

    private ResponseWrapper getFaultCalendarClosedResponse(){
        String response =
        "<s:Envelope xmlns:s=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:a=\"http://www.w3.org/2005/08/addressing\">\n" +
        "<s:Header>\n" +
        "<a:Action s:mustUnderstand=\"1\">\n" +
        "http://www.qnomy.com/Services/IsvcProcess/RescheduleAppointmentQFlowAPIApplicationExceptionFault\n" +
        "        </a:Action>\n" +
        "        <a:RelatesTo>\n" +
        "        urn:uuid:eaa92df8-2eb2-46a9-b24d-165831e2da14\n" +
        "        </a:RelatesTo>\n" +
        "    </s:Header>\n" +
        "    <s:Body>\n" +
        "        <s:Fault>\n" +
        "            <s:Code>\n" +
        "                <s:Value>s:Sender</s:Value>\n" +
        "            </s:Code>\n" +
        "            <s:Reason>\n" +
        "                <s:Text xml:lang=\"en-US\">No active calendar exists in the specified service and date</s:Text>\n" +
        "            </s:Reason>\n" +
        "            <s:Detail>\n" +
        "                <QFlowAPIApplicationException xmlns=\"http://schemas.datacontract.org/2004/07/QFlow.Web.API\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
        "                    <ErrorNumber>58710</ErrorNumber>\n" +
        "                </QFlowAPIApplicationException>\n" +
        "            </s:Detail>\n" +
        "        </s:Fault>\n" +
        "    </s:Body>\n" +
        "</s:Envelope>";
        return new ResponseWrapper(500, response);
    }
}
