package au.gov.dto.dibp.appointments.service;

import au.gov.dto.dibp.appointments.model.Customer;
import au.gov.dto.dibp.appointments.service.api.GetByExternalReferenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;

@Service
public class CustomerDetailsService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    GetByExternalReferenceService getByExtRefService;

    public Customer getCustomerByClientId(String clientId) {
        try {
            return getByExtRefService.getCustomerByExternalReference(clientId);
        } catch (ParserConfigurationException | SAXException | IOException | XPathExpressionException e) {
            log.error(e.getLocalizedMessage());
        }
        return null;
    }
}
