package au.gov.dto.dibp.appointments.service.api;

import au.gov.dto.dibp.appointments.service.api.internal.ApiUserLogInSignOutService;
import au.gov.dto.dibp.appointments.service.api.internal.HttpClientHandler;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import com.squareup.okhttp.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

@Service
public class ApiCallsSenderService {

    @Autowired
    private ApiUserLogInSignOutService apiUserService;

    @Autowired
    private HttpClientHandler httpClient;

    @Autowired
    private Mustache.Compiler mustacheCompiler;

    public Response sendRequest(String requestTemplatePath, Map<String, String> messageParams, String serviceAddress) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {
        Template tmpl = mustacheCompiler.compile(new FileReader(requestTemplatePath));
        String apiSessionId = apiUserService.getApiSessionId();

        try {
            messageParams.put("apiSessionId", apiSessionId);
            messageParams.put("serviceAddress", serviceAddress);
            messageParams.put("messageUUID", UUID.randomUUID().toString());
            String messageBody = tmpl.execute(messageParams);
            System.out.println(messageBody);
            return httpClient.post(serviceAddress, messageBody);
        }
        finally {
            apiUserService.releaseApiSessionId(apiSessionId);
        }
    }
}
