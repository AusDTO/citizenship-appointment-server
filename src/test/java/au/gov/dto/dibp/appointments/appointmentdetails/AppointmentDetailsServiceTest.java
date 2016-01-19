package au.gov.dto.dibp.appointments.appointmentdetails;

import au.gov.dto.dibp.appointments.client.Client;
import au.gov.dto.dibp.appointments.organisation.TimeZoneDictionaryForTests;
import au.gov.dto.dibp.appointments.organisation.UnitDetails;
import au.gov.dto.dibp.appointments.organisation.UnitDetailsService;
import au.gov.dto.dibp.appointments.qflowintegration.ApiCallsSenderService;
import au.gov.dto.dibp.appointments.util.FakeTemplateLoader;
import au.gov.dto.dibp.appointments.util.ResponseWrapper;
import com.samskivert.mustache.Template;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class AppointmentDetailsServiceTest {

    private AppointmentDetailsService service;

    @Test
    public void test_getExpectedAppointmentForClient_should_returnNullIfNoAppointmentsFound(){

        service = new AppointmentDetailsService(
                getApiCallsSenderServiceReturningNoAppointments(),
                getUnitDetailsServiceReturningCorrectAddress(),
                new FakeTemplateLoader(),
                "Service Address");

        final AppointmentDetails appointmentDetails = service.getExpectedAppointmentForClient(getStandardClient(), LocalDateTime.parse("2016-01-01T13:00:00"), LocalDateTime.parse("2016-01-30T13:00:00"));
        assertThat(appointmentDetails, is(nullValue()));
    }

    @Test(expected=RuntimeException.class)
    public void test_getExpectedAppointmentForClient_should_throwRuntimeExceptionIfTheAppointmentCustomerIsNotTheLoggedInClient(){

        service = new AppointmentDetailsService(
                getApiCallsSenderServiceReturningAppointmentWithWrongCustomerId(),
                getUnitDetailsServiceReturningCorrectAddress(),
                new FakeTemplateLoader(),
                "Service Address");

        service.getExpectedAppointmentForClient(getStandardClient(), LocalDateTime.parse("2016-01-01T13:00:00"), LocalDateTime.parse("2016-01-30T13:00:00"));
        assertTrue(false);
    }

    @Test
    public void test_getExpectedAppointmentForClient_should_returnAppointmentDetailsIfSuccessfullyRetrieved(){

        service = new AppointmentDetailsService(
                getApiCallsSenderServiceReturningCorrectAppointment(),
                getUnitDetailsServiceReturningCorrectAddress(),
                new FakeTemplateLoader(),
                "Service Address");

        final AppointmentDetails appointmentDetails = service.getExpectedAppointmentForClient(getStandardClient(), LocalDateTime.parse("2016-01-01T13:00:00"), LocalDateTime.parse("2016-01-30T13:00:00"));

        assertThat(appointmentDetails.getAppointmentDate(), is(LocalDateTime.parse("2016-02-06T13:00:00")));
        assertThat(appointmentDetails.getAppointmentDuration(), is(20));
        assertThat(appointmentDetails.getCustomerId(), is("40404"));
        assertThat(appointmentDetails.getProcessId(), is("125"));
        assertThat(appointmentDetails.getServiceId(), is("111"));
        assertThat(appointmentDetails.getUnitAddress(), is("Some address 23"));
        assertThat(appointmentDetails.getUnitName(), is("Sydney CBD"));
    }

    private ApiCallsSenderService getApiCallsSenderServiceReturningNoAppointments(){
        return (Template requestTemplate, Map<String, String> messageParams, String serviceAddress) -> {
            String responseWithNoAppointments =
            "   <s:Body>\n" +
            "      <GetExpectedAppointmentsResponse xmlns=\"http://www.qnomy.com/Services\">\n" +
            "         <GetExpectedAppointmentsResult xmlns:b=\"http://schemas.datacontract.org/2004/07/QFlow.Library\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\"/>\n" +
            "      </GetExpectedAppointmentsResponse>\n" +
            "   </s:Body>";

            return new ResponseWrapper(200, responseWithNoAppointments);
        };
    }

    private ApiCallsSenderService getApiCallsSenderServiceReturningAppointmentWithWrongCustomerId(){
        return (Template requestTemplate, Map<String, String> messageParams, String serviceAddress) -> {
            String responseWithWrongCustomerId =
            " <s:Body>\n" +
            "      <GetExpectedAppointmentsResponse xmlns=\"http://www.qnomy.com/Services\">\n" +
            "         <GetExpectedAppointmentsResult xmlns:b=\"http://schemas.datacontract.org/2004/07/QFlow.Library\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
            "            <b:CustomerGetExpectedAppointmentsResults>\n" +
            "               <b:AppointmentDate>2016-01-06T09:00:00</b:AppointmentDate>\n" +
            "               <b:AppointmentDuration>20</b:AppointmentDuration>\n" +
            "               <b:AppointmentId>117</b:AppointmentId>\n" +
            "               <b:AppointmentTypeId>3</b:AppointmentTypeId>\n" +
            "               <b:AppointmentTypeName>Standard Citizenship Appointment</b:AppointmentTypeName>\n" +
            "               <b:CustomerId>2323</b:CustomerId>\n" +
            "               <b:ProcessId>125</b:ProcessId>\n" +
            "               <b:ServiceId>111</b:ServiceId>\n" +
            "               <b:ServiceName>Citizenship Interview 20min</b:ServiceName>\n" +
            "               <b:UnitName>Sydney</b:UnitName>\n" +
            "            </b:CustomerGetExpectedAppointmentsResults>\n" +
            "         </GetExpectedAppointmentsResult>\n" +
            "      </GetExpectedAppointmentsResponse>\n" +
            "   </s:Body>";

            return new ResponseWrapper(200, responseWithWrongCustomerId);
        };
    }

    private ApiCallsSenderService getApiCallsSenderServiceReturningCorrectAppointment(){
        return (Template requestTemplate, Map<String, String> messageParams, String serviceAddress) -> {
            String response =
                " <s:Body>\n" +
                "      <GetExpectedAppointmentsResponse xmlns=\"http://www.qnomy.com/Services\">\n" +
                "         <GetExpectedAppointmentsResult xmlns:b=\"http://schemas.datacontract.org/2004/07/QFlow.Library\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
                "            <b:CustomerGetExpectedAppointmentsResults>\n" +
                "               <b:AppointmentDate>2016-02-06T13:00:00</b:AppointmentDate>\n" +
                "               <b:AppointmentDuration>20</b:AppointmentDuration>\n" +
                "               <b:AppointmentId>117</b:AppointmentId>\n" +
                "               <b:AppointmentTypeId>3</b:AppointmentTypeId>\n" +
                "               <b:AppointmentTypeName>Standard Citizenship Appointment</b:AppointmentTypeName>\n" +
                "               <b:CustomerId>40404</b:CustomerId>\n" +
                "               <b:ProcessId>125</b:ProcessId>\n" +
                "               <b:ServiceId>111</b:ServiceId>\n" +
                "               <b:ServiceName>Citizenship Interview 20min</b:ServiceName>\n" +
                "               <b:UnitName>Sydney CBD</b:UnitName>\n" +
                "            </b:CustomerGetExpectedAppointmentsResults>\n" +
                "         </GetExpectedAppointmentsResult>\n" +
                "      </GetExpectedAppointmentsResponse>\n" +
                "   </s:Body>";

            return new ResponseWrapper(200, response);
        };
    }

    private UnitDetailsService getUnitDetailsServiceReturningCorrectAddress(){
        return new UnitDetailsService(null, null, new TimeZoneDictionaryForTests(), new FakeTemplateLoader(), null){
            @Override
            public String getUnitAddressByServiceId(String serviceId){
                return "Some address 23";
            }

            @Override
            public UnitDetails getUnitDetailsByServiceId(String serviceId){
                return new UnitDetails("3", "Some address 23", "Australia/Sydney");
            }
        };
    }

    private Client getStandardClient (){
        return new Client("123", "Surname", "40404", false, "3", "5", "3", true);
    }
}
