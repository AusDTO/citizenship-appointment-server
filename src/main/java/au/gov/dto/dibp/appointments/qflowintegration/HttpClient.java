package au.gov.dto.dibp.appointments.qflowintegration;

import au.gov.dto.dibp.appointments.util.ResponseWrapper;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
class HttpClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClient.class);
    private static final MediaType SOAP_MEDIA_TYPE = MediaType.parse("application/soap+xml; charset=utf-8");

    private final OkHttpClient httpClient;

    public HttpClient() {
        this.httpClient = new OkHttpClient.Builder().build();
    }

    public ResponseWrapper post(String url, String messageBody, String messageId) {
        RequestBody body = RequestBody.create(SOAP_MEDIA_TYPE, messageBody);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        LOGGER.debug("API Request to be sent for messageId=[{}]: " + messageBody.replaceAll("\\n", ""), messageId);
        long startTime = System.currentTimeMillis();

        try {
            Response response = httpClient.newCall(request).execute();
            String responseMessage = response.body().string();
            long timeTakenMillis = System.currentTimeMillis() - startTime;
            LOGGER.debug("API Response received time=[{}ms] messageId=[{}]: " + responseMessage, timeTakenMillis, messageId);
            return new ResponseWrapper(response.code(), responseMessage);
        } catch (IOException e) {
            throw new RuntimeException("Error sending API request messageId=[" + messageId + "]", e);
        }
    }

}
