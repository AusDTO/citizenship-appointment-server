package au.gov.dto.dibp.appointments.client;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.security.core.userdetails.User;

import java.util.Collections;

public class Client extends User {

    private final String customerId;
    private final boolean hasEmail;
    private final boolean hasMobile;
    private final String unitId;
    private final String serviceId;
    private final String appointmentTypeId;

    @JsonCreator
    public Client(@JsonProperty("clientId") String clientId,
                  @JsonProperty("username") String familyName,
                  @JsonProperty("customerId") String customerId,
                  @JsonProperty("email") boolean hasEmail,
                  @JsonProperty("mobile") boolean hasMobile,
                  @JsonProperty("unitId") String unitId,
                  @JsonProperty("serviceId") String serviceId,
                  @JsonProperty("appointmentTypeId") String appointmentTypeId,
                  @JsonProperty("enabled") boolean active) {
        super(clientId, familyName, active, true, true, true, Collections.emptyList());
        this.customerId = customerId;
        this.hasEmail = hasEmail;
        this.hasMobile = hasMobile;
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

    public boolean isEmail() {
        return hasEmail;
    }

    public boolean isMobile() {
        return hasMobile;
    }

    public String getServiceId() {
        return serviceId;
    }

    public String getAppointmentTypeId() {
        return appointmentTypeId;
    }
}
