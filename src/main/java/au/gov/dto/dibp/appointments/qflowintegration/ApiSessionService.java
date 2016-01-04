package au.gov.dto.dibp.appointments.qflowintegration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
class ApiSessionService {

    private final ApiLoginService apiLoginService;
    private final ApiLogoutService apiLogoutService;

    @Autowired
    public ApiSessionService(ApiLoginService apiLoginService, ApiLogoutService apiLogoutService){
        this.apiLoginService = apiLoginService;
        this.apiLogoutService = apiLogoutService;
    }

    public ApiSession createSession() {
        return new ApiSession(apiLoginService.login());
    }

    public class ApiSession implements AutoCloseable {
        private final String apiSessionId;

        public ApiSession(String apiSessionId) {
            this.apiSessionId = apiSessionId;
        }

        @Override
        public void close() {
            apiLogoutService.logout(apiSessionId);
        }

        public String getApiSessionId() {
            return apiSessionId;
        }
    }
}
