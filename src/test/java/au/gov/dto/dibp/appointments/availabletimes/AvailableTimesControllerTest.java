package au.gov.dto.dibp.appointments.availabletimes;

import au.gov.dto.dibp.appointments.client.Client;
import au.gov.dto.dibp.appointments.util.CalendarIdValidator;
import au.gov.dto.dibp.appointments.util.FakeTemplateLoader;
import au.gov.dto.dibp.appointments.util.ResponseWrapper;
import com.samskivert.mustache.Template;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class AvailableTimesControllerTest {

    private AvailableTimesController controller;

    @Before
    public void setUp(){
        controller = new AvailableTimesController(new AvailableTimesService(
                (Template requestTemplate, Map<String, String> messageParams, String serviceAddress) -> getResponse(),
                new FakeTemplateLoader(),
                "service"), new CalendarIdValidator());
    }

    @Test(expected = RuntimeException.class)
    public void test_getAvailableTimesWithLabel_should_throwAnExceptionIfCalendarIdIsNotInCorrectFormat(){
        controller.getAvailableTimesWithLabel(getStandardClient(), "Aba");
        fail();
    }

    @Test
    public void test_getAvailableTimesWithLabel_should_returnTheCalendarDate(){
        final Map<String, Object> result = controller.getAvailableTimesWithLabel(getStandardClient(), "1212");

        assertThat(result.get("date"), is("2016-02-24T00:00:00"));
    }

    @Test
    public void test_getAvailableTimesWithLabel_should_returnResultsWithTwoFields(){
        final List<TimeWithLabel> result = (List<TimeWithLabel>)controller.getAvailableTimesWithLabel(getStandardClient(), "1212").get("times");

        assertThat(result.size(), is(13));
        assertThat(result.get(3).getDisplayTime(), is("10:00 AM"));
        assertThat(result.get(3).getTime(), is("10:00"));


        assertThat(result.get(10).getDisplayTime(), is("2:00 PM"));
        assertThat(result.get(10).getTime(), is("14:00"));
    }


    private Client getStandardClient (){
        return new Client("123", "Surname", "40404", false, false, "1212", "5", "3", true);
    }


    private ResponseWrapper getResponse(){
        String response = "<s:Body>\n" +
                "      <GetDynamicSuggestedSlots2Response xmlns=\"http://www.qnomy.com/Services\">\n" +
                "         <GetDynamicSuggestedSlots2Result xmlns:b=\"http://schemas.datacontract.org/2004/07/QFlow.Library\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
                "            <b:AvailableSequences>\n" +
                "               <b:CalendarDate>2016-02-24T00:00:00</b:CalendarDate>\n" +
                "               <b:LocalTime>2016-02-08T11:16:34.383</b:LocalTime>\n" +
                "               <b:Segments>\n" +
                "                  <b:CalendarSegment>\n" +
                "                     <b:AllocatedToServiceId>0</b:AllocatedToServiceId>\n" +
                "                     <b:AllocatedToServiceName i:nil=\"true\"/>\n" +
                "                     <b:AllowOnlySpecificAppointmentTypes>false</b:AllowOnlySpecificAppointmentTypes>\n" +
                "                     <b:CalendarId>1767</b:CalendarId>\n" +
                "                     <b:CalendarSegmentId>1800</b:CalendarSegmentId>\n" +
                "                     <b:ConcurrencyPercentage>0</b:ConcurrencyPercentage>\n" +
                "                     <b:CurrentDurationAvailable>80</b:CurrentDurationAvailable>\n" +
                "                     <b:CurrentTotalSegmentReservation>0</b:CurrentTotalSegmentReservation>\n" +
                "                     <b:DefaultAppointmentDuration>20</b:DefaultAppointmentDuration>\n" +
                "                     <b:EndTime>620</b:EndTime>\n" +
                "                     <b:InitialMaxSimultaneousAppointments>0</b:InitialMaxSimultaneousAppointments>\n" +
                "                     <b:MaxSimultaneousAppointments>1</b:MaxSimultaneousAppointments>\n" +
                "                     <b:MinimizeConcurrency>false</b:MinimizeConcurrency>\n" +
                "                     <b:MinimumConcurrency>0</b:MinimumConcurrency>\n" +
                "                     <b:SegmentReservations i:nil=\"true\"/>\n" +
                "                     <b:StartTime>540</b:StartTime>\n" +
                "                     <b:TotalAvailableTime>80</b:TotalAvailableTime>\n" +
                "                  </b:CalendarSegment>\n" +
                "                  <b:CalendarSegment>\n" +
                "                     <b:AllocatedToServiceId>0</b:AllocatedToServiceId>\n" +
                "                     <b:AllocatedToServiceName i:nil=\"true\"/>\n" +
                "                     <b:AllowOnlySpecificAppointmentTypes>false</b:AllowOnlySpecificAppointmentTypes>\n" +
                "                     <b:CalendarId>1767</b:CalendarId>\n" +
                "                     <b:CalendarSegmentId>1801</b:CalendarSegmentId>\n" +
                "                     <b:ConcurrencyPercentage>0</b:ConcurrencyPercentage>\n" +
                "                     <b:CurrentDurationAvailable>80</b:CurrentDurationAvailable>\n" +
                "                     <b:CurrentTotalSegmentReservation>0</b:CurrentTotalSegmentReservation>\n" +
                "                     <b:DefaultAppointmentDuration>20</b:DefaultAppointmentDuration>\n" +
                "                     <b:EndTime>720</b:EndTime>\n" +
                "                     <b:InitialMaxSimultaneousAppointments>0</b:InitialMaxSimultaneousAppointments>\n" +
                "                     <b:MaxSimultaneousAppointments>1</b:MaxSimultaneousAppointments>\n" +
                "                     <b:MinimizeConcurrency>false</b:MinimizeConcurrency>\n" +
                "                     <b:MinimumConcurrency>0</b:MinimumConcurrency>\n" +
                "                     <b:SegmentReservations i:nil=\"true\"/>\n" +
                "                     <b:StartTime>640</b:StartTime>\n" +
                "                     <b:TotalAvailableTime>80</b:TotalAvailableTime>\n" +
                "                  </b:CalendarSegment>\n" +
                "                  <b:CalendarSegment>\n" +
                "                     <b:AllocatedToServiceId>0</b:AllocatedToServiceId>\n" +
                "                     <b:AllocatedToServiceName i:nil=\"true\"/>\n" +
                "                     <b:AllowOnlySpecificAppointmentTypes>false</b:AllowOnlySpecificAppointmentTypes>\n" +
                "                     <b:CalendarId>1767</b:CalendarId>\n" +
                "                     <b:CalendarSegmentId>1802</b:CalendarSegmentId>\n" +
                "                     <b:ConcurrencyPercentage>0</b:ConcurrencyPercentage>\n" +
                "                     <b:CurrentDurationAvailable>100</b:CurrentDurationAvailable>\n" +
                "                     <b:CurrentTotalSegmentReservation>0</b:CurrentTotalSegmentReservation>\n" +
                "                     <b:DefaultAppointmentDuration>20</b:DefaultAppointmentDuration>\n" +
                "                     <b:EndTime>900</b:EndTime>\n" +
                "                     <b:InitialMaxSimultaneousAppointments>0</b:InitialMaxSimultaneousAppointments>\n" +
                "                     <b:MaxSimultaneousAppointments>1</b:MaxSimultaneousAppointments>\n" +
                "                     <b:MinimizeConcurrency>false</b:MinimizeConcurrency>\n" +
                "                     <b:MinimumConcurrency>0</b:MinimumConcurrency>\n" +
                "                     <b:SegmentReservations i:nil=\"true\"/>\n" +
                "                     <b:StartTime>800</b:StartTime>\n" +
                "                     <b:TotalAvailableTime>100</b:TotalAvailableTime>\n" +
                "                  </b:CalendarSegment>\n" +
                "               </b:Segments>\n" +
                "               <b:Sequences>\n" +
                "                  <b:CalendarSlotSequence>\n" +
                "                     <b:AllocatedToServiceId>0</b:AllocatedToServiceId>\n" +
                "                     <b:AllocatedToServiceName i:nil=\"true\"/>\n" +
                "                     <b:AppointmentId>0</b:AppointmentId>\n" +
                "                     <b:AvatarId>0</b:AvatarId>\n" +
                "                     <b:CalendarId>1767</b:CalendarId>\n" +
                "                     <b:Date>2016-02-24T00:00:00</b:Date>\n" +
                "                     <b:DisplayProperties i:nil=\"true\"/>\n" +
                "                     <b:Duration>80</b:Duration>\n" +
                "                     <b:EndTime>620</b:EndTime>\n" +
                "                     <b:FirstOrdinalNumber>0</b:FirstOrdinalNumber>\n" +
                "                     <b:LastOrdinalNumber>0</b:LastOrdinalNumber>\n" +
                "                     <b:LockedByUserId>0</b:LockedByUserId>\n" +
                "                     <b:ResourceId>0</b:ResourceId>\n" +
                "                     <b:ServiceId>32</b:ServiceId>\n" +
                "                     <b:SlotExtendedInfo i:nil=\"true\"/>\n" +
                "                     <b:SlotReservationColorCode>0</b:SlotReservationColorCode>\n" +
                "                     <b:SlotReservationReasonId>0</b:SlotReservationReasonId>\n" +
                "                     <b:SlotReservationReasonName/>\n" +
                "                     <b:SlotsCount>0</b:SlotsCount>\n" +
                "                     <b:StartTime>540</b:StartTime>\n" +
                "                     <b:Status>Vacant</b:Status>\n" +
                "                  </b:CalendarSlotSequence>\n" +
                "                  <b:CalendarSlotSequence>\n" +
                "                     <b:AllocatedToServiceId>0</b:AllocatedToServiceId>\n" +
                "                     <b:AllocatedToServiceName i:nil=\"true\"/>\n" +
                "                     <b:AppointmentId>0</b:AppointmentId>\n" +
                "                     <b:AvatarId>0</b:AvatarId>\n" +
                "                     <b:CalendarId>1767</b:CalendarId>\n" +
                "                     <b:Date>2016-02-24T00:00:00</b:Date>\n" +
                "                     <b:DisplayProperties i:nil=\"true\"/>\n" +
                "                     <b:Duration>80</b:Duration>\n" +
                "                     <b:EndTime>720</b:EndTime>\n" +
                "                     <b:FirstOrdinalNumber>0</b:FirstOrdinalNumber>\n" +
                "                     <b:LastOrdinalNumber>0</b:LastOrdinalNumber>\n" +
                "                     <b:LockedByUserId>0</b:LockedByUserId>\n" +
                "                     <b:ResourceId>0</b:ResourceId>\n" +
                "                     <b:ServiceId>32</b:ServiceId>\n" +
                "                     <b:SlotExtendedInfo i:nil=\"true\"/>\n" +
                "                     <b:SlotReservationColorCode>0</b:SlotReservationColorCode>\n" +
                "                     <b:SlotReservationReasonId>0</b:SlotReservationReasonId>\n" +
                "                     <b:SlotReservationReasonName/>\n" +
                "                     <b:SlotsCount>0</b:SlotsCount>\n" +
                "                     <b:StartTime>640</b:StartTime>\n" +
                "                     <b:Status>Vacant</b:Status>\n" +
                "                  </b:CalendarSlotSequence>\n" +
                "                  <b:CalendarSlotSequence>\n" +
                "                     <b:AllocatedToServiceId>0</b:AllocatedToServiceId>\n" +
                "                     <b:AllocatedToServiceName i:nil=\"true\"/>\n" +
                "                     <b:AppointmentId>0</b:AppointmentId>\n" +
                "                     <b:AvatarId>0</b:AvatarId>\n" +
                "                     <b:CalendarId>1767</b:CalendarId>\n" +
                "                     <b:Date>2016-02-24T00:00:00</b:Date>\n" +
                "                     <b:DisplayProperties i:nil=\"true\"/>\n" +
                "                     <b:Duration>100</b:Duration>\n" +
                "                     <b:EndTime>900</b:EndTime>\n" +
                "                     <b:FirstOrdinalNumber>0</b:FirstOrdinalNumber>\n" +
                "                     <b:LastOrdinalNumber>0</b:LastOrdinalNumber>\n" +
                "                     <b:LockedByUserId>0</b:LockedByUserId>\n" +
                "                     <b:ResourceId>0</b:ResourceId>\n" +
                "                     <b:ServiceId>32</b:ServiceId>\n" +
                "                     <b:SlotExtendedInfo i:nil=\"true\"/>\n" +
                "                     <b:SlotReservationColorCode>0</b:SlotReservationColorCode>\n" +
                "                     <b:SlotReservationReasonId>0</b:SlotReservationReasonId>\n" +
                "                     <b:SlotReservationReasonName/>\n" +
                "                     <b:SlotsCount>0</b:SlotsCount>\n" +
                "                     <b:StartTime>800</b:StartTime>\n" +
                "                     <b:Status>Vacant</b:Status>\n" +
                "                  </b:CalendarSlotSequence>\n" +
                "               </b:Sequences>\n" +
                "            </b:AvailableSequences>\n" +
                "            <b:SuggestedSlots>\n" +
                "               <b:DynamicCalendarSuggestedSlotItem>\n" +
                "                  <b:AllocatedToServiceId>0</b:AllocatedToServiceId>\n" +
                "                  <b:Duration>20</b:Duration>\n" +
                "                  <b:LockedByUserId>0</b:LockedByUserId>\n" +
                "                  <b:SlotReservationColorCode>0</b:SlotReservationColorCode>\n" +
                "                  <b:SlotReservationReasonId>0</b:SlotReservationReasonId>\n" +
                "                  <b:SlotReservationReasonName/>\n" +
                "                  <b:StartTime>540</b:StartTime>\n" +
                "                  <b:Status>Vacant</b:Status>\n" +
                "                  <b:_AppointmentDuration>20</b:_AppointmentDuration>\n" +
                "                  <b:_OrdinalNumber>0</b:_OrdinalNumber>\n" +
                "               </b:DynamicCalendarSuggestedSlotItem>\n" +
                "               <b:DynamicCalendarSuggestedSlotItem>\n" +
                "                  <b:AllocatedToServiceId>0</b:AllocatedToServiceId>\n" +
                "                  <b:Duration>20</b:Duration>\n" +
                "                  <b:LockedByUserId>0</b:LockedByUserId>\n" +
                "                  <b:SlotReservationColorCode>0</b:SlotReservationColorCode>\n" +
                "                  <b:SlotReservationReasonId>0</b:SlotReservationReasonId>\n" +
                "                  <b:SlotReservationReasonName/>\n" +
                "                  <b:StartTime>560</b:StartTime>\n" +
                "                  <b:Status>Vacant</b:Status>\n" +
                "                  <b:_AppointmentDuration>20</b:_AppointmentDuration>\n" +
                "                  <b:_OrdinalNumber>0</b:_OrdinalNumber>\n" +
                "               </b:DynamicCalendarSuggestedSlotItem>\n" +
                "               <b:DynamicCalendarSuggestedSlotItem>\n" +
                "                  <b:AllocatedToServiceId>0</b:AllocatedToServiceId>\n" +
                "                  <b:Duration>20</b:Duration>\n" +
                "                  <b:LockedByUserId>0</b:LockedByUserId>\n" +
                "                  <b:SlotReservationColorCode>0</b:SlotReservationColorCode>\n" +
                "                  <b:SlotReservationReasonId>0</b:SlotReservationReasonId>\n" +
                "                  <b:SlotReservationReasonName/>\n" +
                "                  <b:StartTime>580</b:StartTime>\n" +
                "                  <b:Status>Vacant</b:Status>\n" +
                "                  <b:_AppointmentDuration>20</b:_AppointmentDuration>\n" +
                "                  <b:_OrdinalNumber>0</b:_OrdinalNumber>\n" +
                "               </b:DynamicCalendarSuggestedSlotItem>\n" +
                "               <b:DynamicCalendarSuggestedSlotItem>\n" +
                "                  <b:AllocatedToServiceId>0</b:AllocatedToServiceId>\n" +
                "                  <b:Duration>20</b:Duration>\n" +
                "                  <b:LockedByUserId>0</b:LockedByUserId>\n" +
                "                  <b:SlotReservationColorCode>0</b:SlotReservationColorCode>\n" +
                "                  <b:SlotReservationReasonId>0</b:SlotReservationReasonId>\n" +
                "                  <b:SlotReservationReasonName/>\n" +
                "                  <b:StartTime>600</b:StartTime>\n" +
                "                  <b:Status>Vacant</b:Status>\n" +
                "                  <b:_AppointmentDuration>20</b:_AppointmentDuration>\n" +
                "                  <b:_OrdinalNumber>0</b:_OrdinalNumber>\n" +
                "               </b:DynamicCalendarSuggestedSlotItem>\n" +
                "               <b:DynamicCalendarSuggestedSlotItem>\n" +
                "                  <b:AllocatedToServiceId>0</b:AllocatedToServiceId>\n" +
                "                  <b:Duration>20</b:Duration>\n" +
                "                  <b:LockedByUserId>0</b:LockedByUserId>\n" +
                "                  <b:SlotReservationColorCode>0</b:SlotReservationColorCode>\n" +
                "                  <b:SlotReservationReasonId>0</b:SlotReservationReasonId>\n" +
                "                  <b:SlotReservationReasonName/>\n" +
                "                  <b:StartTime>640</b:StartTime>\n" +
                "                  <b:Status>Vacant</b:Status>\n" +
                "                  <b:_AppointmentDuration>20</b:_AppointmentDuration>\n" +
                "                  <b:_OrdinalNumber>0</b:_OrdinalNumber>\n" +
                "               </b:DynamicCalendarSuggestedSlotItem>\n" +
                "               <b:DynamicCalendarSuggestedSlotItem>\n" +
                "                  <b:AllocatedToServiceId>0</b:AllocatedToServiceId>\n" +
                "                  <b:Duration>20</b:Duration>\n" +
                "                  <b:LockedByUserId>0</b:LockedByUserId>\n" +
                "                  <b:SlotReservationColorCode>0</b:SlotReservationColorCode>\n" +
                "                  <b:SlotReservationReasonId>0</b:SlotReservationReasonId>\n" +
                "                  <b:SlotReservationReasonName/>\n" +
                "                  <b:StartTime>660</b:StartTime>\n" +
                "                  <b:Status>Vacant</b:Status>\n" +
                "                  <b:_AppointmentDuration>20</b:_AppointmentDuration>\n" +
                "                  <b:_OrdinalNumber>0</b:_OrdinalNumber>\n" +
                "               </b:DynamicCalendarSuggestedSlotItem>\n" +
                "               <b:DynamicCalendarSuggestedSlotItem>\n" +
                "                  <b:AllocatedToServiceId>0</b:AllocatedToServiceId>\n" +
                "                  <b:Duration>20</b:Duration>\n" +
                "                  <b:LockedByUserId>0</b:LockedByUserId>\n" +
                "                  <b:SlotReservationColorCode>0</b:SlotReservationColorCode>\n" +
                "                  <b:SlotReservationReasonId>0</b:SlotReservationReasonId>\n" +
                "                  <b:SlotReservationReasonName/>\n" +
                "                  <b:StartTime>680</b:StartTime>\n" +
                "                  <b:Status>Vacant</b:Status>\n" +
                "                  <b:_AppointmentDuration>20</b:_AppointmentDuration>\n" +
                "                  <b:_OrdinalNumber>0</b:_OrdinalNumber>\n" +
                "               </b:DynamicCalendarSuggestedSlotItem>\n" +
                "               <b:DynamicCalendarSuggestedSlotItem>\n" +
                "                  <b:AllocatedToServiceId>0</b:AllocatedToServiceId>\n" +
                "                  <b:Duration>20</b:Duration>\n" +
                "                  <b:LockedByUserId>0</b:LockedByUserId>\n" +
                "                  <b:SlotReservationColorCode>0</b:SlotReservationColorCode>\n" +
                "                  <b:SlotReservationReasonId>0</b:SlotReservationReasonId>\n" +
                "                  <b:SlotReservationReasonName/>\n" +
                "                  <b:StartTime>700</b:StartTime>\n" +
                "                  <b:Status>Vacant</b:Status>\n" +
                "                  <b:_AppointmentDuration>20</b:_AppointmentDuration>\n" +
                "                  <b:_OrdinalNumber>0</b:_OrdinalNumber>\n" +
                "               </b:DynamicCalendarSuggestedSlotItem>\n" +
                "               <b:DynamicCalendarSuggestedSlotItem>\n" +
                "                  <b:AllocatedToServiceId>0</b:AllocatedToServiceId>\n" +
                "                  <b:Duration>20</b:Duration>\n" +
                "                  <b:LockedByUserId>0</b:LockedByUserId>\n" +
                "                  <b:SlotReservationColorCode>0</b:SlotReservationColorCode>\n" +
                "                  <b:SlotReservationReasonId>0</b:SlotReservationReasonId>\n" +
                "                  <b:SlotReservationReasonName/>\n" +
                "                  <b:StartTime>800</b:StartTime>\n" +
                "                  <b:Status>Vacant</b:Status>\n" +
                "                  <b:_AppointmentDuration>20</b:_AppointmentDuration>\n" +
                "                  <b:_OrdinalNumber>0</b:_OrdinalNumber>\n" +
                "               </b:DynamicCalendarSuggestedSlotItem>\n" +
                "               <b:DynamicCalendarSuggestedSlotItem>\n" +
                "                  <b:AllocatedToServiceId>0</b:AllocatedToServiceId>\n" +
                "                  <b:Duration>20</b:Duration>\n" +
                "                  <b:LockedByUserId>0</b:LockedByUserId>\n" +
                "                  <b:SlotReservationColorCode>0</b:SlotReservationColorCode>\n" +
                "                  <b:SlotReservationReasonId>0</b:SlotReservationReasonId>\n" +
                "                  <b:SlotReservationReasonName/>\n" +
                "                  <b:StartTime>820</b:StartTime>\n" +
                "                  <b:Status>Vacant</b:Status>\n" +
                "                  <b:_AppointmentDuration>20</b:_AppointmentDuration>\n" +
                "                  <b:_OrdinalNumber>0</b:_OrdinalNumber>\n" +
                "               </b:DynamicCalendarSuggestedSlotItem>\n" +
                "               <b:DynamicCalendarSuggestedSlotItem>\n" +
                "                  <b:AllocatedToServiceId>0</b:AllocatedToServiceId>\n" +
                "                  <b:Duration>20</b:Duration>\n" +
                "                  <b:LockedByUserId>0</b:LockedByUserId>\n" +
                "                  <b:SlotReservationColorCode>0</b:SlotReservationColorCode>\n" +
                "                  <b:SlotReservationReasonId>0</b:SlotReservationReasonId>\n" +
                "                  <b:SlotReservationReasonName/>\n" +
                "                  <b:StartTime>840</b:StartTime>\n" +
                "                  <b:Status>Vacant</b:Status>\n" +
                "                  <b:_AppointmentDuration>20</b:_AppointmentDuration>\n" +
                "                  <b:_OrdinalNumber>0</b:_OrdinalNumber>\n" +
                "               </b:DynamicCalendarSuggestedSlotItem>\n" +
                "               <b:DynamicCalendarSuggestedSlotItem>\n" +
                "                  <b:AllocatedToServiceId>0</b:AllocatedToServiceId>\n" +
                "                  <b:Duration>20</b:Duration>\n" +
                "                  <b:LockedByUserId>0</b:LockedByUserId>\n" +
                "                  <b:SlotReservationColorCode>0</b:SlotReservationColorCode>\n" +
                "                  <b:SlotReservationReasonId>0</b:SlotReservationReasonId>\n" +
                "                  <b:SlotReservationReasonName/>\n" +
                "                  <b:StartTime>860</b:StartTime>\n" +
                "                  <b:Status>Vacant</b:Status>\n" +
                "                  <b:_AppointmentDuration>20</b:_AppointmentDuration>\n" +
                "                  <b:_OrdinalNumber>0</b:_OrdinalNumber>\n" +
                "               </b:DynamicCalendarSuggestedSlotItem>\n" +
                "               <b:DynamicCalendarSuggestedSlotItem>\n" +
                "                  <b:AllocatedToServiceId>0</b:AllocatedToServiceId>\n" +
                "                  <b:Duration>20</b:Duration>\n" +
                "                  <b:LockedByUserId>0</b:LockedByUserId>\n" +
                "                  <b:SlotReservationColorCode>0</b:SlotReservationColorCode>\n" +
                "                  <b:SlotReservationReasonId>0</b:SlotReservationReasonId>\n" +
                "                  <b:SlotReservationReasonName/>\n" +
                "                  <b:StartTime>880</b:StartTime>\n" +
                "                  <b:Status>Vacant</b:Status>\n" +
                "                  <b:_AppointmentDuration>20</b:_AppointmentDuration>\n" +
                "                  <b:_OrdinalNumber>0</b:_OrdinalNumber>\n" +
                "               </b:DynamicCalendarSuggestedSlotItem>\n" +
                "            </b:SuggestedSlots>\n" +
                "         </GetDynamicSuggestedSlots2Result>\n" +
                "      </GetDynamicSuggestedSlots2Response>\n" +
                "   </s:Body>";
        return new ResponseWrapper(200, response);
    }
}
