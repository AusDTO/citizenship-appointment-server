package au.gov.dto.dibp.appointments.model;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collections;

public class Customer extends User {

    private final String customerClientId;

    private String id;
    private boolean withEmailAddress;
    private String serviceId;
    private String appointmentTypeId;

    public Customer(String username, String familyName, String id, boolean withEmail, boolean active) {
        super(username, familyName, active, true, true, true, Collections.singletonList(new SimpleGrantedAuthority("USER")));
        this.customerClientId = username;
        this.id = id;
        this.withEmailAddress = withEmail;
    }

    public String getCustomerClientId() {
        return customerClientId;
    }

    public String getId() {
        return id;
    }

    public boolean isWithEmailAddress() {
        return withEmailAddress;
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
