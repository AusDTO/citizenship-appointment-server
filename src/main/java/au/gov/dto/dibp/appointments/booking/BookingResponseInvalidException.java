package au.gov.dto.dibp.appointments.booking;

public class BookingResponseInvalidException extends RuntimeException {
    public BookingResponseInvalidException(String message) {
        super(message);
    }

    public BookingResponseInvalidException(String message, Throwable cause) {
        super(message, cause);
    }
}
