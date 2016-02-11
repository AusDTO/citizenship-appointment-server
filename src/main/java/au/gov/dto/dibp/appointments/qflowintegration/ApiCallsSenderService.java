package au.gov.dto.dibp.appointments.qflowintegration;

import au.gov.dto.dibp.appointments.util.ResponseWrapper;
import com.samskivert.mustache.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

public interface ApiCallsSenderService {
    ResponseWrapper sendRequest(Template template, Map<String, String> messageParams, String serviceAddress);
}

@Service
class DefaultApiCallsSenderService implements ApiCallsSenderService {

    private final ApiSessionService apiSessionService;
    private final HttpClient httpClient;

    @Autowired
    public DefaultApiCallsSenderService(ApiSessionService apiSessionService, HttpClient httpClient) {
        this.apiSessionService = apiSessionService;
        this.httpClient = httpClient;
    }

    @Override
    public ResponseWrapper sendRequest(Template template, Map<String, String> messageParams, String serviceAddress) {
        ApiSession apiSession = apiSessionService.createSession();

        try {
            messageParams.put("apiSessionId", apiSession.getApiSessionId());
            messageParams.put("currentUserId", apiSession.getUserId());
            messageParams.put("serviceAddress", serviceAddress);
            String messageId = UUID.randomUUID().toString();
            messageParams.put("messageUUID", messageId);
            String requestBody = template.execute(messageParams);
            ResponseWrapper response = httpClient.post(serviceAddress, requestBody, messageId);
            if (response.getCode() != 200){
                throw new RuntimeException("Invalid server response with statusCode=[" + response.getCode() + "] for messageId=[" + messageId + "]: " + response.getMessage());
            }
            return response;
        }finally {
            apiSessionService.closeSession(apiSession.getApiSessionId());
        }
    }
}


