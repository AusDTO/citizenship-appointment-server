package au.gov.dto.dibp.appointments.qflowintegration;

import au.gov.dto.dibp.appointments.util.ResponseWrapper;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
class ApiLoginService {
    private static final String SIGN_IN_TEMPLATE_PATH = "FormsSignIn.mustache";
    private static final String API_SESSION_ID = "//FormsSignInResponse/FormsSignInResult";

    static final int MAX_ATTEMPTS = 7;

    private String serviceAddressUser;
    private String userForceLogin;

    private final List<ApiUser> apiUsers;
    private final ResourceLoader resourceLoader;
    private final HttpClient httpClient;

    @Autowired
    public ApiLoginService(ResourceLoader resourceLoader, ApiUserService apiUserService, HttpClient httpClient, @Value("${SERVICE.ADDRESS.USER}") String serviceAddressUser, @Value("${USER.FORCE.LOGIN}") String userForceLogin) {
        this.resourceLoader = resourceLoader;
        this.apiUsers = Collections.unmodifiableList(apiUserService.initializeApiUsers());
        this.httpClient = httpClient;
        this.serviceAddressUser = serviceAddressUser;
        this.userForceLogin = userForceLogin;
    }

    public String login() {
        Resource resource = resourceLoader.getResource("classpath:request_templates/" + SIGN_IN_TEMPLATE_PATH);
        InputStream inputStream = null;
        try {
            inputStream = resource.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException("Problem reading request template: " + SIGN_IN_TEMPLATE_PATH, e);
        }
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        Template tmpl = Mustache.compiler().compile(inputStreamReader);

        for (int attempts = 0 ; attempts < MAX_ATTEMPTS; attempts++) {
            ResponseWrapper response = sendLoginRequest(tmpl);
            if (response!=null && response.getCode() == 200) {
                return response.getStringAttribute(API_SESSION_ID);
            }
        }
        throw new ApiLoginException("Failed to authenticate to Q-Flow API. Exceeded max FormsSignIn attempts: " + MAX_ATTEMPTS);
    }

    private ResponseWrapper sendLoginRequest(Template tmpl) {
        Map<String, String> messageParams = new HashMap<>();
        int index = new Random().nextInt(apiUsers.size());
        messageParams.put("username", apiUsers.get(index).getUsername());
        messageParams.put("password", apiUsers.get(index).getPassword());
        messageParams.put("forceSignIn", userForceLogin);
        messageParams.put("messageUUID", UUID.randomUUID().toString());
        messageParams.put("serviceAddress", serviceAddressUser);
        String messageBody = tmpl.execute(messageParams);

        try{
            return httpClient.post(serviceAddressUser, messageBody);
        }catch(RuntimeException e){
            //do nothing, retries
            return null;
        }
    }

}
