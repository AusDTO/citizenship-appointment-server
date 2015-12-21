package au.gov.dto.dibp.appointments.service.api;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
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
public class ApiUserLogInSignOutService {

    static final String SIGN_OUT_TEMPLATE_PATH = "SignOut.mustache";

    private final String SERVICE_ADDRESS_USER;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final HttpClientHandler httpClient;

    private final ResourceLoader resourceLoader;

    private final ApiLoginService apiLoginService;

    @Autowired
    public ApiUserLogInSignOutService(HttpClientHandler httpClient, ResourceLoader resourceLoader, ApiLoginService apiLoginService){
        this.httpClient = httpClient;
        this.resourceLoader = resourceLoader;
        this.apiLoginService = apiLoginService;
        SERVICE_ADDRESS_USER = getEnvironmentVariable("SERVICE_ADDRESS_USER");
    }

    public String getApiSessionId() throws ParserConfigurationException, SAXException, XPathExpressionException, IOException {
        return this.apiLoginService.login();
    }

    public void releaseApiSessionId(String apiSessionId) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {
        Resource resource = resourceLoader.getResource("classpath:request_templates/" + SIGN_OUT_TEMPLATE_PATH);
        InputStream inputStream = resource.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        Template tmpl = Mustache.compiler().compile(inputStreamReader);

        Map<String, String> messageParams = new HashMap<>();
        messageParams.put("apiSessionId", apiSessionId);
        messageParams.put("messageUUID", UUID.randomUUID().toString());
        messageParams.put("serviceAddress", SERVICE_ADDRESS_USER);
        String messageBody = tmpl.execute(messageParams);

        httpClient.post(SERVICE_ADDRESS_USER, messageBody);
    }

    String getEnvironmentVariable(String name){
        String value =  System.getenv(name);
        if(StringUtils.isEmpty(value)){
            log.error("No value set for the environment variable "+ name);
        }
        return value;
    }
}
