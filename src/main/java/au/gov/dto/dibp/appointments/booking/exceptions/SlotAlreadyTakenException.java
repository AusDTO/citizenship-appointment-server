package au.gov.dto.dibp.appointments.booking.exceptions;

public class SlotAlreadyTakenException extends RuntimeException {
    public SlotAlreadyTakenException(String message, Throwable cause) {
        super(message, cause);
    }
}
