package au.gov.dto.dibp.appointments.errors;

import au.gov.dto.dibp.appointments.util.InputValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalControllerExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalControllerExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public String handleException(Exception exception) {
        if (shouldLogAsWarning(exception)) {
            LOGGER.warn("Unhandled Exception", exception);
        } else {
            LOGGER.error("Unhandled Exception", exception);
        }
        return "redirect:/error";
    }

    boolean shouldLogAsWarning(Exception exception) {
        return exception instanceof HttpRequestMethodNotSupportedException
                || exception instanceof InputValidationException;
    }
}
