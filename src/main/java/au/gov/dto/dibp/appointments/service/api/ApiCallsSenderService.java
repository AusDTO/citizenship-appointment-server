package au.gov.dto.dibp.appointments.service.api;

import au.gov.dto.dibp.appointments.service.api.internal.ApiUserLogInSignOutService;
import au.gov.dto.dibp.appointments.service.api.internal.HttpClientHandler;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import com.squareup.okhttp.Response;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.Map;
import java.util.UUID;

@Service
public class ApiCallsSenderService {

    @Autowired
    private ApiUserLogInSignOutService apiUserService;

    @Autowired
    private HttpClientHandler httpClient;

    @Autowired
    private Mustache.Compiler mustacheCompiler;

    @Autowired
    ResourceLoader resourceLoader;

    public Response sendRequest(String requestTemplatePath, Map<String, String> messageParams, String serviceAddress) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {
        Resource resource = resourceLoader.getResource("classpath:request_templates/" + requestTemplatePath);
        InputStream inputStream = resource.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        Template tmpl = mustacheCompiler.compile(inputStreamReader);

        String apiSessionId = apiUserService.getApiSessionId();

        try {
            messageParams.put("apiSessionId", apiSessionId);
            messageParams.put("serviceAddress", serviceAddress);
            messageParams.put("messageUUID", UUID.randomUUID().toString());
            String messageBody = tmpl.execute(messageParams);
            System.out.println(messageBody);
            return httpClient.post(serviceAddress, messageBody);
        }
        finally {
            apiUserService.releaseApiSessionId(apiSessionId);
        }
    }
}
