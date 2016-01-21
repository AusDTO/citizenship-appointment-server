package au.gov.dto.dibp.appointments.qflowintegration;

import au.gov.dto.dibp.appointments.util.ResponseWrapper;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.security.GeneralSecurityException;

@Component
class HttpClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClient.class);
    private static final MediaType SOAP_MEDIA_TYPE = MediaType.parse("application/soap+xml; charset=utf-8");

    private final OkHttpClient httpClient;

    @Autowired
    public HttpClient(SSLSocketFactory sslSocketFactory) throws GeneralSecurityException, IOException {
        this.httpClient = new OkHttpClient();
        this.httpClient.setSslSocketFactory(sslSocketFactory);
        this.httpClient.setHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession sslSession) {
                return true;
            }
        });
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
