package au.gov.dto.dibp.appointments.availabledates;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

class AvailableDate {

    @JsonProperty("calendar_id")
    private final String id;

    @JsonIgnore
    private final String calendarDate;

    @JsonProperty("available_slots_count")
    private final int availableSlotsCount;

    public AvailableDate(String id, String calendarDate, int availableSlotsCount) {
        this.id = id;
        this.calendarDate = calendarDate;
        this.availableSlotsCount = availableSlotsCount;
    }

    public String getId() {
        return id;
    }

    public String getCalendarDate() {
        return calendarDate;
    }

    public int getAvailableSlotsCount() {
        return availableSlotsCount;
    }
}
