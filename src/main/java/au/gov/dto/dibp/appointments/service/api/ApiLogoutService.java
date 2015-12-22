package au.gov.dto.dibp.appointments.service.api;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class ApiLogoutService {
    private static final String SIGN_OUT_TEMPLATE_PATH = "SignOut.mustache";

    private final ResourceLoader resourceLoader;
    private final String serviceAddressUser;
    private final HttpClientHandler httpClient;

    @Autowired
    public ApiLogoutService(ResourceLoader resourceLoader, @Value("${SERVICE.ADDRESS.USER}") String serviceAddressUser, HttpClientHandler httpClient) {
        this.resourceLoader = resourceLoader;
        this.serviceAddressUser = serviceAddressUser;
        this.httpClient = httpClient;
    }

    public void logout(String apiSessionId) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {
        Resource resource = resourceLoader.getResource("classpath:request_templates/" + SIGN_OUT_TEMPLATE_PATH);
        InputStream inputStream = resource.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        Template tmpl = Mustache.compiler().compile(inputStreamReader);

        Map<String, String> messageParams = new HashMap<>();
        messageParams.put("apiSessionId", apiSessionId);
        messageParams.put("messageUUID", UUID.randomUUID().toString());
        messageParams.put("serviceAddress", serviceAddressUser);
        String messageBody = tmpl.execute(messageParams);

        httpClient.post(serviceAddressUser, messageBody);
    }
}
