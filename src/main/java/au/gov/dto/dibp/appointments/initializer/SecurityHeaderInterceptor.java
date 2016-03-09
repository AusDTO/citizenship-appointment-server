package au.gov.dto.dibp.appointments.initializer;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SecurityHeaderInterceptor extends HandlerInterceptorAdapter {
    private static final long HSTS_MAX_AGE_SECONDS = 31536000L;
    private static final long HPKP_MAX_AGE_SECONDS = 10L;

    private final String certificateFingerprintSha256;

    public SecurityHeaderInterceptor(String certificateFingerprintSha256) {
        this.certificateFingerprintSha256 = certificateFingerprintSha256;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        addContentSecurityPolicyHeader(response);
        addStrictTransportSecurityHeader(response);
        addPublicKeyPinsHeader(response);
//        addPublicKeyPinsReportOnlyHeader(response);
        addXFrameOptionsHeader(response);
        super.postHandle(request, response, handler, modelAndView);
    }

    private void addContentSecurityPolicyHeader(HttpServletResponse response) {
        response.addHeader("Content-Security-Policy", "default-src 'self'; script-src 'self' www.google-analytics.com; img-src 'self' www.google-analytics.com;");
    }

    private void addStrictTransportSecurityHeader(HttpServletResponse response) {
        response.addHeader("Strict-Transport-Security", "max-age=" + HSTS_MAX_AGE_SECONDS);
    }

    /**
     * See https://scotthelme.co.uk/hpkp-http-public-key-pinning/
     * and http://www.exploresecurity.com/five-considerations-for-http-public-key-pinning-hpkp/
     */
    private void addPublicKeyPinsHeader(HttpServletResponse response) {
        if (StringUtils.isNotBlank(certificateFingerprintSha256)) {
            response.addHeader("Public-Key-Pins", "pin-sha256='" + certificateFingerprintSha256 + "'; max-age=" + HPKP_MAX_AGE_SECONDS);
        }
    }

    private void addPublicKeyPinsReportOnlyHeader(HttpServletResponse response) {
        if (StringUtils.isNotBlank(certificateFingerprintSha256)) {
            response.addHeader("Public-Key-Pins-Report-Only", "pin-sha256='" + certificateFingerprintSha256 + "'");
        }
    }

    private void addXFrameOptionsHeader(HttpServletResponse response) {
        response.addHeader("X-Frame-Options", "DENY");
    }
}
