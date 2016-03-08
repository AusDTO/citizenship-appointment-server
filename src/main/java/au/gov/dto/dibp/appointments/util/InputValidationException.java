package au.gov.dto.dibp.appointments.util;

public class InputValidationException extends RuntimeException {
    public InputValidationException(String message) {
        super(message);
    }

    public InputValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
