package au.gov.dto.dibp.appointments.client;

import org.springframework.stereotype.Component;

@Component
public class ClientIdValidator {

    public boolean isClientIdValid(String clientId){
        return clientId != null && clientId.matches("[0-9]{11}");
    }
}
