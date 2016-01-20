package au.gov.dto.dibp.appointments.organisation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.stream.Stream;

@Component
class TimeZoneDictionary {

    private final HashMap<String, String> timeZones = new HashMap<>();

    @Autowired
    public TimeZoneDictionary(ResourceLoader resourceLoader){
        readTimeZonesConfigurationFile(resourceLoader);
    }

    private void readTimeZonesConfigurationFile(ResourceLoader resourceLoader) {
        Resource resource = resourceLoader.getResource("classpath:" + "timezones.txt");

        try (Stream<String> lines = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)).lines()){
            lines.forEach(line -> {
                String[] entry = line.split(":");
                timeZones.put(entry[0], entry[1]);
            });
        } catch (IOException e) {
            throw new RuntimeException("Error reading timezones from timezones.txt", e);
        }
    }

    public String getTimeZoneIANA(String timeZoneId){
        return MicrosoftToIANA.get(timeZones.get(timeZoneId));
    }


    private static final HashMap<String, String> MicrosoftToIANA = new HashMap<>();

    static {
        MicrosoftToIANA.put("Afghanistan Standard Time", "Asia/Kabul");
        MicrosoftToIANA.put("Alaskan Standard Time", "America/Anchorage");
        MicrosoftToIANA.put("Arab Standard Time", "Asia/Riyadh");
        MicrosoftToIANA.put("Arabian Standard Time", "Asia/Dubai");
        MicrosoftToIANA.put("Arabic Standard Time", "Asia/Baghdad");
        MicrosoftToIANA.put("Argentina Standard Time", "America/Buenos_Aires");
        MicrosoftToIANA.put("Atlantic Standard Time", "America/Halifax");
        MicrosoftToIANA.put("AUS Central Standard Time", "Australia/Darwin");
        MicrosoftToIANA.put("AUS Eastern Standard Time", "Australia/Sydney");
        MicrosoftToIANA.put("Azerbaijan Standard Time", "Asia/Baku");
        MicrosoftToIANA.put("Azores Standard Time", "Atlantic/Azores");
        MicrosoftToIANA.put("Bangladesh Standard Time", "Asia/Dhaka");
        MicrosoftToIANA.put("Canada Central Standard Time", "America/Regina");
        MicrosoftToIANA.put("Cape Verde Standard Time", "Atlantic/Cape_Verde");
        MicrosoftToIANA.put("Caucasus Standard Time", "Asia/Yerevan");
        MicrosoftToIANA.put("Cen. Australia Standard Time", "Australia/Adelaide");
        MicrosoftToIANA.put("Central America Standard Time", "America/Guatemala");
        MicrosoftToIANA.put("Central Asia Standard Time", "Asia/Almaty");
        MicrosoftToIANA.put("Central Brazilian Standard Time", "America/Cuiaba");
        MicrosoftToIANA.put("Central Europe Standard Time", "Europe/Budapest");
        MicrosoftToIANA.put("Central European Standard Time", "Europe/Warsaw");
        MicrosoftToIANA.put("Central Pacific Standard Time", "Pacific/Guadalcanal");
        MicrosoftToIANA.put("Central Standard Time (Mexico)", "America/Mexico_City");
        MicrosoftToIANA.put("Central Standard Time", "America/Chicago");
        MicrosoftToIANA.put("China Standard Time", "Asia/Shanghai");
        MicrosoftToIANA.put("Dateline Standard Time", "Etc/GMT+12");
        MicrosoftToIANA.put("E. Africa Standard Time", "Africa/Nairobi");
        MicrosoftToIANA.put("E. Australia Standard Time", "Australia/Brisbane");
        MicrosoftToIANA.put("E. Europe Standard Time", "Europe/Minsk");
        MicrosoftToIANA.put("E. South America Standard Time", "America/Sao_Paulo");
        MicrosoftToIANA.put("Eastern Standard Time", "America/New_York");
        MicrosoftToIANA.put("Egypt Standard Time", "Africa/Cairo");
        MicrosoftToIANA.put("Ekaterinburg Standard Time", "Asia/Yekaterinburg");
        MicrosoftToIANA.put("Fiji Standard Time", "Pacific/Fiji");
        MicrosoftToIANA.put("FLE Standard Time", "Europe/Kiev");
        MicrosoftToIANA.put("Georgian Standard Time", "Asia/Tbilisi");
        MicrosoftToIANA.put("GMT Standard Time", "Europe/London");
        MicrosoftToIANA.put("Greenland Standard Time", "America/Godthab");
        MicrosoftToIANA.put("Greenwich Standard Time", "Atlantic/Reykjavik");
        MicrosoftToIANA.put("GTB Standard Time", "Europe/Istanbul");
        MicrosoftToIANA.put("Hawaiian Standard Time", "Pacific/Honolulu");
        MicrosoftToIANA.put("India Standard Time", "Asia/Calcutta");
        MicrosoftToIANA.put("Iran Standard Time", "Asia/Tehran");
        MicrosoftToIANA.put("Israel Standard Time", "Asia/Jerusalem");
        MicrosoftToIANA.put("Jordan Standard Time", "Asia/Amman");
        MicrosoftToIANA.put("Kamchatka Standard Time", "Asia/Kamchatka");
        MicrosoftToIANA.put("Korea Standard Time", "Asia/Seoul");
        MicrosoftToIANA.put("Magadan Standard Time", "Asia/Magadan");
        MicrosoftToIANA.put("Mauritius Standard Time", "Indian/Mauritius");
        MicrosoftToIANA.put("Mid-Atlantic Standard Time", "Etc/GMT+2");
        MicrosoftToIANA.put("Middle East Standard Time", "Asia/Beirut");
        MicrosoftToIANA.put("Montevideo Standard Time", "America/Montevideo");
        MicrosoftToIANA.put("Morocco Standard Time", "Africa/Casablanca");
        MicrosoftToIANA.put("Mountain Standard Time (Mexico)", "America/Chihuahua");
        MicrosoftToIANA.put("Mountain Standard Time", "America/Denver");
        MicrosoftToIANA.put("Myanmar Standard Time", "Asia/Rangoon");
        MicrosoftToIANA.put("N. Central Asia Standard Time", "Asia/Novosibirsk");
        MicrosoftToIANA.put("Namibia Standard Time", "Africa/Windhoek");
        MicrosoftToIANA.put("Nepal Standard Time", "Asia/Katmandu");
        MicrosoftToIANA.put("New Zealand Standard Time", "Pacific/Auckland");
        MicrosoftToIANA.put("Newfoundland Standard Time", "America/St_Johns");
        MicrosoftToIANA.put("North Asia East Standard Time", "Asia/Irkutsk");
        MicrosoftToIANA.put("North Asia Standard Time", "Asia/Krasnoyarsk");
        MicrosoftToIANA.put("Pacific SA Standard Time", "America/Santiago");
        MicrosoftToIANA.put("Pacific Standard Time (Mexico)", "America/Tijuana");
        MicrosoftToIANA.put("Pacific Standard Time", "America/Los_Angeles");
        MicrosoftToIANA.put("Pakistan Standard Time", "Asia/Karachi");
        MicrosoftToIANA.put("Paraguay Standard Time", "America/Asuncion");
        MicrosoftToIANA.put("Romance Standard Time", "Europe/Paris");
        MicrosoftToIANA.put("Russian Standard Time", "Europe/Moscow");
        MicrosoftToIANA.put("SA Eastern Standard Time", "America/Cayenne");
        MicrosoftToIANA.put("SA Pacific Standard Time", "America/Bogota");
        MicrosoftToIANA.put("SA Western Standard Time", "America/La_Paz");
        MicrosoftToIANA.put("Samoa Standard Time", "Pacific/Apia");
        MicrosoftToIANA.put("SE Asia Standard Time", "Asia/Bangkok");
        MicrosoftToIANA.put("Singapore Standard Time", "Asia/Singapore");
        MicrosoftToIANA.put("South Africa Standard Time", "Africa/Johannesburg");
        MicrosoftToIANA.put("Sri Lanka Standard Time", "Asia/Colombo");
        MicrosoftToIANA.put("Syria Standard Time", "Asia/Damascus");
        MicrosoftToIANA.put("Taipei Standard Time", "Asia/Taipei");
        MicrosoftToIANA.put("Tasmania Standard Time", "Australia/Hobart");
        MicrosoftToIANA.put("Tokyo Standard Time", "Asia/Tokyo");
        MicrosoftToIANA.put("Tonga Standard Time", "Pacific/Tongatapu");
        MicrosoftToIANA.put("Ulaanbaatar Standard Time", "Asia/Ulaanbaatar");
        MicrosoftToIANA.put("US Eastern Standard Time", "America/Indianapolis");
        MicrosoftToIANA.put("US Mountain Standard Time", "America/Phoenix");
        MicrosoftToIANA.put("GMT ", "Etc/GMT");
        MicrosoftToIANA.put("GMT +12", "Etc/GMT-12");
        MicrosoftToIANA.put("GMT -02", "Etc/GMT+2");
        MicrosoftToIANA.put("GMT -11", "Etc/GMT+11");
        MicrosoftToIANA.put("Venezuela Standard Time", "America/Caracas");
        MicrosoftToIANA.put("Vladivostok Standard Time", "Asia/Vladivostok");
        MicrosoftToIANA.put("W. Australia Standard Time", "Australia/Perth");
        MicrosoftToIANA.put("W. Central Africa Standard Time", "Africa/Lagos");
        MicrosoftToIANA.put("W. Europe Standard Time", "Europe/Berlin");
        MicrosoftToIANA.put("West Asia Standard Time", "Asia/Tashkent");
        MicrosoftToIANA.put("West Pacific Standard Time", "Pacific/Port_Moresby");
        MicrosoftToIANA.put("Yakutsk Standard Time", "Asia/Yakutsk");
    }

}
