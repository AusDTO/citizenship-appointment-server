package au.gov.dto.dibp.appointments.errors;

import org.junit.Test;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GlobalControllerExceptionHandlerTest {
    @Test
    public void testHttpRequestMethodNotSupportedExceptionShouldLogAsWarn() throws Exception {
        HttpRequestMethodNotSupportedException exception = new HttpRequestMethodNotSupportedException("message");
        boolean shouldLogAsWarning = new GlobalControllerExceptionHandler().shouldLogAsWarning(exception);
        assertTrue(shouldLogAsWarning);
    }

    @Test
    public void testRuntimeExceptionShouldLogAsError() throws Exception {
        RuntimeException exception = new RuntimeException("message");
        boolean shouldLogAsWarning = new GlobalControllerExceptionHandler().shouldLogAsWarning(exception);
        assertFalse(shouldLogAsWarning);
    }

    @Test
    public void testExceptionShouldLogAsError() throws Exception {
        Exception exception = new Exception("message");
        boolean shouldLogAsWarning = new GlobalControllerExceptionHandler().shouldLogAsWarning(exception);
        assertFalse(shouldLogAsWarning);
    }
}
