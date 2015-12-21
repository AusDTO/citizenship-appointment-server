package au.gov.dto.dibp.appointments.service.api;

import com.squareup.okhttp.mockwebserver.Dispatcher;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;

public class ApiLoginServiceIntegrationTest {

    private final DefaultResourceLoader defaultResourceLoader = new DefaultResourceLoader();
    private MockWebServer mockWebServer;

    @Before
    public void setup() {
        Dispatcher dispatcher = new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
                try {
                    String requestBody = request.getBody().readUtf8();
                    if (requestBody.contains("success_user")) {
                        String responseBody = IOUtils.toString(defaultResourceLoader.getResource("classpath:FormsSignInSuccessResponse.xml").getInputStream(), StandardCharsets.UTF_8);
                        return new MockResponse().setResponseCode(200).setBody(responseBody);
                    }
                    if (requestBody.contains("wrong_credentials_user")) {
                        String responseBody = IOUtils.toString(defaultResourceLoader.getResource("classpath:FormsSignInWrongCredentialsResponse.xml").getInputStream(), StandardCharsets.UTF_8);
                        return new MockResponse().setResponseCode(500).setBody(responseBody);
                    }
                    if (requestBody.contains("taken_user")) {
                        String responseBody = IOUtils.toString(defaultResourceLoader.getResource("classpath:FormsSignInTakenUserResponse.xml").getInputStream(), StandardCharsets.UTF_8);
                        return new MockResponse().setResponseCode(500).setBody(responseBody);
                    }

                    return null;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        mockWebServer = new MockWebServer();
        mockWebServer.setDispatcher(dispatcher);
    }

    @After
    public void teardown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    public void testSuccessfulApiLogin() throws SAXException, ParserConfigurationException, XPathExpressionException, IOException {
//        ApiLoginService apiLoginService = new ApiLoginService(new DefaultResourceLoader(), new ApiUserService(), new HttpClientHandler(), "","");
//        String apiSessionId = apiLoginService.login();
//        assertThat(apiSessionId, not(isEmptyOrNullString()));
    }
}
