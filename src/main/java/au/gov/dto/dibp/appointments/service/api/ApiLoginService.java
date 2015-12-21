package au.gov.dto.dibp.appointments.service.api;

import au.gov.dto.dibp.appointments.util.ResponseParser;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import com.squareup.okhttp.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class ApiLoginService {
    private static final String SIGN_IN_TEMPLATE_PATH = "FormsSignIn.mustache";
    private static final String API_SESSION_ID = "//FormsSignInResponse/FormsSignInResult";

    private String serviceAddressUser;
    private String userForceLogin;

    private final List<ApiUser> apiUsers;
    private final ResourceLoader resourceLoader;
    private final HttpClientHandler httpClient;

    @Autowired
    public ApiLoginService(ResourceLoader resourceLoader, ApiUserService apiUserService, HttpClientHandler httpClient, @Value("${SERVICE.ADDRESS.USER}") String serviceAddressUser, @Value("${USER.FORCE.LOGIN}") String userForceLogin) {
        this.resourceLoader = resourceLoader;
        this.apiUsers = Collections.unmodifiableList(apiUserService.initializeApiUsers());
        this.httpClient = httpClient;
        this.serviceAddressUser = serviceAddressUser;
        this.userForceLogin = userForceLogin;
    }

    public String login() throws IOException, XPathExpressionException, SAXException, ParserConfigurationException {
        Resource resource = resourceLoader.getResource("classpath:request_templates/" + SIGN_IN_TEMPLATE_PATH);
        InputStream inputStream = resource.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        Template tmpl = Mustache.compiler().compile(inputStreamReader);

        Map<String, String> messageParams = new HashMap<>();
        messageParams.put("username", apiUsers.get(0).getUsername());
        messageParams.put("password", apiUsers.get(0).getPassword());
        messageParams.put("forceSignIn", userForceLogin);
        messageParams.put("messageUUID", UUID.randomUUID().toString());
        messageParams.put("serviceAddress", serviceAddressUser);
        String messageBody = tmpl.execute(messageParams);

        Response r = httpClient.post(serviceAddressUser, messageBody);
        return new ResponseParser(r.body().byteStream()).getStringAttribute(API_SESSION_ID);
    }

}
