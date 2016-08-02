package au.gov.dto.dibp.appointments.qflowintegration;

public class MaintenanceException extends RuntimeException {
    public MaintenanceException(final String message) {
        super(message);
    }

    public MaintenanceException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
