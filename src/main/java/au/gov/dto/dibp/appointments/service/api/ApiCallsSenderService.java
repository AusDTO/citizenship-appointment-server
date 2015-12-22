package au.gov.dto.dibp.appointments.service.api;

import au.gov.dto.dibp.appointments.util.ResponseWrapper;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

@Service
public class ApiCallsSenderService {

    @Autowired
    private ApiSessionService apiSessionService;

    @Autowired
    private HttpClient httpClient;

    @Autowired
    private Mustache.Compiler mustacheCompiler;

    @Autowired
    ResourceLoader resourceLoader;

    public ResponseWrapper sendRequest(String requestTemplatePath, Map<String, String> messageParams, String serviceAddress) throws IOException {
        Resource resource = resourceLoader.getResource("classpath:request_templates/" + requestTemplatePath);
        InputStream inputStream = resource.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        Template tmpl = mustacheCompiler.compile(inputStreamReader);

        try (ApiSessionService.ApiSession apiSession = apiSessionService.createSession()) {
            messageParams.put("apiSessionId", apiSession.getApiSessionId());
            messageParams.put("serviceAddress", serviceAddress);
            messageParams.put("messageUUID", UUID.randomUUID().toString());
            String messageBody = tmpl.execute(messageParams);
            System.out.println(messageBody);
            return httpClient.post(serviceAddress, messageBody);
        }
    }
}
