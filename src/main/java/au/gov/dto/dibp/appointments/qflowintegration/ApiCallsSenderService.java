package au.gov.dto.dibp.appointments.qflowintegration;

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

public interface ApiCallsSenderService {
    ResponseWrapper sendRequest(String requestTemplatePath, Map<String, String> messageParams, String serviceAddress);
}

@Service
class DefaultApiCallsSenderService implements ApiCallsSenderService {

    private final ApiSessionService apiSessionService;
    private final HttpClient httpClient;
    private final Mustache.Compiler mustacheCompiler;
    private final ResourceLoader resourceLoader;

    @Autowired
    public DefaultApiCallsSenderService(ApiSessionService apiSessionService, HttpClient httpClient, Mustache.Compiler mustacheCompiler, ResourceLoader resourceLoader) {
        this.apiSessionService = apiSessionService;
        this.httpClient = httpClient;
        this.mustacheCompiler = mustacheCompiler;
        this.resourceLoader = resourceLoader;
    }

    @Override
    public ResponseWrapper sendRequest(String requestTemplatePath, Map<String, String> messageParams, String serviceAddress) {
        Resource resource = resourceLoader.getResource("classpath:request_templates/" + requestTemplatePath);
        InputStream inputStream;
        try {
            inputStream = resource.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException("Problem reading request template: " + requestTemplatePath, e);
        }
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        Template tmpl = mustacheCompiler.compile(inputStreamReader);

        try (ApiSessionService.ApiSession apiSession = apiSessionService.createSession()) {
            messageParams.put("apiSessionId", apiSession.getApiSessionId());
            messageParams.put("serviceAddress", serviceAddress);
            messageParams.put("messageUUID", UUID.randomUUID().toString());
            String requestBody = tmpl.execute(messageParams);
            ResponseWrapper response = httpClient.post(serviceAddress, requestBody);
            if (response.getCode() != 200){
                throw new RuntimeException("Invalid server response with code "+ response.getCode() + ": " + response.getMessage());
            }
            return response;
        }
    }
}

