package au.gov.dto.dibp.appointments.appointmentdetails;

import java.time.LocalDateTime;

public class AppointmentDetails {

    private LocalDateTime appointmentDate;
    private int appointmentDuration;
    private String serviceId;
    private String processId;
    private String customerId;
    private String unitName;
    private String unitAddress;

    public AppointmentDetails(LocalDateTime appointmentDate, int appointmentDuration, String processId, String serviceId, String customerId, String unitName, String unitAddress){
        this.appointmentDate = appointmentDate;
        this.appointmentDuration = appointmentDuration;
        this.processId = processId;
        this.serviceId = serviceId;
        this.customerId = customerId;
        this.unitName = unitName;
        this.unitAddress = unitAddress;
    }

    public LocalDateTime getAppointmentDate() {
        return appointmentDate;
    }

    public int getAppointmentDuration() {
        return appointmentDuration;
    }

    public String getServiceId() {
        return serviceId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getUnitName() {
        return unitName;
    }

    public String getUnitAddress() {
        return unitAddress;
    }

    public String getProcessId() {
        return processId;
    }
}
