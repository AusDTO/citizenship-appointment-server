package au.gov.dto.dibp.appointments.security.context;

import au.gov.dto.dibp.appointments.client.Client;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

/**
 * Adapted from oakfusion/spring-cookie-session under the MIT license:
 * https://github.com/oakfusion/spring-cookie-session/blob/spring-cookie-session-1.0/src/main/java/com/oakfusion/security/AuthenticationSerializer.java
 */
@Component
public class AuthenticationSerializer {
    private final ObjectMapper objectMapper;

    @Autowired
    public AuthenticationSerializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public byte[] serializeToByteArray(Authentication authentication) {
        Client client = (Client) authentication.getPrincipal();
        try {
            String clientJson = objectMapper.writeValueAsString(client);
            return clientJson.getBytes(StandardCharsets.UTF_8);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Problem serializing client to JSON: " + client.toString(), e);
        }
    }

    public Authentication deserializeFrom(byte[] bytes) {
        if (bytes == null || new String(bytes, StandardCharsets.UTF_8).isEmpty()) {
            return null;
        }
        String clientJson = new String(bytes, StandardCharsets.UTF_8);
        try {
            Client client = objectMapper.readValue(clientJson, Client.class);
            return new UsernamePasswordAuthenticationToken(client, null, Collections.emptyList());
        } catch (IOException e) {
            throw new RuntimeException("Problem deserializing Client JSON: " + clientJson, e);
        }
    }
}
