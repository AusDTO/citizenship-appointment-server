package au.gov.dto.dibp.appointments.service.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;

@Service
public class ApiUserLogInSignOutService {

    private final ApiLoginService apiLoginService;
    private final ApiLogoutService apiLogoutService;

    @Autowired
    public ApiUserLogInSignOutService(ApiLoginService apiLoginService, ApiLogoutService apiLogoutService){
        this.apiLoginService = apiLoginService;
        this.apiLogoutService = apiLogoutService;
    }

    public String getApiSessionId() throws ParserConfigurationException, SAXException, XPathExpressionException, IOException {
        return this.apiLoginService.login();
    }

    public void releaseApiSessionId(String apiSessionId) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {
        this.apiLogoutService.logout(apiSessionId);
    }

    public ApiSession login() {
        try {
            return new ApiSession(getApiSessionId());
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
                releaseApiSessionId(apiSessionId);
            } catch (ParserConfigurationException|SAXException|XPathExpressionException|IOException e) {
                throw new RuntimeException("Error on logout of Q-Flow API", e);
            }
        }

        public String getApiSessionId() {
            return apiSessionId;
        }
    }
}
