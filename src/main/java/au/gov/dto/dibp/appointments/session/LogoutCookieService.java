package au.gov.dto.dibp.appointments.session;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class LogoutCookieService {
    private static final String COOKIE_NAME = "session";
    private static final String COOKIE_PATH = "/";

    public static Cookie getLogoutCookie(HttpServletRequest request){
        Cookie cookie = new Cookie(COOKIE_NAME, "");
        cookie.setPath(COOKIE_PATH);
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        cookie.setSecure(request.isSecure());
        return cookie;
    }
}
