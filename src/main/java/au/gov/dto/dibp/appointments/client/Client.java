package au.gov.dto.dibp.appointments.client;

import org.springframework.security.core.userdetails.User;

import java.util.Collections;

public class Client extends User {

    private final String customerId;
    private final boolean hasEmail;
    private String unitId;
    private String serviceId;
    private String appointmentTypeId;

    public Client(String clientId, String familyName, String customerId, boolean hasEmail,String unitId, String serviceId, String appointmentTypeId, boolean active) {
        super(clientId, familyName, active, true, true, true, Collections.emptyList());
        this.customerId = customerId;
        this.hasEmail = hasEmail;
        this.unitId = unitId;
        this.serviceId = serviceId;
        this.appointmentTypeId = appointmentTypeId;
    }

    public String getClientId() {
        return getUsername();
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getUnitId() {
        return unitId;
    }

    public boolean hasEmail() {
        return hasEmail;
    }

    public String getServiceId() {
        return serviceId;
    }
    
    public String getAppointmentTypeId() {
        return appointmentTypeId;
    }
}
