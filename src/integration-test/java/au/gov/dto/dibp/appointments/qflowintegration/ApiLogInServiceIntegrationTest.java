package au.gov.dto.dibp.appointments.qflowintegration;

import okhttp3.internal.tls.OkHostnameVerifier;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.DefaultResourceLoader;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.fail;

public class ApiLogInServiceIntegrationTest {

    private final HttpClient httpClient = new HttpClient(defaultSocketFactory(), OkHostnameVerifier.INSTANCE);

    private MockWebServer mockWebServer;

    @Before
    public void setup() {
        mockWebServer = new MockWebServer();
    }

    @After
    public void teardown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    public void testSuccessfulApiLogin() throws Exception {
        Dispatcher dispatcher = new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
                try {
                    String requestBody = request.getBody().readUtf8();
                    if (requestBody.contains("success_user")) {
                        String responseBody = readFile("src/integration-test/resources/FormsSignInSuccessResponse.xml");
                        return new MockResponse().setResponseCode(200).setBody(responseBody);
                    }
                    if (requestBody.contains("wrong_credentials_user")) {
                        String responseBody = readFile("src/integration-test/resources/FormsSignInWrongCredentialsResponse.xml");
                        return new MockResponse().setResponseCode(500).setBody(responseBody);
                    }
                    if (requestBody.contains("taken_user")) {
                        String responseBody = readFile("src/integration-test/resources/FormsSignInTakenUserResponse.xml");
                        return new MockResponse().setResponseCode(500).setBody(responseBody);
                    }

                    throw new RuntimeException("Unexpected request: " + requestBody);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        mockWebServer.setDispatcher(dispatcher);

        ApiLoginService apiLoginService = new ApiLoginService(new DefaultResourceLoader(), new ApiUserService(new ApiUser("success_user", "any_password", "1")), httpClient, "http://localhost:"+this.mockWebServer.getPort(), "false");
        ApiSession apiSession = apiLoginService.login();
        assertThat(apiSession.getApiSessionId(), not(isEmptyOrNullString()));
        assertThat(apiSession.getUserId(), not(isEmptyOrNullString()));
    }

    @Test
    public void successfulApiLoginOnLastRetry() throws Exception {
        String takenUserResponse = readFile("src/integration-test/resources/FormsSignInTakenUserResponse.xml");
        String successResponse = readFile("src/integration-test/resources/FormsSignInSuccessResponse.xml");
        for (int i = 0; i < ApiLoginService.MAX_ATTEMPTS - 1; i++) {
            this.mockWebServer.enqueue(new MockResponse().setResponseCode(500).setBody(takenUserResponse));
        }
        this.mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(successResponse));

        ApiLoginService apiLoginService = new ApiLoginService(new DefaultResourceLoader(), new ApiUserService(new ApiUser("success_user", "any_password", "1")), httpClient, "http://localhost:"+this.mockWebServer.getPort(), "false");
        ApiSession apiSession = apiLoginService.login();

        assertThat(apiSession.getApiSessionId(), not(isEmptyOrNullString()));
        assertThat(apiSession.getUserId(), not(isEmptyOrNullString()));
    }

    @Test
    public void failApiLoginAfterFailedRetries() throws Exception {
        String takenUserResponse = readFile("src/integration-test/resources/FormsSignInTakenUserResponse.xml");
        for (int i = 0; i < ApiLoginService.MAX_ATTEMPTS; i++) {
            this.mockWebServer.enqueue(new MockResponse().setResponseCode(500).setBody(takenUserResponse));
        }

        ApiLoginService apiLoginService = new ApiLoginService(new DefaultResourceLoader(), new ApiUserService(new ApiUser("success_user", "any_password", "1")), httpClient, "http://localhost:"+this.mockWebServer.getPort(), "false");
        try {
            apiLoginService.login();
            fail("Expected Runtime exception");
        } catch (ApiLoginException expected) {
            // expected
        }
    }

    private String readFile(String filename) throws IOException {
        return IOUtils.toString(new InputStreamReader(new FileInputStream(filename), StandardCharsets.UTF_8));
    }

    private SSLSocketFactory defaultSocketFactory() {
        try {
            return SSLContext.getDefault().getSocketFactory();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Could not create default SSLContext", e);
        }
    }
}
