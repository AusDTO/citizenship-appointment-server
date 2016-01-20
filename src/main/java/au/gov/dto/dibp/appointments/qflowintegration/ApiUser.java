package au.gov.dto.dibp.appointments.qflowintegration;

class ApiUser {
    ApiUser(String username, String password, String userId) {
        this.username = username;
        this.password = password;
        this.userId = userId;
    }

    private final String username;
    private final String password;
    private final String userId;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getUserId() {
        return userId;
    }
}
