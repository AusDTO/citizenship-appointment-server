package au.gov.dto.dibp.appointments.service.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class ApiUserService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public List<ApiUser> initializeApiUsers(){
        int userCount = Integer.parseInt(getEnvironmentVariable("USER_COUNT"));
        List<ApiUser> apiUsers = new ArrayList<>();

        for(int i = 1; i<=userCount; i++){
            String username = getEnvironmentVariable("USER_USERNAME_"+i);
            String password = getEnvironmentVariable("USER_PASSWORD_"+i);
            apiUsers.add(new ApiUser(username, password));
        }
        return apiUsers;
    }

    String getEnvironmentVariable(String name){
        String value =  System.getenv(name);
        if(StringUtils.isEmpty(value)){
            log.error("No value set for the environment variable "+ name);
        }
        return value;
    }
}
