package au.gov.dto.dibp.appointments.booking.exceptions;

public class UserNotEligibleToBookException  extends RuntimeException {
    public UserNotEligibleToBookException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserNotEligibleToBookException(String message) {
        super(message);
    }
}
