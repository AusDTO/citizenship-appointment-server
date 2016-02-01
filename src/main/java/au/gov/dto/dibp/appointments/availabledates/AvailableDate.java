package au.gov.dto.dibp.appointments.availabledates;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

class AvailableDate {

    @JsonProperty("calendar_id")
    private final String id;

    @JsonIgnore
    private final String calendarDate;

    @JsonProperty("available_times_count")
    private int availableTimesCount;

    @JsonProperty("times")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> availableTimes;

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

    public List<String> getAvailableTimes() {
        return availableTimes != null ? new ArrayList<>(availableTimes) : null;
    }

    public void setAvailableTimes(List<String> availableTimes) {
        this.availableTimes = availableTimes != null ? new ArrayList<>(availableTimes) : new ArrayList<>();
        this.availableTimesCount = this.availableTimes.size();
    }
}
