package au.gov.dto.dibp.appointments.organisation;

public class ServiceDetails {

    private String unitId;
    private String serviceId;
    private String externalReference;

    public ServiceDetails(String unitId, String serviceId, String externalReference) {
        this.unitId = unitId;
        this.serviceId = serviceId;
        this.externalReference = externalReference;
    }

    public String getUnitId() {
        return unitId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public String getExternalReference() {
        return externalReference;
    }
}
