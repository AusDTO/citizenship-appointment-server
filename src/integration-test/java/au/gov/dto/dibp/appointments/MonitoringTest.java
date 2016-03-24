package au.gov.dto.dibp.appointments;

import okhttp3.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

public class MonitoringTest {

    private String baseUrl;
    private OkHttpClient httpClient;
    private ApplicationForTests applicationForTests;

    @Before
    public void before() {
        applicationForTests = new ApplicationForTests();
        applicationForTests.runTestApplication();

        baseUrl = System.getenv("MONITOR_BASE_URL");
        String adminPass = System.getenv("SECURITY_ADMIN_PASSWORD");

        httpClient = new OkHttpClient.Builder()
        .authenticator((Route route, Response response) -> {
                String credentials = Credentials.basic("admin", adminPass);
                return response.request().newBuilder().header("Authorization", credentials).build();
            }
        )
        .build();
    }

    @Test
    public void test_systemMonitoringEndpoint_isAccessible() throws IOException {
        Request request = new Request.Builder()
                .url(baseUrl+"/monitoring/system")
                .get()
                .build();

        Response response = httpClient.newCall(request).execute();
        Assert.assertThat(response.code(), is(200));

        String responseBody = response.body().string();
        Assert.assertThat(responseBody, containsString("classes"));
        Assert.assertThat(responseBody, containsString("uptime"));
    }

    @Test
    public void test_backendMonitoringEndpoint_isAccessible() throws IOException {
        Request request = new Request.Builder()
                .url(baseUrl+"/monitoring/backend")
                .get()
                .build();

        Response response = httpClient.newCall(request).execute();
        Assert.assertThat(response.code(), is(200));
        Assert.assertThat(response.body().string(), is("Nah, yeah"));
    }

    @After
    public void cleanUp(){
        applicationForTests.stopApplication();
    }
}
