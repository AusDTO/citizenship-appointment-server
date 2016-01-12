package au.gov.dto.dibp.appointments.login;

import au.gov.dto.dibp.appointments.util.ResponseWrapper;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class AppointmentTypeServiceTest {

    private AppointmentTypeService service;

    @Test
    public void test_getAppointmentTypeIdByExternalReference_should_returnAppointmentId(){
        service = new AppointmentTypeService((String requestTemplatePath, Map<String, String> messageParams, String serviceAddress) -> getAppointmentTypeByExtRefResponse(),
                "Some Service URL");

        assertThat("3", is(service.getAppointmentTypeIdByExternalReference("232323")));
    }

    private ResponseWrapper getAppointmentTypeByExtRefResponse(){
        String response =
        " <s:Body>\n" +
        "      <GetByExtRefResponse xmlns=\"http://www.qnomy.com/Services\">\n" +
        "         <GetByExtRefResult xmlns:b=\"http://schemas.datacontract.org/2004/07/QFlow.Library\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
        "            <b:AppointmentType>\n" +
        "               <b:Active>true</b:Active>\n" +
        "               <b:ExtRef>STD_CIT_APPT</b:ExtRef>\n" +
        "               <b:Id>3</b:Id>\n" +
        "               <b:Name>Standard Citizenship Appointment</b:Name>\n" +
        "               <b:Duration>20</b:Duration>\n" +
        "               <b:IsRestricted>false</b:IsRestricted>\n" +
        "               <b:ServiceTypeId>1</b:ServiceTypeId>\n" +
        "               <b:ServiceTypeName/>\n" +
        "            </b:AppointmentType>\n" +
        "         </GetByExtRefResult>\n" +
        "      </GetByExtRefResponse>\n" +
        "   </s:Body>";
        return new ResponseWrapper(200, response);
    }
}
