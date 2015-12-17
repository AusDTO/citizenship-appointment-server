package au.gov.dto.dibp.appointments.service.api;

import au.gov.dto.dibp.appointments.model.Customer;
import au.gov.dto.dibp.appointments.util.ResponseParser;
import com.squareup.okhttp.Response;
import org.eclipse.jetty.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class GetByExternalReferenceService {

    @Autowired
    private ApiCallsSenderService senderService;

    @Value("${SERVICE.ADDRESS.CUSTOMER}")
    private String SERVICE_ADDRESS_CUSTOMER;

    private class GetByExtRef {
        static final String REQUEST_TEMPLATE_PATH = "src/main/resources/request_templates/GetByExtRef.mustache";

        static final String CUSTOMER_ID = "//GetByExtRefResponse/GetByExtRefResult/Customer/Id";
        static final String CUSTOMER_LAST_NAME = "//GetByExtRefResponse/GetByExtRefResult/Customer/LastName";
        static final String CUSTOMER_ACTIVE = "//GetByExtRefResponse/GetByExtRefResult/Customer/Active";
        static final String CUSTOMER_CLIENT_ID = "//GetByExtRefResponse/GetByExtRefResult/Customer/ExtRef";
        static final String CUSTOMER_EMAIL = "//GetByExtRefResponse/GetByExtRefResult/Customer/EMail";
    }

    public Customer getCustomerByExternalReference(String clientId) throws ParserConfigurationException, SAXException, XPathExpressionException, IOException {
        Map<String, String> data = new HashMap<>();
        data.put("externalReference", clientId);

        Response response = senderService.sendRequest(GetByExtRef.REQUEST_TEMPLATE_PATH, data, SERVICE_ADDRESS_CUSTOMER);
        return parseGetCustomerByClientIdResponse(response);
    }

    private Customer parseGetCustomerByClientIdResponse(Response response) throws IOException, SAXException, ParserConfigurationException, XPathExpressionException {
        ResponseParser parser = new ResponseParser(response.body().byteStream());

        String clientId = parser.getStringAttribute(GetByExtRef.CUSTOMER_CLIENT_ID);
        String lastName = parser.getStringAttribute(GetByExtRef.CUSTOMER_LAST_NAME);
        boolean isActive = "true".equals(parser.getStringAttribute(GetByExtRef.CUSTOMER_ACTIVE));
        String id = parser.getStringAttribute(GetByExtRef.CUSTOMER_ID);
        boolean isWithEmail = StringUtil.isNotBlank(parser.getStringAttribute(GetByExtRef.CUSTOMER_EMAIL));

        return new Customer(clientId, lastName, id, isWithEmail, isActive);
    }

}
