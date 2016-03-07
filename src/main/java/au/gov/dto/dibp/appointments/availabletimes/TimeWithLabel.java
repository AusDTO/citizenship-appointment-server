package au.gov.dto.dibp.appointments.availabletimes;

public class TimeWithLabel {
    final String time;
    final String displayTime;

    public TimeWithLabel(String time, String displayTime){
        this.time = time;
        this.displayTime = displayTime;
    }

    public String getTime() {
        return time;
    }

    public String getDisplayTime() {
        return displayTime;
    }
}
