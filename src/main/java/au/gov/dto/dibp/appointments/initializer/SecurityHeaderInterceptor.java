package au.gov.dto.dibp.appointments.initializer;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SecurityHeaderInterceptor  extends HandlerInterceptorAdapter {

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        response.addHeader("Content-Security-Policy", "default-src 'self'; " +
                "script-src 'self' www.google-analytics.com;" +
                "img-src 'self' www.google-analytics.com;");
        response.addHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
        super.postHandle(request, response, handler, modelAndView);
    }
}
