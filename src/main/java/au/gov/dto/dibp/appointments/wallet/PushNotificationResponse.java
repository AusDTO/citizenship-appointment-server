package au.gov.dto.dibp.appointments.wallet;

class PushNotificationResponse {
    private final boolean accepted;
    private final String rejectionReason;
    private final boolean tokenInvalid;

    public PushNotificationResponse(boolean accepted, String rejectionReason, boolean tokenInvalid) {
        this.accepted = accepted;
        this.rejectionReason = rejectionReason;
        this.tokenInvalid = tokenInvalid;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public boolean isTokenInvalid() {
        return tokenInvalid;
    }
}
