package au.gov.dto.dibp.appointments.wallet;

import au.gov.dto.dibp.appointments.appointmentdetails.AppointmentDetails;
import au.gov.dto.dibp.appointments.client.Client;
import de.brendamour.jpasskit.PKPass;
import org.junit.Test;

import java.net.URL;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class PassBuilderTest {
    @Test
    public void testPassInformationNonDaylightSavingsTime() throws Exception {
        PassBuilder passBuilder = createPassBuilder();
        Client client = new Client("clientId", "familyName", "customerId", true, true, "unitId", "serviceId", "appointmentTypeId", true);
        LocalDateTime appointmentDate = LocalDateTime.of(2016, 5, 20, 15, 20, 0);
        AppointmentDetails appointment = new AppointmentDetails(appointmentDate, 1, "processId", "serviceId", "customerId", "unitName", "unitAddress", "Australia/Melbourne");
        URL walletWebServiceUrl = new URL("http://localhost:8083/wallet");

        Pass pass = passBuilder.createAppointmentPassForClient(client, appointment, walletWebServiceUrl);

        PKPass pkPass = pass.getUnsignedPass();
        assertThat(pkPass.getEventTicket().getSecondaryFields().get(0).getValue(), equalTo("2016-05-20T15:20+10:00"));
    }

    @Test
    public void testPassInformationDaylightSavingsTime() throws Exception {
        PassBuilder passBuilder = createPassBuilder();
        Client client = new Client("clientId", "familyName", "customerId", true, true, "unitId", "serviceId", "appointmentTypeId", true);
        LocalDateTime appointmentDate = LocalDateTime.of(2016, 1, 31, 9, 0, 0);
        AppointmentDetails appointment = new AppointmentDetails(appointmentDate, 1, "processId", "serviceId", "customerId", "unitName", "unitAddress", "Australia/Melbourne");
        URL walletWebServiceUrl = new URL("http://localhost:8083/wallet");

        Pass pass = passBuilder.createAppointmentPassForClient(client, appointment, walletWebServiceUrl);

        PKPass pkPass = pass.getUnsignedPass();
        assertThat(pkPass.getEventTicket().getSecondaryFields().get(0).getValue(), equalTo("2016-01-31T09:00+11:00"));
    }

    /**
     * Uses a dummy certificate and key
     */
    private PassBuilder createPassBuilder() {
        return new PassBuilder(
                "passTypeIdentifier",
                "teamIdentifier",
                "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",
                "test");
    }
}
