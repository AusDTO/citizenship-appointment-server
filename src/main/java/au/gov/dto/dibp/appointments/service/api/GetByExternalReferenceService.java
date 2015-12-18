package au.gov.dto.dibp.appointments.service.api;

import au.gov.dto.dibp.appointments.model.Customer;
import au.gov.dto.dibp.appointments.util.ResponseParser;
import com.squareup.okhttp.Response;
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
public class GetByExternalReferenceService implements UserDetailsService {

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

    public Customer getCustomerByExternalReference(String clientId) throws ParserConfigurationException, SAXException, XPathExpressionException, IOException {
        Map<String, String> data = new HashMap<>();
        data.put("externalReference", clientId);

        Response response = senderService.sendRequest(REQUEST_TEMPLATE_PATH, data, SERVICE_ADDRESS_CUSTOMER);
        return parseGetCustomerByClientIdResponse(response);
    }

    private Customer parseGetCustomerByClientIdResponse(Response response) throws IOException, SAXException, ParserConfigurationException, XPathExpressionException {
        ResponseParser parser = new ResponseParser(response.body().byteStream());

        String clientId = parser.getStringAttribute(CUSTOMER_CLIENT_ID);
        String lastName = parser.getStringAttribute(CUSTOMER_LAST_NAME);
        boolean isActive = "true".equals(parser.getStringAttribute(CUSTOMER_ACTIVE));
        String id = parser.getStringAttribute(CUSTOMER_ID);
        boolean isWithEmail = StringUtil.isNotBlank(parser.getStringAttribute(CUSTOMER_EMAIL));

        return new Customer(clientId, lastName, id, isWithEmail, isActive);
    }

}
