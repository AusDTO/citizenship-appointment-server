package au.gov.dto.dibp.appointments.organisation;

public class UnitDetails {
    private String id;
    private String address;
    private String timeZoneIANA;

    public UnitDetails(String id, String address, String timeZoneIANA) {
        this.id = id;
        this.address = address;
        this.timeZoneIANA = timeZoneIANA;
    }

    public String getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }

    public String getTimeZoneIANA() {
        return timeZoneIANA;
    }
}
