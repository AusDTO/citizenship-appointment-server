package au.gov.dto.dibp.appointments.qflowintegration;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
class ApiUserService {
    private static final Logger LOG = LoggerFactory.getLogger(ApiUserService.class);

    private final List<ApiUser> apiUsers = new ArrayList<>();

    public ApiUserService() {
        String username;
        String password;
        String userId;
        for (int i = 1;
             StringUtils.isNotBlank(username = System.getenv("USER_USERNAME_" + i))
                     && StringUtils.isNotBlank(password = System.getenv("USER_PASSWORD_" + i))
                     && StringUtils.isNotBlank(userId = System.getenv("USER_ID_" + i));
             i++) {
            apiUsers.add(new ApiUser(username, password, userId));
            LOG.info("Configuring API user number {} with username=[{}] and userid=[{}]", i, username, userId);
        }
    }

    public ApiUserService(ApiUser... apiUsers) {
        Collections.addAll(this.apiUsers, apiUsers);
    }

    public List<ApiUser> initializeApiUsers() {
        return new ArrayList<>(apiUsers);
    }
}
