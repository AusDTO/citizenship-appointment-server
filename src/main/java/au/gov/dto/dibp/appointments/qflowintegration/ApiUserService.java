package au.gov.dto.dibp.appointments.qflowintegration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
class ApiUserService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final List<ApiUser> apiUsers;

    public ApiUserService() {
        int userCount = Integer.parseInt(getEnvironmentVariable("USER_COUNT"));
        apiUsers = new ArrayList<>();

        for(int i = 1; i<=userCount; i++){
            String username = getEnvironmentVariable("USER_USERNAME_"+i);
            String password = getEnvironmentVariable("USER_PASSWORD_"+i);
            apiUsers.add(new ApiUser(username, password));
        }
    }

    public ApiUserService(ApiUser... apiUsers) {
        this.apiUsers = Arrays.asList(apiUsers);
    }

    public List<ApiUser> initializeApiUsers(){
        return new ArrayList<>(apiUsers);
    }

    String getEnvironmentVariable(String name){
        String value =  System.getenv(name);
        if(StringUtils.isEmpty(value)){
            log.error("No value set for the environment variable "+ name);
        }
        return value;
    }
}
