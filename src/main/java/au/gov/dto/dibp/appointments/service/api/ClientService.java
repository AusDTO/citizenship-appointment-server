package au.gov.dto.dibp.appointments.service.api;

import au.gov.dto.dibp.appointments.model.Client;
import au.gov.dto.dibp.appointments.util.ResponseWrapper;
import org.eclipse.jetty.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class ClientService implements UserDetailsService {

    static final String REQUEST_TEMPLATE_PATH = "GetByExtRef.mustache";
    private static final String CUSTOMER_EMAIL = "//GetByExtRefResponse/GetByExtRefResult/Customer/EMail";
    private static final String CUSTOMER_CLIENT_ID = "//GetByExtRefResponse/GetByExtRefResult/Customer/ExtRef";
    private static final String CUSTOMER_ACTIVE = "//GetByExtRefResponse/GetByExtRefResult/Customer/Active";
    private static final String CUSTOMER_LAST_NAME = "//GetByExtRefResponse/GetByExtRefResult/Customer/LastName";
    private static final String CUSTOMER_ID = "//GetByExtRefResponse/GetByExtRefResult/Customer/Id";

    @Autowired
    private ApiCallsSenderService senderService;

    @Value("${SERVICE.ADDRESS.CUSTOMER}")
    private String SERVICE_ADDRESS_CUSTOMER;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails user = null;
        try {
            user = this.getCustomerByExternalReference(username);
        } catch (ParserConfigurationException|SAXException|XPathExpressionException|IOException e){
            throw new RuntimeException("Error when retrieving client with clientId=[" + username + "]", e);
        }
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        return user;
    }

    public Client getCustomerByExternalReference(String clientId) throws ParserConfigurationException, SAXException, XPathExpressionException, IOException {
        Map<String, String> data = new HashMap<>();
        data.put("externalReference", clientId);

        ResponseWrapper response = senderService.sendRequest(REQUEST_TEMPLATE_PATH, data, SERVICE_ADDRESS_CUSTOMER);
        return parseGetCustomerByClientIdResponse(response);
    }

    private Client parseGetCustomerByClientIdResponse(ResponseWrapper response) throws IOException, SAXException, ParserConfigurationException, XPathExpressionException {
        String clientId = response.getStringAttribute(CUSTOMER_CLIENT_ID);
        String lastName = response.getStringAttribute(CUSTOMER_LAST_NAME);
        boolean isActive = "true".equals(response.getStringAttribute(CUSTOMER_ACTIVE));
        String id = response.getStringAttribute(CUSTOMER_ID);
        boolean isWithEmail = StringUtil.isNotBlank(response.getStringAttribute(CUSTOMER_EMAIL));

        return new Client(clientId, lastName, id, isWithEmail, isActive);
    }

}
