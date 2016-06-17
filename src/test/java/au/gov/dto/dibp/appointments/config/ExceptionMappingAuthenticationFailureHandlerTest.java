package au.gov.dto.dibp.appointments.config;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;

import static org.mockito.Mockito.mock;
import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Unit Test for the ExceptionMappingAuthenticationFailureHandler.
 *
 * @author Darian Bridge.
 */
public class ExceptionMappingAuthenticationFailureHandlerTest {

    /**
     * Test that onAuthenticationFailure will respond with a Url containing the clientId
     * in the case of BadCredentialsException occurring.
     *
     * @throws Exception Thrown if a problem occurs.
     */
    @Test
    public void onAuthenticationFailureWithBadCredentialsException() throws Exception {

        ExceptionMappingAuthenticationFailureHandler handler = new ExceptionMappingAuthenticationFailureHandler();

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        request.addParameter("username", "01919191919");

        handler.onAuthenticationFailure(request, response,
            mock(BadCredentialsException.class));

        assertThat(response.getRedirectedUrl(), endsWith("/login?error&id=01919191919"));
    }
}
