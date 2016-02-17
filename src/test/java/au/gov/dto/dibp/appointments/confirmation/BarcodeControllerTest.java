package au.gov.dto.dibp.appointments.confirmation;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class BarcodeControllerTest {
    @Test
    public void testClientIdValidationAcceptsValidClientId() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        String validClientId = "11111111111";

        new BarcodeController().barcode417(validClientId, response);

        assertThat(response.getStatus(), equalTo(200));
    }

    @Test
    public void testClientIdValidationRejectsTooShortClientId() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        String tooShortClientId = "1111111111";

        try {
            new BarcodeController().barcode417(tooShortClientId, response);
            fail("Should not reach this statement");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testClientIdValidationRejectsTooLongClientId() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        String tooLongClientId = "111111111111";

        try {
            new BarcodeController().barcode417(tooLongClientId, response);
            fail("Should not reach this statement");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testClientIdValidationRejectsInvalidClientId() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        String invalidClientId = "1111111111a";

        try {
            new BarcodeController().barcode417(invalidClientId, response);
            fail("Should not reach this statement");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }
}
