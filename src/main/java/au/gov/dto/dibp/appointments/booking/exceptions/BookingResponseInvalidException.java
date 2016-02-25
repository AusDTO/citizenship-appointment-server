package au.gov.dto.dibp.appointments.booking.exceptions;

public class BookingResponseInvalidException extends RuntimeException {
    public BookingResponseInvalidException(String message, Throwable cause) {
        super(message, cause);
    }
}
