package au.gov.dto.dibp.appointments.qflowintegration;

import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class HttpClientTest {
    @Test
    public void loggableMessageBodyShouldNotContainNewLines() throws Exception {
        String messageBody = "a\nb\nc";
        String loggableMessageBody = new HttpClient().loggableMessage(messageBody);
        assertThat(loggableMessageBody, equalTo("abc"));
    }

    @Test
    public void loggableMessageBodyShouldNotContainPasswords() throws Exception {
        String messageBody = "<userName>username</userName> <password>password</password>";
        String loggableMessageBody = new HttpClient().loggableMessage(messageBody);
        assertThat(loggableMessageBody, equalTo("<userName>username</userName> <password>MASKED</password>"));
    }
}
