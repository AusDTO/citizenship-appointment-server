package com.relayrides.pushy.apns;

import com.relayrides.pushy.apns.util.SimpleApnsPushNotification;

import java.util.Date;

/**
 * Required to access package local class com.relayrides.pushy.apns.SimplePushNotificationResponse
 */
public class TestPushNotificationResponse extends SimplePushNotificationResponse {
    public TestPushNotificationResponse(boolean success, Date tokenExpirationTimestamp) {
        super(new SimpleApnsPushNotification("token", "topic", "payload"), success, "rejectionReason", tokenExpirationTimestamp);
    }
}
