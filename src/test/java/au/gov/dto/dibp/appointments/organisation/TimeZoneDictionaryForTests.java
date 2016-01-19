package au.gov.dto.dibp.appointments.organisation;

import org.springframework.core.io.DefaultResourceLoader;

public class TimeZoneDictionaryForTests extends TimeZoneDictionary{

    public TimeZoneDictionaryForTests(){
        super(new DefaultResourceLoader());
    }
}
