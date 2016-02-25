package au.gov.dto.dibp.appointments.booking.exceptions;

public class NoCalendarExistsException extends RuntimeException{
    public NoCalendarExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
