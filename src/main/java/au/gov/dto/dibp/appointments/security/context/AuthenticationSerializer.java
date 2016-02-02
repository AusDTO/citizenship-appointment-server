package au.gov.dto.dibp.appointments.security.context;

import au.gov.dto.dibp.appointments.client.Client;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
    private static final String ANONYMOUS_AUTHENTICATION_TOKEN_PRINCIPAL = "anonymousUser";
    private static final String ANONYMOUS_ROLE = "ROLE_ANONYMOUS";

    private final ObjectMapper objectMapper;
    private final String anonymousAuthenticationTokenKey;

    @Autowired
    public AuthenticationSerializer(ObjectMapper objectMapper,
                                    @Value("${security.anonymous.token.key}") String anonymousAuthenticationTokenKey) {
        this.objectMapper = objectMapper;
        this.anonymousAuthenticationTokenKey = anonymousAuthenticationTokenKey;
    }

    public byte[] serializeToByteArray(Authentication authentication) {
        if (authentication == null
                || authentication.getPrincipal() == null
                || !Client.class.isAssignableFrom(authentication.getPrincipal().getClass())) {
            return "".getBytes(StandardCharsets.UTF_8);
        }
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
            return new AnonymousAuthenticationToken(anonymousAuthenticationTokenKey,
                    ANONYMOUS_AUTHENTICATION_TOKEN_PRINCIPAL,
                    Collections.singletonList(new SimpleGrantedAuthority(ANONYMOUS_ROLE)));
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
