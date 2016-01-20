package au.gov.dto.dibp.appointments.qflowintegration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
class ApiSessionService {

    private final ApiLoginService apiLoginService;
    private final ApiLogoutService apiLogoutService;

    @Autowired
    public ApiSessionService(ApiLoginService apiLoginService, ApiLogoutService apiLogoutService) {
        this.apiLoginService = apiLoginService;
        this.apiLogoutService = apiLogoutService;
    }

    public ApiSession createSession() {
        return apiLoginService.login();
    }

    public void closeSession(String apiSessionId) {
        apiLogoutService.logout(apiSessionId);
    }
}
