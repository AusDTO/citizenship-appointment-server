package au.gov.dto.dibp.appointments.qflowintegration;

import au.gov.dto.dibp.appointments.util.ResponseWrapper;
import com.samskivert.mustache.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

public interface ApiCallsSenderService {
    ResponseWrapper sendRequest(Template template, Map<String, String> messageParams, String serviceAddress);
}

@Service
class DefaultApiCallsSenderService implements ApiCallsSenderService {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultApiCallsSenderService.class);
    static final int MAX_ATTEMPTS = 5;

    private final ApiSessionService apiSessionService;
    private final HttpClient httpClient;

    @Autowired
    public DefaultApiCallsSenderService(ApiSessionService apiSessionService, HttpClient httpClient) {
        this.apiSessionService = apiSessionService;
        this.httpClient = httpClient;
    }

    @Override
    public ResponseWrapper sendRequest(Template template, Map<String, String> messageParams, String serviceAddress) {
        ResponseWrapper response = null;
        for (int numberOfAttempts = 1; numberOfAttempts <= MAX_ATTEMPTS; numberOfAttempts++) {
            String messageId = UUID.randomUUID().toString();
            response = sendRequestInternal(template, messageParams, serviceAddress, messageId);
            if (response.getCode() == 200) {
                return response;
            }
            if (!response.isInvalidSessionId()) {
                throw new ApiResponseNotSuccessfulException("Invalid server response with statusCode=[" + response.getCode() + "] for messageId=[" + messageId + "]: " + response.getMessage(), response);
            }
            LOG.warn("Request failed due to InvalidSessionId on attempt {}, retrying", numberOfAttempts);
        }
        LOG.error("Request failed {} times with InvalidSessionId error, giving up", MAX_ATTEMPTS);
        return response;
    }

    private ResponseWrapper sendRequestInternal(Template template, Map<String, String> messageParams, String serviceAddress, String messageId) {
        ApiSession apiSession = apiSessionService.createSession();

        try {
            messageParams.put("apiSessionId", apiSession.getApiSessionId());
            messageParams.put("currentUserId", apiSession.getUserId());
            messageParams.put("serviceAddress", serviceAddress);
            messageParams.put("messageUUID", messageId);
            String requestBody = template.execute(messageParams);
            return httpClient.post(serviceAddress, requestBody, messageId);
        } finally {
            if (apiSession != null) {
                apiSessionService.closeSession(apiSession.getApiSessionId());
            }
        }
    }
}


