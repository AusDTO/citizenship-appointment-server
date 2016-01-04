package au.gov.dto.dibp.appointments.qflowintegration;

import au.gov.dto.dibp.appointments.util.ResponseWrapper;
import com.squareup.okhttp.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
class HttpClient {

    private final OkHttpClient httpClient = new OkHttpClient();
    private static final MediaType SOAP_MEDIA_TYPE = MediaType.parse("application/soap+xml; charset=utf-8");
    private static final Logger logger = LoggerFactory.getLogger(HttpClient.class);

    public ResponseWrapper post(String url, String messageBody) {
        RequestBody body = RequestBody.create(SOAP_MEDIA_TYPE, messageBody);
        Request request = new Request.Builder()
            .url(url)
            .post(body)
            .build();
        logger.debug("Request to be sent: " + messageBody);

        try {
            Response response = httpClient.newCall(request).execute();
            String responseMessage = response.body().string();
            logger.debug("Response received: " + responseMessage);

            if(!response.isSuccessful()){
                logger.error("Invalid server response with code "+ response.code() +"/n"+ responseMessage);
                throw new RuntimeException("Invalid server response with code "+ response.code());
            }

            return new ResponseWrapper(response.code(), responseMessage);
        } catch (IOException e) {
            logger.error("An IO exception has occured "+ e);
            throw new RuntimeException("Error sending SOAP request", e);
        }
    }
}
