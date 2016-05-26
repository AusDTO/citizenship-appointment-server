package au.gov.dto.dibp.appointments.wallet;

import com.relayrides.pushy.apns.ApnsClient;
import com.relayrides.pushy.apns.ApnsPushNotification;
import com.relayrides.pushy.apns.util.ApnsPayloadBuilder;
import com.relayrides.pushy.apns.util.SimpleApnsPushNotification;
import org.apache.commons.codec.binary.Base64InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.net.ssl.SSLException;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
class PushNotificationClient {
    private static final Logger LOG = LoggerFactory.getLogger(PushNotificationClient.class);
    private static final long DISCONNECT_TIMEOUT_SECONDS = 10L;
    private static final long SEND_TIMEOUT_SECONDS = 10L;

    private final String passTypeIdentifier;
    private final ApnsClient<ApnsPushNotification> client;

    @Autowired
    public PushNotificationClient(@Value("${wallet.pass.type.identifier}") String passTypeIdentifier,
                                  @Value("${wallet.private.key.p12.base64}") String privateKeyP12Base64,
                                  @Value("${wallet.private.key.passphrase}") String privateKeyPassPhrase) {
        this.passTypeIdentifier = passTypeIdentifier;
        ByteArrayInputStream base64EncodedPrivateKeyAndCertificatePkcs12AsStream = new ByteArrayInputStream(privateKeyP12Base64.getBytes(StandardCharsets.UTF_8));
        Base64InputStream privateKeyAndCertificatePkcs12AsStream = new Base64InputStream(base64EncodedPrivateKeyAndCertificatePkcs12AsStream);
        try {
            this.client = new ApnsClient<>(privateKeyAndCertificatePkcs12AsStream, privateKeyPassPhrase);
        } catch (SSLException e) {
            throw new RuntimeException("Problem creating APNs client", e);
        }
    }

    @PostConstruct
    public void connect() {
        LOG.info("Connecting to APNs service");
        client.connect(ApnsClient.PRODUCTION_APNS_HOST);
    }

    public PushNotificationResponse sendPushNotification(String pushToken) {
        LOG.info("Sending push notification with pushToken=[{}]", pushToken);
        ApnsPayloadBuilder payloadBuilder = new ApnsPayloadBuilder();
        payloadBuilder.setAlertBody("{}");
        String payload = payloadBuilder.buildWithDefaultMaximumLength();
        try {
            com.relayrides.pushy.apns.PushNotificationResponse<ApnsPushNotification> response = client.sendNotification(new SimpleApnsPushNotification(pushToken, passTypeIdentifier, payload)).get(SEND_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            return new PushNotificationResponse(response.isAccepted(), response.getRejectionReason(), response.getTokenInvalidationTimestamp() != null);
        } catch (InterruptedException|ExecutionException|TimeoutException e) {
            throw new RuntimeException("Problem sending push notification with pushToken=[" + pushToken + "]", e);
        }
    }

    @PreDestroy
    public void disconnect() {
        LOG.info("Disconnecting from APNs service");
        try {
            client.disconnect().await(DISCONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException("Problem disconnecting from the APNs service", e);
        }
    }
}
