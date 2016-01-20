package au.gov.dto.dibp.appointments.qflowintegration;

class ApiSession {
    private final String apiSessionId;
    private final String userId;

    public ApiSession(String apiSessionId, String userId) {
        this.apiSessionId = apiSessionId;
        this.userId = userId;
    }

    public String getApiSessionId() {
        return apiSessionId;
    }

    public String getUserId() {
        return userId;
    }
}
