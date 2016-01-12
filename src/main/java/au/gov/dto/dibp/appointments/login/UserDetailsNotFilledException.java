package au.gov.dto.dibp.appointments.login;

public class UserDetailsNotFilledException extends RuntimeException {
    public UserDetailsNotFilledException(String message) {
        super(message);
    }

    public UserDetailsNotFilledException(String message, Throwable cause) {
        super(message, cause);
    }
}
