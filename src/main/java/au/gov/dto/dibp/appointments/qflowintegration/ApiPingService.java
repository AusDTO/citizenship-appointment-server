package au.gov.dto.dibp.appointments.qflowintegration;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

public interface ApiPingService {
    void sendRequest();
}

@Service
class DefaultApiPingService implements ApiPingService {

    private final ApiSessionService apiSessionService;

    @Autowired
    public DefaultApiPingService(ApiSessionService apiSessionService) {
        this.apiSessionService = apiSessionService;
    }

    @Override
    public void sendRequest() {
        ApiSession apiSession = null;
        try {
            apiSession = apiSessionService.createSession();
        } finally {
            if(apiSession != null) {
                apiSessionService.closeSession(apiSession.getApiSessionId());
            }
        }
    }
}
