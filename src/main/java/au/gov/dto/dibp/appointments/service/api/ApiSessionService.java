package au.gov.dto.dibp.appointments.service.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;

@Service
public class ApiSessionService {

    private final ApiLoginService apiLoginService;
    private final ApiLogoutService apiLogoutService;

    @Autowired
    public ApiSessionService(ApiLoginService apiLoginService, ApiLogoutService apiLogoutService){
        this.apiLoginService = apiLoginService;
        this.apiLogoutService = apiLogoutService;
    }

    public ApiSession createSession() {
        try {
            return new ApiSession(apiLoginService.login());
        } catch (ParserConfigurationException|SAXException|XPathExpressionException|IOException e) {
            throw new RuntimeException("Error on login to Q-Flow API", e);
        }
    }

    public class ApiSession implements AutoCloseable {
        private final String apiSessionId;

        public ApiSession(String apiSessionId) {
            this.apiSessionId = apiSessionId;
        }

        @Override
        public void close() {
            try {
                apiLogoutService.logout(apiSessionId);
            } catch (ParserConfigurationException|SAXException|XPathExpressionException|IOException e) {
                throw new RuntimeException("Error on logout of Q-Flow API", e);
            }
        }

        public String getApiSessionId() {
            return apiSessionId;
        }
    }
}
