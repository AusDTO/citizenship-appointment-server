package au.gov.dto.dibp.appointments.service.api.internal;

import com.squareup.okhttp.*;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;

@Component
public class HttpClientHandler {

    private OkHttpClient httpClient = new OkHttpClient();
    private static final MediaType SOAP_MEDIA_TYPE = MediaType.parse("application/soap+xml; charset=utf-8");

    public Response post(String url, String messageBody) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {
        RequestBody body = RequestBody.create(SOAP_MEDIA_TYPE, messageBody);
        Request request = new Request.Builder()
            .url(url)
            .post(body)
            .build();
        return httpClient.newCall(request).execute();
    }
}
