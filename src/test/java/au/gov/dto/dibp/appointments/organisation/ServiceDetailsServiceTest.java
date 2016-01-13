package au.gov.dto.dibp.appointments.organisation;

import au.gov.dto.dibp.appointments.util.FakeTemplateLoader;
import au.gov.dto.dibp.appointments.util.ResponseWrapper;
import com.samskivert.mustache.Template;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ServiceDetailsServiceTest {

    private ServiceDetailsService service;

    @Before
    public void setUp(){
        service = new ServiceDetailsService(
                (Template requestTemplate, Map<String, String> messageParams, String serviceAddress)
                        -> getCallsResponses(requestTemplate),
                new FakeTemplateLoader(),
                "Some Service URL");
    }

    @Test
    public void test_getUnitIdForService_should_returnUnitIdGivenAServiceId(){

        assertThat(service.getUnitIdForService("1234"), is("3"));
    }

    @Test
    public void test_getServiceIdByExternalReference_should_returnServiceIdGivenAServiceExternalReference(){

        ServiceDetails serviceDetails = service.getServiceByExternalReference("SYD_CI_20m");
        assertThat(serviceDetails.getServiceId(), is("5"));
        assertThat(serviceDetails.getUnitId(), is("3"));
        assertThat(serviceDetails.getExternalReference(), is("SYD_CI_20m"));
    }

    private ResponseWrapper getCallsResponses(Template template){
        if(template.toString().contains("GetService.")){
            return getServiceDetailsResponse();
        } else if(template.toString().contains("GetServiceByExternalReference.")){
            return getServiceByExtRefResponse();
        }
        return null;
    }

    private ResponseWrapper getServiceDetailsResponse(){
        String response =
        "   <s:Body>\n" +
        "      <GetResponse xmlns=\"http://www.qnomy.com/Services\">\n" +
        "         <GetResult xmlns:b=\"http://schemas.datacontract.org/2004/07/QFlow.Library\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
        "            <b:Active>true</b:Active>\n" +
        "            <b:Description/>\n" +
        "            <b:ExtRef/>\n" +
        "            <b:Id>5</b:Id>\n" +
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
        "            <b:UnitId>3</b:UnitId>\n" +
        "            <b:WaitingAreaLocationId>0</b:WaitingAreaLocationId>\n" +
        "            <b:WorkingHoursId>0</b:WorkingHoursId>\n" +
        "         </GetResult>\n" +
        "      </GetResponse>\n" +
        "   </s:Body>";
        return new ResponseWrapper(200, response);
    }

    private ResponseWrapper getServiceByExtRefResponse(){
        String response =
        "<s:Body>\n" +
        "      <GetByExtRefResponse xmlns=\"http://www.qnomy.com/Services\">\n" +
        "         <GetByExtRefResult xmlns:b=\"http://schemas.datacontract.org/2004/07/QFlow.Library\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
        "            <b:Service>\n" +
        "               <b:Active>true</b:Active>\n" +
        "               <b:ExtRef>SYD_CI_20m</b:ExtRef>\n" +
        "               <b:Id>5</b:Id>\n" +
        "               <b:Name>Citizenship Interview 20min</b:Name>\n" +
        "               <b:UnitId>3</b:UnitId>\n" +
        "            </b:Service>\n" +
        "         </GetByExtRefResult>\n" +
        "      </GetByExtRefResponse>\n" +
        "   </s:Body>";
        return new ResponseWrapper(200, response);
    }

}
