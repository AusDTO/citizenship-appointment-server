package au.gov.dto.dibp.appointments.service.api.internal;

import au.gov.dto.dibp.appointments.util.ResponseParser;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import com.squareup.okhttp.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

@Service
public class ApiUserLogInSignOutService {

    @Autowired
    HttpClientHandler httpClient;

    private final String SERVICE_ADDRESS_USER;
    private final String FORCE_LOGIN;
    private List<ApiUser> apiUsers;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public ApiUserLogInSignOutService(){
        initializeApiUsers();
        SERVICE_ADDRESS_USER = getEnvironmentVariable("SERVICE_ADDRESS_USER");
        FORCE_LOGIN = getEnvironmentVariable("USER_FORCE_LOGIN");
    }

    private class FormsSignIn {
        public static final String REQUEST_TEMPLATE_PATH = "src/main/resources/templates/request/FormsSignIn.mustache";
        public static final String API_SESSION_ID = "//FormsSignInResponse/FormsSignInResult";
    }

    private class SignOut {
        public static final String REQUEST_TEMPLATE_PATH = "src/main/resources/templates/request/SignOut.mustache";
    }

    public String getApiSessionId() throws ParserConfigurationException, SAXException, XPathExpressionException, IOException {
        Template tmpl = Mustache.compiler().compile(new FileReader(FormsSignIn.REQUEST_TEMPLATE_PATH));
        Map<String, String> messageParams = new HashMap<>();
        messageParams.put("username", apiUsers.get(0).username);
        messageParams.put("password", apiUsers.get(0).password);
        messageParams.put("forceSignIn", FORCE_LOGIN);
        messageParams.put("messageUUID", UUID.randomUUID().toString());
        messageParams.put("serviceAddress", SERVICE_ADDRESS_USER);
        String messageBody = tmpl.execute(messageParams);

        Response r = httpClient.post(SERVICE_ADDRESS_USER, messageBody);
        return new ResponseParser(r.body().byteStream()).getStringAttribute(FormsSignIn.API_SESSION_ID);
    }

    public void releaseApiSessionId(String apiSessionId) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {
        Template tmpl = Mustache.compiler().compile(new FileReader(SignOut.REQUEST_TEMPLATE_PATH));
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
