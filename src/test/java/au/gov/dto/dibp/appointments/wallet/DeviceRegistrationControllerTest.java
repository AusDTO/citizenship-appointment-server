package au.gov.dto.dibp.appointments.wallet;

import au.gov.dto.dibp.appointments.client.Client;
import au.gov.dto.dibp.appointments.util.FakeTemplateLoader;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class DeviceRegistrationControllerTest {
    @Test
    public void registrationAcceptsValidDeviceLibraryIdentifierAndPushToken() throws Exception {
        DeviceRegistrationController controller = new DeviceRegistrationController(new DeviceRegistrationService(null, new FakeTemplateLoader(), null) {
            @Override
            public void addDeviceForClient(Client client, String deviceLibraryIdentifier, String pushToken) {
            }
        });

        String deviceLibraryIdentifier = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz_";
        String pushToken = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz_";
        ResponseEntity<String> responseEntity = controller.registerDevice(null, deviceLibraryIdentifier, new HashMap<String, Object>() {{
            put("pushToken", pushToken);
        }});

        assertThat(responseEntity.getStatusCode(), equalTo(HttpStatus.CREATED));
        assertThat(responseEntity.getBody(), equalTo(""));
    }

    @Test
    public void unregistrationAcceptsValidDeviceLibraryIdentifier() throws Exception {
        DeviceRegistrationController controller = new DeviceRegistrationController(new DeviceRegistrationService(null, new FakeTemplateLoader(), null) {
            @Override
            public void removeDeviceForClient(Client client, String deviceLibraryIdentifier) {
            }
        });

        String deviceLibraryIdentifier = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz_";
        ResponseEntity<String> responseEntity = controller.unregisterDevice(null, deviceLibraryIdentifier);

        assertThat(responseEntity.getStatusCode(), equalTo(HttpStatus.OK));
    }

    @Test
    public void registrationShouldRejectInvalidDeviceLibraryIdentifier() throws Exception {
        String invalidDeviceLibraryIdentifier = "<bad>request</bad>";
        ResponseEntity<String> responseEntity = new DeviceRegistrationController(null).registerDevice(null, invalidDeviceLibraryIdentifier, new HashMap<String, Object>() {{
            put("pushToken", "validPushToken");
        }});

        assertThat(responseEntity.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void registrationShouldRejectTooLongDeviceLibraryIdentifier() throws Exception {
        String tooLongDeviceLibraryIdentifier = StringUtils.repeat("a", 501);
        ResponseEntity<String> responseEntity = new DeviceRegistrationController(null).registerDevice(null, tooLongDeviceLibraryIdentifier, new HashMap<String, Object>() {{
            put("pushToken", "validPushToken");
        }});

        assertThat(responseEntity.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void registrationShouldRejectInvalidPushToken() throws Exception {
        String invalidPushToken = "<bad>request</bad>";
        ResponseEntity<String> responseEntity = new DeviceRegistrationController(null).registerDevice(null, "validDeviceLibraryIdentifier", new HashMap<String, Object>() {{
            put("pushToken", invalidPushToken);
        }});

        assertThat(responseEntity.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void registrationShouldRejectTooLongPushToken() throws Exception {
        String tooLongPushToken = StringUtils.repeat("a", 501);
        ResponseEntity<String> responseEntity = new DeviceRegistrationController(null).registerDevice(null, "validDeviceLibraryIdentifier", new HashMap<String, Object>() {{
            put("pushToken", tooLongPushToken);
        }});

        assertThat(responseEntity.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void unregistrationShouldRejectInvalidDeviceLibraryIdentifier() throws Exception {
        String invalidDeviceLibraryIdentifier = "<bad>request</bad>";
        DeviceRegistrationController controller = new DeviceRegistrationController(null);

        ResponseEntity<String> responseEntity = controller.unregisterDevice(null, invalidDeviceLibraryIdentifier);
        assertThat(responseEntity.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
    }
}
