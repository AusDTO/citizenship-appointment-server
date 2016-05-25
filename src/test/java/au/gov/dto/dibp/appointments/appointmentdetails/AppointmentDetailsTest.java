package au.gov.dto.dibp.appointments.appointmentdetails;

import org.junit.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class AppointmentDetailsTest {
    @Test
    public void testAppointmentDateTimeWithTimeZoneDuringDaylightSavings() throws Exception {
        LocalDateTime localDateTime = LocalDateTime.of(2016, 1, 2, 12, 34, 56);
        String timeZoneIANA = "Australia/Adelaide";

        AppointmentDetails appointment = new AppointmentDetails(localDateTime, 0, "processId", "serviceId", "customerId", "unitName", "unitAddress", timeZoneIANA);

        ZonedDateTime dateTimeWithTimeZone = appointment.getDateTimeWithTimeZone();
        assertThat(dateTimeWithTimeZone, equalTo(ZonedDateTime.of(LocalDateTime.of(2016, 1, 2, 12, 34, 56), ZoneId.of(timeZoneIANA))));
        assertThat(dateTimeWithTimeZone.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME), equalTo("2016-01-02T12:34:56+10:30"));
    }

    @Test
    public void testAppointmentDateTimeWithTimeZoneDuringStandardTime() throws Exception {
        LocalDateTime localDateTime = LocalDateTime.of(2016, 6, 2, 12, 34, 56);
        String timeZoneIANA = "Australia/Adelaide";

        AppointmentDetails appointment = new AppointmentDetails(localDateTime, 0, "processId", "serviceId", "customerId", "unitName", "unitAddress", timeZoneIANA);

        ZonedDateTime dateTimeWithTimeZone = appointment.getDateTimeWithTimeZone();
        assertThat(dateTimeWithTimeZone, equalTo(ZonedDateTime.of(LocalDateTime.of(2016, 6, 2, 12, 34, 56), ZoneId.of(timeZoneIANA))));
        assertThat(dateTimeWithTimeZone.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME), equalTo("2016-06-02T12:34:56+09:30"));
    }
}
