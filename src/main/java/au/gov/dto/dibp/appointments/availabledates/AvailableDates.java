package au.gov.dto.dibp.appointments.availabledates;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;

class AvailableDates {
    private final LocalDate today;
    private final Map<String, AvailableDate> availableDates;

    public AvailableDates(LocalDate today, Map<String, AvailableDate> availableDates) {
        this.today = today;
        this.availableDates = Collections.unmodifiableMap(availableDates);
    }

    public LocalDate getToday() {
        return today;
    }

    public Map<String, AvailableDate> getAvailableDates() {
        return availableDates;
    }
}
