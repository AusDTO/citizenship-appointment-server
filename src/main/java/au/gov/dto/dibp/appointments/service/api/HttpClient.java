package au.gov.dto.dibp.appointments.service.api;

import au.gov.dto.dibp.appointments.util.ResponseWrapper;
import com.squareup.okhttp.*;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class HttpClient {

    private final OkHttpClient httpClient = new OkHttpClient();
    private static final MediaType SOAP_MEDIA_TYPE = MediaType.parse("application/soap+xml; charset=utf-8");

    public ResponseWrapper post(String url, String messageBody) {
        RequestBody body = RequestBody.create(SOAP_MEDIA_TYPE, messageBody);
        Request request = new Request.Builder()
            .url(url)
            .post(body)
            .build();
        try {
            Response response = httpClient.newCall(request).execute();
            return new ResponseWrapper(response.code(), response.body().byteStream());
        } catch (IOException e) {
            throw new RuntimeException("Error sending SOAP request", e);
        }
    }
}
