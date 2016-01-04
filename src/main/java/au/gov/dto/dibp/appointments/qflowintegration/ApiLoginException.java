package au.gov.dto.dibp.appointments.qflowintegration;

class ApiLoginException extends RuntimeException {
    public ApiLoginException(String message) {
        super(message);
    }

    public ApiLoginException(String message, Throwable cause) {
        super(message, cause);
    }
}
