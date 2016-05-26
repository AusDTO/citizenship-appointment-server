package au.gov.dto.dibp.appointments.wallet;

import au.gov.dto.dibp.appointments.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class PassUpdateService {
    private static final Logger LOG = LoggerFactory.getLogger(PassUpdateService.class);

    private final DeviceRegistrationService deviceRegistrationService;
    private final PushNotificationClient pushNotificationClient;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Autowired
    public PassUpdateService(PushNotificationClient pushNotificationClient,
                             DeviceRegistrationService deviceRegistrationService) {
        this.pushNotificationClient = pushNotificationClient;
        this.deviceRegistrationService = deviceRegistrationService;
    }

    public Future<List<String>> sendPassUpdatesInBackground(Client client) {
        return executorService.submit(() -> {
            List<String> deviceLibraryIdentifiersUpdated = new ArrayList<>();
            Map<String, String> devices = deviceRegistrationService.getDevicesForClient(client);
            if (devices == null || devices.isEmpty()) {
                LOG.info("No Wallet devices registered for clientId=[{}], skipping pass update notifiations", client.getClientId());
                return deviceLibraryIdentifiersUpdated;
            }
            List<String> deviceRegistrationsToRemove = new ArrayList<>();
            pushNotificationClient.connect();
            for (Map.Entry<String, String> entry : devices.entrySet()) {
                String deviceLibraryIdentifier = entry.getKey();
                String pushToken = entry.getValue();
                LOG.info("Sending pass update notification for clientId=[{}] with deviceLibraryIdentifier=[{}] pushToken=[{}]", client.getClientId(), deviceLibraryIdentifier, pushToken);
                PushNotificationResponse response = pushNotificationClient.sendPushNotification(pushToken);
                if (response.isAccepted()) {
                    LOG.info("Push notification accepted by APNs gateway for deviceLibraryIdentifier=[{}] pushToken=[{}]", deviceLibraryIdentifier, pushToken);
                    deviceLibraryIdentifiersUpdated.add(deviceLibraryIdentifier);
                } else {
                    LOG.warn("Push notification rejected by the APNs service for deviceLibraryIdentifier=[{}] pushToken=[{}] with rejectionReason=[{}]", deviceLibraryIdentifier, pushToken, response.getRejectionReason());
                    if (response.isTokenInvalid()) {
                        LOG.info("The registration deviceLibraryIdentifier=[{}] pushToken=[{}] is invalid, removing device registration from client.", deviceLibraryIdentifier, pushToken);
                        deviceRegistrationsToRemove.add(deviceLibraryIdentifier);
                    }
                }
            }
            deviceRegistrationsToRemove.forEach(entryToRemove -> deviceRegistrationService.removeDeviceForClient(client, entryToRemove));
            return deviceLibraryIdentifiersUpdated;
        });
    }
}
