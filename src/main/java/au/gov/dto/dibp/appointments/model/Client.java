package au.gov.dto.dibp.appointments.model;

import org.springframework.security.core.userdetails.User;

import java.util.Collections;

public class Client extends User {

    private final String customerId;
    private final boolean hasEmail;
    private String serviceId;
    private String appointmentTypeId;

    public Client(String clientId, String familyName, String customerId, boolean hasEmail, boolean active) {
        super(clientId, familyName, active, true, true, true, Collections.emptyList());
        this.customerId = customerId;
        this.hasEmail = hasEmail;
    }

    public String getClientId() {
        return getUsername();
    }

    public String getCustomerId() {
        return customerId;
    }

    public boolean hasEmail() {
        return hasEmail;
    }

    public String getServiceId() {
        // FIXME(Marz)
        return "5";
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getAppointmentTypeId() {
        return appointmentTypeId;
    }

    public void setAppointmentTypeId(String appointmentTypeId) {
        this.appointmentTypeId = appointmentTypeId;
    }

}
