package au.gov.dto.dibp.appointments.initializer;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class SecurityHeaderInterceptor extends HandlerInterceptorAdapter {
    static final String CONTENT_SECURITY_POLICY_VALUE = "default-src 'self'; script-src 'self' www.google-analytics.com http://*.hotjar.com https://*.hotjar.com https://cdn.jsdelivr.net; img-src 'self' www.google-analytics.com; connect-src 'self' http://*.hotjar.com https://*.hotjar.com wss://ins4.hotjar.com;";
    static final long HSTS_MAX_AGE_SECONDS = 31536000L; // 1 year
    static final long HPKP_MAX_AGE_SECONDS = 1800L;

    private final String publicKeyFingerprintBase64_1;
    private final String publicKeyFingerprintBase64_2;
    private final String hpkpReportUriEnforced;
    private final String hpkpReportUriReportOnly;
    private final String cspReportUri;

    @Autowired
    public SecurityHeaderInterceptor(@Value("${public.key.fingerprint.base64.1:}") String publicKeyFingerprintBase64_1,
                                     @Value("${public.key.fingerprint.base64.2:}") String publicKeyFingerprintBase64_2,
                                     @Value("${hpkp.report.uri.enforced:}") String hpkpReportUriEnforced,
                                     @Value("${hpkp.report.uri.report.only:}") String hpkpReportUriReportOnly,
                                     @Value("${csp.report.uri:}") String cspReportUri) {
        this.publicKeyFingerprintBase64_1 = publicKeyFingerprintBase64_1;
        this.publicKeyFingerprintBase64_2 = publicKeyFingerprintBase64_2;
        this.hpkpReportUriEnforced = hpkpReportUriEnforced;
        this.hpkpReportUriReportOnly = hpkpReportUriReportOnly;
        this.cspReportUri = cspReportUri;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        addContentSecurityPolicyHeader(response);
        addStrictTransportSecurityHeader(response);
        //addPublicKeyPinsHeader(response);
        addPublicKeyPinsReportOnlyHeader(response);
        addXFrameOptionsHeader(response);
        addXXssProtectionHeader(response);
        addXContentTypeOptionsHeader(response);
        super.postHandle(request, response, handler, modelAndView);
    }

    private void addContentSecurityPolicyHeader(HttpServletResponse response) {
        String headerValue = CONTENT_SECURITY_POLICY_VALUE;
        if (StringUtils.isNotBlank(cspReportUri)) {
            headerValue += String.format("; report-uri %s", cspReportUri);
        }
        response.setHeader("Content-Security-Policy", headerValue);
    }

    private void addStrictTransportSecurityHeader(HttpServletResponse response) {
        response.setHeader("Strict-Transport-Security", "max-age=" + HSTS_MAX_AGE_SECONDS +"; includeSubDomains; preload");
    }

    /**
     * See https://scotthelme.co.uk/hpkp-http-public-key-pinning/
     * and http://www.exploresecurity.com/five-considerations-for-http-public-key-pinning-hpkp/
     * See https://tools.ietf.org/html/rfc7469
     */
    private void addPublicKeyPinsHeader(HttpServletResponse response) {
        if (StringUtils.isNotBlank(publicKeyFingerprintBase64_1) && StringUtils.isNotBlank(publicKeyFingerprintBase64_2)) {
            String headerValue = String.format("pin-sha256=\"%s\"; pin-sha256=\"%s\"; max-age=%d", publicKeyFingerprintBase64_1, publicKeyFingerprintBase64_2, HPKP_MAX_AGE_SECONDS);
            if (StringUtils.isNotBlank(hpkpReportUriEnforced)) {
                headerValue += String.format("; report-uri=\"%s\"", hpkpReportUriEnforced);
            }
            response.setHeader("Public-Key-Pins", headerValue);
        }
    }

    private void addPublicKeyPinsReportOnlyHeader(HttpServletResponse response) {
        if (StringUtils.isNotBlank(publicKeyFingerprintBase64_1) && StringUtils.isNotBlank(publicKeyFingerprintBase64_2)) {
            String headerValue = String.format("pin-sha256=\"%s\"; pin-sha256=\"%s\"", publicKeyFingerprintBase64_1, publicKeyFingerprintBase64_2);
            if (StringUtils.isNotBlank(hpkpReportUriReportOnly)) {
                headerValue += String.format("; report-uri=\"%s\"", hpkpReportUriReportOnly);
            }
            response.setHeader("Public-Key-Pins-Report-Only", headerValue);
        }
    }

    /**
     * See https://tools.ietf.org/html/rfc7034
     */
    private void addXFrameOptionsHeader(HttpServletResponse response) {
        response.setHeader("X-Frame-Options", "DENY");
    }

    /**
     * See https://blogs.msdn.microsoft.com/ie/2008/07/02/ie8-security-part-iv-the-xss-filter/
     */
    private void addXXssProtectionHeader(HttpServletResponse response) {
        response.setHeader("X-Xss-Protection", "1; mode=block");
    }

    /**
     * See https://blogs.msdn.microsoft.com/ie/2008/09/02/ie8-security-part-vi-beta-2-update/
     */
    private void addXContentTypeOptionsHeader(HttpServletResponse response) {
        response.setHeader("X-Content-Type-Options", "nosniff");
    }
}
