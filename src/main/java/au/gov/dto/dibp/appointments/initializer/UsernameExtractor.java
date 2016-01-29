package au.gov.dto.dibp.appointments.initializer;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

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
}
