package au.gov.dto.dibp.appointments.availabledates;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

class AvailableDate {

    @JsonProperty("calendar_id")
    private final String id;

    @JsonIgnore
    private final String calendarDate;

    @JsonProperty("available_times_count")
    private final int availableTimesCount;

    public AvailableDate(String id, String calendarDate, int availableTimesCount) {
        this.id = id;
        this.calendarDate = calendarDate;
        this.availableTimesCount = availableTimesCount;
    }

    public String getId() {
        return id;
    }

    public String getCalendarDate() {
        return calendarDate;
    }

    public int getAvailableTimesCount() {
        return availableTimesCount;
    }
}
