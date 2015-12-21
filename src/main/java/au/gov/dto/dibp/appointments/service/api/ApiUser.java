package au.gov.dto.dibp.appointments.service.api;

public class ApiUser {
    ApiUser(String username, String password) {
        this.username = username;
        this.password = password;
    }

    private final String username;
    private final String password;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
