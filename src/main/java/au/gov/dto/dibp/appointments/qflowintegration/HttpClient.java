package au.gov.dto.dibp.appointments.qflowintegration;

import au.gov.dto.dibp.appointments.util.ResponseWrapper;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;

@Component
class HttpClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClient.class);
    private static final MediaType SOAP_MEDIA_TYPE = MediaType.parse("application/soap+xml; charset=utf-8");

    private final OkHttpClient httpClient;

    @Autowired
    public HttpClient(SSLSocketFactory sslSocketFactory, HostnameVerifier hostnameVerifier) {
        this.httpClient = new OkHttpClient.Builder()
                .sslSocketFactory(sslSocketFactory)
                .hostnameVerifier(hostnameVerifier)
                .build();
    }

    public ResponseWrapper post(String url, String messageBody) {
        RequestBody body = RequestBody.create(SOAP_MEDIA_TYPE, messageBody);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        LOGGER.debug("Request to be sent: " + messageBody.replaceAll("\\n", ""));

        try {
            Response response = httpClient.newCall(request).execute();
            String responseMessage = response.body().string();
            LOGGER.debug("Response received: " + responseMessage);
            return new ResponseWrapper(response.code(), responseMessage);
        } catch (IOException e) {
            throw new RuntimeException("Error sending SOAP request", e);
        }
    }

}
