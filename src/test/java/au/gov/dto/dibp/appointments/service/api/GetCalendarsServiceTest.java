package au.gov.dto.dibp.appointments.service.api;

import au.gov.dto.dibp.appointments.model.CalendarEntry;
import com.squareup.okhttp.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;
import java.util.SortedMap;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GetCalendarsServiceTest {

    @Mock
    ApiCallsSenderService senderService;

    @InjectMocks
    GetCalendarsService service;

    @Before
    public void setup() {
        ReflectionTestUtils.setField(service, "SERVICE_ADDRESS_SERVICE", "http://someurl");
    }

    @Test
    public void getCalendars_shouldPutServiceIdIntoData() throws ParserConfigurationException, SAXException, XPathExpressionException, IOException {
        String serviceId = "123";
        ArgumentCaptor<Map> dataArgumentCaptor = ArgumentCaptor.forClass(Map.class);

        when(senderService.sendRequest(anyString(), dataArgumentCaptor.capture(), anyString())).thenReturn(getStandardResponse());
        service.getCalendars(serviceId, LocalDate.now(), LocalDate.now());

        Map<String, String> capturedData = dataArgumentCaptor.getValue();
        assertThat(capturedData.size(), is(3));
        assertThat(capturedData.get("serviceId"), is(serviceId));
    }

    @Test
    public void getCalendars_shouldPutStartDateIntoData() throws ParserConfigurationException, SAXException, XPathExpressionException, IOException {
        LocalDate startDate = LocalDate.of(2015, 12, 16);
        ArgumentCaptor<Map> dataArgumentCaptor = ArgumentCaptor.forClass(Map.class);

        when(senderService.sendRequest(anyString(), dataArgumentCaptor.capture(), anyString())).thenReturn(getStandardResponse());
        service.getCalendars("", startDate, LocalDate.now());

        Map<String, String> capturedData = dataArgumentCaptor.getValue();
        assertThat(capturedData.size(), is(3));
        assertThat(capturedData.get("startDate"), is(startDate.toString()+"T00:00:00"));
    }

    @Test
    public void getCalendars_shouldPutEndDateIntoData() throws ParserConfigurationException, SAXException, XPathExpressionException, IOException {
        LocalDate endDate = LocalDate.of(2018, 12, 16);
        ArgumentCaptor<Map> dataArgumentCaptor = ArgumentCaptor.forClass(Map.class);

        when(senderService.sendRequest(anyString(), dataArgumentCaptor.capture(), anyString())).thenReturn(getStandardResponse());
        service.getCalendars("", LocalDate.now(), endDate);

        Map<String, String> capturedData = dataArgumentCaptor.getValue();
        assertThat(capturedData.size(), is(3));
        assertThat(capturedData.get("endDate"), is(endDate.toString()+"T00:00:00"));
    }

    @Test
    public void getCalendars_shouldCallSendServiceWithCorrectArguments() throws ParserConfigurationException, SAXException, XPathExpressionException, IOException {
        String serviceId = "123";
        LocalDate startDate = LocalDate.of(2015, 12, 16);
        LocalDate endDate = LocalDate.of(2018, 12, 16);

        ArgumentCaptor<Map> dataArgumentCaptor = ArgumentCaptor.forClass(Map.class);
        ArgumentCaptor<String> templatePathArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> serviceAddressArgumentCaptor = ArgumentCaptor.forClass(String.class);

        when(senderService.sendRequest(templatePathArgumentCaptor.capture(), dataArgumentCaptor.capture(), serviceAddressArgumentCaptor.capture())).thenReturn(getStandardResponse());
        service.getCalendars(serviceId, startDate, endDate);

        Map<String, String> capturedData = dataArgumentCaptor.getValue();
        assertThat(capturedData.size(), is(3));
        assertThat(templatePathArgumentCaptor.getValue(), endsWith("GetCalendars.mustache"));
        assertThat(serviceAddressArgumentCaptor.getValue(), is("http://someurl"));
    }

    @Test
    public void getCalendars_shouldConvertResponseIntoCalendarEntryObject() throws ParserConfigurationException, SAXException, XPathExpressionException, IOException {
        when(senderService.sendRequest(anyString(), Matchers.<Map<String, String>>any(), anyString())).thenReturn(getStandardResponse());
        SortedMap<String, CalendarEntry> calendarEntries = service.getCalendars("", LocalDate.now(), LocalDate.now());

        CalendarEntry calendarEntry = calendarEntries.get(calendarEntries.firstKey());

        assertThat(calendarEntries.size(), is(1));
        assertThat(calendarEntry.getId(), is("192"));
        assertThat(calendarEntry.getCalendarDate(), is("2015-12-16"));
        assertThat(calendarEntry.getAvailableSlotsCount(), is(12));
    }

    // FIXME(Emily) test inactive entries are not included in response

    private Response getStandardResponse(){
        String response =
            "<s:Body>\n" +
            "      <GetCalendarsResponse xmlns=\"http://www.qnomy.com/Services\">\n" +
            "         <GetCalendarsResult xmlns:b=\"http://schemas.datacontract.org/2004/07/QFlow.Library\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
            "            <b:Calendar>\n" +
            "               <b:CalendarDate>2015-12-16T00:00:00</b:CalendarDate>\n" +
            "               <b:Id>192</b:Id>\n" +
            "               <b:Status>Active</b:Status>\n" +
            "               <b:VacantSlotsAfternoon>2</b:VacantSlotsAfternoon>\n" +
            "               <b:VacantSlotsEvening>1</b:VacantSlotsEvening>\n" +
            "               <b:VacantSlotsMorning>3</b:VacantSlotsMorning>\n" +
            "               <b:VacantSlotsNight>0</b:VacantSlotsNight>\n" +
            "               <b:VacantSlotsNoon>6</b:VacantSlotsNoon>\n" +
            "            </b:Calendar>\n" +
            "         </GetCalendarsResult>\n" +
            "      </GetCalendarsResponse>\n" +
            "   </s:Body>";

        Response.Builder responseBuilder = new Response.Builder();
        responseBuilder.code(200);
        responseBuilder.protocol(Protocol.HTTP_1_1);

        ResponseBody body = ResponseBody.create(MediaType.parse("application/soap+xml; charset=utf-8"), response);
        responseBuilder.body(body);

        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url("http://test.test.com");
        responseBuilder.request(requestBuilder.build());
        return responseBuilder.build();
    }

}
