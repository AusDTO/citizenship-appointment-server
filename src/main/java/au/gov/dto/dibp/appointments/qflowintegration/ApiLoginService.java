package au.gov.dto.dibp.appointments.qflowintegration;

import au.gov.dto.dibp.appointments.util.ResponseWrapper;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

@Service
class ApiLoginService {
    private static final String SIGN_IN_TEMPLATE_PATH = "FormsSignIn.mustache";
    private static final String API_SESSION_ID = "//FormsSignInResponse/FormsSignInResult";
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiLoginService.class);

    static final int MAX_ATTEMPTS = 10;

    private final String serviceAddressUser;
    private final List<ApiUser> apiUsers;
    private final ResourceLoader resourceLoader;
    private final HttpClient httpClient;

    @Autowired
    public ApiLoginService(ResourceLoader resourceLoader, ApiUserService apiUserService, HttpClient httpClient, @Value("${SERVICE.ADDRESS.USER}") String serviceAddressUser) {
        this.resourceLoader = resourceLoader;
        this.apiUsers = Collections.unmodifiableList(apiUserService.initializeApiUsers());
        this.httpClient = httpClient;
        this.serviceAddressUser = serviceAddressUser;
    }

    public ApiSession login() {
        Resource resource = resourceLoader.getResource("classpath:request_templates/" + SIGN_IN_TEMPLATE_PATH);
        InputStream inputStream = null;
        try {
            inputStream = resource.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException("Problem reading request template: " + SIGN_IN_TEMPLATE_PATH, e);
        }
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        Template tmpl = Mustache.compiler().compile(inputStreamReader);
        
        checkMaintenance();
        
        for (int attempts = 1 ; attempts <= MAX_ATTEMPTS; attempts++) {
            int index = new Random().nextInt(apiUsers.size());
            ResponseWrapper response = sendLoginRequest(tmpl, index);
            if (response!=null && response.getCode() == 200) {

                String apiSessionId = response.getStringAttribute(API_SESSION_ID);
                String userId = apiUsers.get(index).getUserId();
                return new ApiSession(apiSessionId, userId);
            }
            if (attempts >= MAX_ATTEMPTS*.8) {
                LOGGER.warn("Reached {} of {} max FormsSignIn attempts", attempts, MAX_ATTEMPTS);
            }
        }
        throw new ApiLoginException("Failed to authenticate to Q-Flow API. Exceeded max FormsSignIn attempts: " + MAX_ATTEMPTS);
    }
    
    private void checkMaintenance() {
        
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Australia/Sydney"));
        
        //ZonedDateTime start = LocalDateTime.of(2016, 8, 5, 5, 00).atZone(ZoneId.of("Australia/Sydney"));
        //ZonedDateTime end = LocalDateTime.of(2016, 8, 5, 9, 00).atZone(ZoneId.of("Australia/Sydney"));
        
        ZonedDateTime start = LocalDateTime.of(2016, 8, 2, 12, 00).atZone(ZoneId.of("Australia/Sydney"));
        ZonedDateTime end = LocalDateTime.of(2016, 8, 2, 14, 00).atZone(ZoneId.of("Australia/Sydney"));
        
        if (now.isAfter(start) && now.isBefore(end)) {
            throw new MaintenanceException("This service is temporarily unavailable due to a scheduled maintenance period.");
        }
    }

    private ResponseWrapper sendLoginRequest(Template tmpl, int index) {
        Map<String, String> messageParams = new HashMap<>();
        messageParams.put("username", apiUsers.get(index).getUsername());
        messageParams.put("password", apiUsers.get(index).getPassword());
        messageParams.put("forceSignIn", "false");
        String messageId = UUID.randomUUID().toString();
        messageParams.put("messageUUID", messageId);
        messageParams.put("serviceAddress", serviceAddressUser);
        messageParams.put("ipAddressUUID", UUID.randomUUID().toString());
        String messageBody = tmpl.execute(messageParams);

        return httpClient.post(serviceAddressUser, messageBody, messageId);
    }
}
