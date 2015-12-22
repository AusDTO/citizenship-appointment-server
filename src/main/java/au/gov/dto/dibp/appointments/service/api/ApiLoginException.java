package au.gov.dto.dibp.appointments.service.api;

public class ApiLoginException extends RuntimeException {
    public ApiLoginException(String message) {
        super(message);
    }

    public ApiLoginException(String message, Throwable cause) {
        super(message, cause);
    }
}
