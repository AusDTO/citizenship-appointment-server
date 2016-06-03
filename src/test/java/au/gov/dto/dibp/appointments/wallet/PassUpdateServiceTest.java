package au.gov.dto.dibp.appointments.wallet;

import au.gov.dto.dibp.appointments.client.Client;
import au.gov.dto.dibp.appointments.util.FakeTemplateLoader;
import org.apache.commons.codec.binary.Base64InputStream;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class PassUpdateServiceTest {
    @Test
    public void testUpdates() throws Exception {
        PassUpdateService passUpdateService = new PassUpdateService(
                pushNotificationClient(),
                deviceRegistrationService(new HashMap<String, String>() {{
                    put("deviceLibraryIdentifier1", "pushToken1");
                    put("deviceLibraryIdentifier2", "pushToken2");
                }}));
        Client client = new Client("clientId", "familyName", "customerId", true, true, "unitId", "serviceId", "appointmentTypeId", true);

        List<String> devicesUpdated = passUpdateService.sendPassUpdatesInBackground(client).get(1L, TimeUnit.SECONDS);

        assertThat(devicesUpdated.size(), equalTo(2));
        assertThat(devicesUpdated, contains("deviceLibraryIdentifier1", "deviceLibraryIdentifier2"));
    }

    private PushNotificationClient pushNotificationClient() throws IOException {
        return new PushNotificationClient(
                "passTypeIdentifier",
                IOUtils.toString(new Base64InputStream(getClass().getClassLoader().getResourceAsStream("wallet/test.p12"), true)),
                "test",
                "true") {
            @Override
            public void connect() {
            }

            @Override
            public PushNotificationResponse sendPushNotification(String pushToken) {
                return new PushNotificationResponse(true, "", false);
            }

            @Override
            public void disconnect() {
            }
        };
    }

    private DeviceRegistrationService deviceRegistrationService(Map<String, String> devices) {
        return new TestDeviceRegistrationService(devices);
    }

    private static class TestDeviceRegistrationService extends DeviceRegistrationService {
        private final Map<String, String> devices;

        public TestDeviceRegistrationService(Map<String, String> devices) {
            super(null, new FakeTemplateLoader(), "serviceAddressCustomer");
            this.devices = devices;
        }

        @Override
        public void removeDeviceForClient(Client client, String deviceLibraryIdentifier) {
            devices.remove(deviceLibraryIdentifier);
        }

        @Override
        public Map<String, String> getDevicesForClient(Client client) {
            return new HashMap<>(devices);
        }
    }
}
