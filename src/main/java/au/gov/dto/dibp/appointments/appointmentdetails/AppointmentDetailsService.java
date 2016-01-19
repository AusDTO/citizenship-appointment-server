package au.gov.dto.dibp.appointments.appointmentdetails;

import au.gov.dto.dibp.appointments.client.Client;
import au.gov.dto.dibp.appointments.organisation.UnitDetailsService;
import au.gov.dto.dibp.appointments.qflowintegration.ApiCallsSenderService;
import au.gov.dto.dibp.appointments.util.NodeParser;
import au.gov.dto.dibp.appointments.util.ResponseWrapper;
import au.gov.dto.dibp.appointments.util.TemplateLoader;
import com.samskivert.mustache.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class AppointmentDetailsService {

    private final ApiCallsSenderService senderService;
    private final UnitDetailsService unitDetailsService;

    private final String serviceAddressCustomer;
    private final Template getExpectedAppointmentsTemplate;

    @Autowired
    public AppointmentDetailsService(ApiCallsSenderService senderService,
                                     UnitDetailsService unitDetailsService,
                                     TemplateLoader templateLoader,
                                     @Value("${SERVICE.ADDRESS.CUSTOMER}") String serviceAddressCustomer) {
        this.senderService = senderService;
        this.unitDetailsService = unitDetailsService;
        this.serviceAddressCustomer = serviceAddressCustomer;

        this.getExpectedAppointmentsTemplate = templateLoader.loadRequestTemplate(GetExpectedAppointments.REQUEST_TEMPLATE_PATH);
    }

    public AppointmentDetails getExpectedAppointmentForClientForNextYear(Client client) {
        LocalDateTime today =  unitDetailsService.getUnitCurrentLocalTime(client.getUnitId());
        LocalDateTime endDate = today.plusYears(1L);

        return this.getExpectedAppointmentForClient(client, today, endDate);
    }

    public AppointmentDetails getExpectedAppointmentForClient(Client client, LocalDateTime startDate, LocalDateTime endDate){
        Map<String, String> data = new HashMap<>();
        data.put("customerId", client.getCustomerId());
        data.put("startDate", startDate.toString());
        data.put("endDate", endDate.toString());

        ResponseWrapper response = senderService.sendRequest(getExpectedAppointmentsTemplate, data, serviceAddressCustomer);
        return parseGetExpectedAppointmentsResponse(response, client);
    }

    private AppointmentDetails parseGetExpectedAppointmentsResponse(ResponseWrapper responseWrapper, Client client){

        NodeList appointmentNodes = responseWrapper.getNodeListAttribute(GetExpectedAppointments.APPOINTMENTS);
        if(appointmentNodes.getLength() < 1){
            return null;
        }
        return getAppointmentDetails(appointmentNodes.item(0), client);
    }

    private AppointmentDetails getAppointmentDetails(Node appointmentNode, Client client) {
        NodeParser nodeParser = new NodeParser(appointmentNode);

        String appointmentDateString = nodeParser.getStringAttribute(GetExpectedAppointments.APPOINTMENT_DATE);
        LocalDateTime appointmentDate = LocalDateTime.parse(appointmentDateString);

        int appointmentDuration = nodeParser.getIntegerAttribute(GetExpectedAppointments.APPOINTMENT_DURATION);
        String processId = nodeParser.getStringAttribute(GetExpectedAppointments.PROCESS_ID);
        String serviceId = nodeParser.getStringAttribute(GetExpectedAppointments.SERVICE_ID);

        String customerId = nodeParser.getStringAttribute(GetExpectedAppointments.CUSTOMER_ID);
        if( !client.getCustomerId().equals(customerId) ){
            throw new RuntimeException("The found appointment does not belong to the current user.");
        }

        String unitName = nodeParser.getStringAttribute(GetExpectedAppointments.UNIT_NAME);
        String unitAddress = unitDetailsService.getUnitAddressByServiceId(serviceId);


        return new AppointmentDetails(appointmentDate, appointmentDuration, processId, serviceId, customerId, unitName, unitAddress);
    }

    private class GetExpectedAppointments {
        static final String REQUEST_TEMPLATE_PATH = "GetExpectedAppointments.mustache";

        static final String APPOINTMENTS = "//GetExpectedAppointmentsResponse/GetExpectedAppointmentsResult/CustomerGetExpectedAppointmentsResults";
        static final String APPOINTMENT_DATE = "AppointmentDate";
        static final String APPOINTMENT_DURATION = "AppointmentDuration";
        static final String PROCESS_ID = "ProcessId";
        static final String SERVICE_ID = "ServiceId";
        static final String CUSTOMER_ID = "CustomerId";
        static final String UNIT_NAME = "UnitName";
    }
}
