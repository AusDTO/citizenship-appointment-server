package au.gov.dto.dibp.appointments.security.context;

import au.gov.dto.dibp.appointments.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

@Component
public class SecurityContextSerializer {
    static final String COOKIE_NAME = "session";
    static final String COOKIE_PATH = "/";
    static final int COOKIE_MAX_AGE_SECONDS = 60 * 30;

    private final JwtClientSerializer jwtClientSerializer;

    @Autowired
    public SecurityContextSerializer(JwtClientSerializer jwtClientSerializer) {
        this.jwtClientSerializer = jwtClientSerializer;
    }

    public SecurityContext deserialize(HttpServletRequest request, HttpServletResponse response) {
        Client client = getClientFromSessionCookie(request);
        if (client == null) {
            response.addCookie(createLogoutCookie(request));
            return SecurityContextHolder.createEmptyContext();
        }
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        Authentication authentication = createAuthentication(client);
        securityContext.setAuthentication(authentication);
        return securityContext;
    }

    public void serialize(SecurityContext securityContext, HttpServletRequest request, HttpServletResponse response) {
        if (securityContext.getAuthentication() == null) {
            response.addCookie(createLogoutCookie(request));
            return;
        }
        Client client = (Client) securityContext.getAuthentication().getPrincipal();
        String jwtToken = jwtClientSerializer.serialize(client);
        Cookie sessionCookie = new Cookie(COOKIE_NAME, jwtToken);
        sessionCookie.setPath(COOKIE_PATH);
        sessionCookie.setHttpOnly(true);
        sessionCookie.setSecure(request.isSecure());
        sessionCookie.setMaxAge(COOKIE_MAX_AGE_SECONDS);
        response.addCookie(sessionCookie);
    }

    public boolean hasSessionCookie(HttpServletRequest request) {
        return getSessionCookie(request) != null;
    }

    private Client getClientFromSessionCookie(HttpServletRequest request) {
        Cookie sessionCookie = getSessionCookie(request);
        return sessionCookie == null ? null : jwtClientSerializer.deserialize(sessionCookie.getValue());
    }

    private Cookie getSessionCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }
        Optional<Cookie> maybeCookie = Arrays.stream(request.getCookies()).filter(cookie -> cookie.getName().equals(COOKIE_NAME)).findFirst();
        return maybeCookie.isPresent() ? maybeCookie.get() : null;
    }

    private Cookie createLogoutCookie(HttpServletRequest request) {
        Cookie cookie = new Cookie(COOKIE_NAME, "");
        cookie.setPath(COOKIE_PATH);
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        cookie.setSecure(request.isSecure());
        return cookie;
    }

    private Authentication createAuthentication(Client client) {
        return new UsernamePasswordAuthenticationToken(client, null, Collections.emptyList());
    }
}
