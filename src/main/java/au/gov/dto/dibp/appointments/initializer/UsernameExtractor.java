package au.gov.dto.dibp.appointments.initializer;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

class UsernameExtractor {
    /**
     * Returns username of current authenticated user. Returns null if no authenticated user present in the SecurityContext.
     */
    public static String getAuthenticatedUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null || !(auth.getPrincipal() instanceof User)) {
            return null;
        }
        User user = (User) auth.getPrincipal();
        return user.getUsername();
    }

    public static String getUsername(HttpServletRequest request) {
        String authenticatedUsername = getAuthenticatedUsername();
        if (authenticatedUsername != null) {
            return authenticatedUsername;
        }
        String usernameFromLoginRequest = request.getParameter("username");
        if (usernameFromLoginRequest != null) {
            return usernameFromLoginRequest;
        }
        String usernameFromQueryString = request.getParameter("id");
        if (usernameFromQueryString != null) {
            return usernameFromQueryString;
        }
        String authorizationHeaderValue = request.getHeader("authorization");
        if (authorizationHeaderValue != null && authorizationHeaderValue.startsWith("ApplePass ")) {
            String authorizationToken = authorizationHeaderValue.replace("ApplePass ", "");
            String decodedAuthorizationToken = new String(Base64.getMimeDecoder().decode(authorizationToken), StandardCharsets.ISO_8859_1);
            String[] credentials = decodedAuthorizationToken.split(":", 2);
            if (credentials.length == 2) {
                return credentials[0];
            }
        }
        return null;
    }
}
