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
    static final String CONTENT_SECURITY_POLICY_VALUE = "default-src 'self'; script-src 'self' www.google-analytics.com; img-src 'self' www.google-analytics.com";
    static final long HSTS_MAX_AGE_SECONDS = 31536000L; // 1 year
    static final long HPKP_MAX_AGE_SECONDS = 10L;

    private final String certificateFingerprintBase64_1;
    private final String certificateFingerprintBase64_2;
    private final String hpkpReportUri;
    private final String cspReportUri;

    @Autowired
    public SecurityHeaderInterceptor(@Value("${public.key.fingerprint.base64.1:}") String certificateFingerprintBase64_1,
                                     @Value("${public.key.fingerprint.base64.2:}") String certificateFingerprintBase64_2,
                                     @Value("${hpkp.report.uri:}") String hpkpReportUri,
                                     @Value("${csp.report.uri:}") String cspReportUri) {
        this.certificateFingerprintBase64_1 = certificateFingerprintBase64_1;
        this.certificateFingerprintBase64_2 = certificateFingerprintBase64_2;
        this.hpkpReportUri = hpkpReportUri;
        this.cspReportUri = cspReportUri;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        addContentSecurityPolicyHeader(response);
        addStrictTransportSecurityHeader(response);
        addPublicKeyPinsHeader(response);
        addPublicKeyPinsReportOnlyHeader(response);
        addXFrameOptionsHeader(response);
        addXXssProtectionHeader(response);
        addXContentTypeOptionsHeader(response);
        super.postHandle(request, response, handler, modelAndView);
    }

    private void addContentSecurityPolicyHeader(HttpServletResponse response) {
        String headerValue = CONTENT_SECURITY_POLICY_VALUE;
        if (StringUtils.isNotBlank(cspReportUri)) {
            headerValue += String.format("; report-uri='%s'", cspReportUri);
        }
        response.setHeader("Content-Security-Policy", headerValue);
    }

    private void addStrictTransportSecurityHeader(HttpServletResponse response) {
        response.setHeader("Strict-Transport-Security", "max-age=" + HSTS_MAX_AGE_SECONDS);
    }

    /**
     * See https://scotthelme.co.uk/hpkp-http-public-key-pinning/
     * and http://www.exploresecurity.com/five-considerations-for-http-public-key-pinning-hpkp/
     * See https://tools.ietf.org/html/rfc7469
     */
    private void addPublicKeyPinsHeader(HttpServletResponse response) {
        if (StringUtils.isNotBlank(certificateFingerprintBase64_1) && StringUtils.isNotBlank(certificateFingerprintBase64_2)) {
            String headerValue = String.format("pin-sha256='%s'; pin-sha256='%s'; max-age=%d", certificateFingerprintBase64_1, certificateFingerprintBase64_2, HPKP_MAX_AGE_SECONDS);
            if (StringUtils.isNotBlank(hpkpReportUri)) {
                headerValue += String.format("; report-uri='%s'", hpkpReportUri);
            }
            response.setHeader("Public-Key-Pins", headerValue);
        }
    }

    private void addPublicKeyPinsReportOnlyHeader(HttpServletResponse response) {
        if (StringUtils.isNotBlank(certificateFingerprintBase64_1) && StringUtils.isNotBlank(certificateFingerprintBase64_2)) {
            String headerValue = String.format("pin-sha256='%s'; pin-sha256='%s'", certificateFingerprintBase64_1, certificateFingerprintBase64_2);
            if (StringUtils.isNotBlank(hpkpReportUri)) {
                headerValue += String.format("; report-uri='%s'", hpkpReportUri);
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
