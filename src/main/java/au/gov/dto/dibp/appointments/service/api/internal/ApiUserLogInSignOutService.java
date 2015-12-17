package au.gov.dto.dibp.appointments.service.api.internal;

import au.gov.dto.dibp.appointments.util.ResponseParser;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import com.squareup.okhttp.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class ApiUserLogInSignOutService {

    static final String SIGN_IN_TEMPLATE_PATH = "FormsSignIn.mustache";
    static final String SIGN_OUT_TEMPLATE_PATH = "SignOut.mustache";
    private static final String API_SESSION_ID = "//FormsSignInResponse/FormsSignInResult";

    @Autowired
    HttpClientHandler httpClient;

    private final String SERVICE_ADDRESS_USER;
    private final String FORCE_LOGIN;
    private List<ApiUser> apiUsers;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ResourceLoader resourceLoader;

    public ApiUserLogInSignOutService(){
        initializeApiUsers();
        SERVICE_ADDRESS_USER = getEnvironmentVariable("SERVICE_ADDRESS_USER");
        FORCE_LOGIN = getEnvironmentVariable("USER_FORCE_LOGIN");
    }

    public String getApiSessionId() throws ParserConfigurationException, SAXException, XPathExpressionException, IOException {
        Resource resource = resourceLoader.getResource("classpath:request_templates/" + SIGN_IN_TEMPLATE_PATH);
        InputStream inputStream = resource.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        Template tmpl = Mustache.compiler().compile(inputStreamReader);

        Map<String, String> messageParams = new HashMap<>();
        messageParams.put("username", apiUsers.get(0).username);
        messageParams.put("password", apiUsers.get(0).password);
        messageParams.put("forceSignIn", FORCE_LOGIN);
        messageParams.put("messageUUID", UUID.randomUUID().toString());
        messageParams.put("serviceAddress", SERVICE_ADDRESS_USER);
        String messageBody = tmpl.execute(messageParams);

        Response r = httpClient.post(SERVICE_ADDRESS_USER, messageBody);
        return new ResponseParser(r.body().byteStream()).getStringAttribute(API_SESSION_ID);
    }

    public void releaseApiSessionId(String apiSessionId) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {
        Resource resource = resourceLoader.getResource("classpath:request_templates/" + SIGN_OUT_TEMPLATE_PATH);
        InputStream inputStream = resource.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        Template tmpl = Mustache.compiler().compile(inputStreamReader);

        Map<String, String> messageParams = new HashMap<>();
        messageParams.put("apiSessionId", apiSessionId);
        messageParams.put("messageUUID", UUID.randomUUID().toString());
        messageParams.put("serviceAddress", SERVICE_ADDRESS_USER);
        String messageBody = tmpl.execute(messageParams);

        httpClient.post(SERVICE_ADDRESS_USER, messageBody);
    }

    private void initializeApiUsers(){
        int userCount = Integer.parseInt(getEnvironmentVariable("USER_COUNT"));
        apiUsers = new ArrayList<>();

        for(int i = 1; i<=userCount; i++){
            String username = getEnvironmentVariable("USER_USERNAME_"+i);
            String password = getEnvironmentVariable("USER_PASSWORD_"+i);
            apiUsers.add(new ApiUser(username, password));
        }
    }

    private class ApiUser {
        ApiUser(String username, String password){
            this.username = username;
            this.password = password;
        }
        private String username;
        private String password;
    }

    String getEnvironmentVariable(String name){
        String value =  System.getenv(name);
        if(StringUtils.isEmpty(value)){
            log.error("No value set for the environment variable "+ name);
        }
        return value;
    }
}
