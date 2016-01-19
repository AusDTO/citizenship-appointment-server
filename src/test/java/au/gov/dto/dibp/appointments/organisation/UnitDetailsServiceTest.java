package au.gov.dto.dibp.appointments.organisation;

import au.gov.dto.dibp.appointments.qflowintegration.ApiCallsSenderService;
import au.gov.dto.dibp.appointments.util.FakeTemplateLoader;
import au.gov.dto.dibp.appointments.util.ResponseWrapper;
import au.gov.dto.dibp.appointments.util.TemplateLoader;
import com.samskivert.mustache.Template;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class UnitDetailsServiceTest {

    private UnitDetailsService service;

    public static final String SERVICE_ID = "AAA";
    public static final String UNIT_ID = "232323";
    public static final String UNIT_ADDRESS = "Some Address in the middle of the forest";
    public static final String UNIT_LOCALTIME = "2016-01-06T11:06:25";

    @Before
    public void setUp(){
        TemplateLoader templateLoader = new FakeTemplateLoader();

        ApiCallsSenderService senderService = (Template template, Map<String, String> messageParams, String serviceAddress)
            -> getCallsResponses(messageParams, template);

        ServiceDetailsService serviceDetailsService = new ServiceDetailsService(senderService, templateLoader, "SomeUrl");

        service = new UnitDetailsService(serviceDetailsService, senderService, new TimeZoneDictionaryForTests(), templateLoader, "Some Service URL");
    }

    @Test
    public void test_getUnitAddressByServiceId_should_returnRelatedUnitAddress(){

        assertThat(service.getUnitAddressByServiceId(SERVICE_ID), is(UNIT_ADDRESS));

    }

    @Test
    public void test_getUnitAddress_should_returnTheUnitAddress(){

        assertThat(service.getUnitAddress(UNIT_ID), is(UNIT_ADDRESS));
    }

    @Test
    public void test_getUnitCurrentLocalTimeByServiceId_should_returnRelatedUnitCurrentLocalDateTime(){

        assertThat(service.getUnitCurrentLocalTimeByServiceId(SERVICE_ID), is(LocalDateTime.of(2016, 1, 6, 11, 6, 25)));
    }

    @Test
    public void test_getUnitCurrentLocalTime_should_returnUnitCurrentLocalDateTime(){

        assertThat(service.getUnitCurrentLocalTime(UNIT_ID), is(is(LocalDateTime.of(2016, 1, 6, 11, 6, 25))));
    }

    private ResponseWrapper getCallsResponses(Map<String, String> messageParams, Template template){
        if(template.toString().contains("GetUnit.")){
            return getUnitDetailsResponse(messageParams);
        } else if(template.toString().contains("GetUnitLocalTime.")){
            return getUnitCurrentLocalTimeResponse(messageParams);
        } else if(template.toString().contains("GetService.")){
            return getServiceDetailsResponse(messageParams);
        }
        return null;
    }

    private ResponseWrapper getUnitDetailsResponse(Map<String, String> messageParams){
        assertThat(messageParams.get("unitId"), is(UNIT_ID));

        String response =
        "<s:Body>\n" +
        "      <GetResponse xmlns=\"http://www.qnomy.com/Services\">\n" +
        "         <GetResult xmlns:b=\"http://schemas.datacontract.org/2004/07/QFlow.Library\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
        "            <b:Active>true</b:Active>\n" +
        "            <b:Description/>\n" +
        "            <b:ExtRef/>\n" +
        "            <b:Id>" + messageParams.get("unitId")+ "</b:Id>\n" +
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
        return new ResponseWrapper(200, response);
    }

    private ResponseWrapper getUnitCurrentLocalTimeResponse(Map<String, String> messageParams){
        assertThat(messageParams.get("unitId"), is(UNIT_ID));

        String response =
        "   <s:Body>\n" +
        "      <GetLocalTimeResponse xmlns=\"http://www.qnomy.com/Services\">\n" +
        "         <GetLocalTimeResult>" + UNIT_LOCALTIME + "</GetLocalTimeResult>\n" +
        "      </GetLocalTimeResponse>\n" +
        "   </s:Body>";
        return new ResponseWrapper(200, response);
    }

    private ResponseWrapper getServiceDetailsResponse(Map<String, String> messageParams){
        assertThat(messageParams.get("serviceId"), is(SERVICE_ID));

        String response =
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
        return new ResponseWrapper(200, response);
    }
}
