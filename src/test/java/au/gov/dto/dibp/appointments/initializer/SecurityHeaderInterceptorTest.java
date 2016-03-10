package au.gov.dto.dibp.appointments.initializer;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.List;

import static au.gov.dto.dibp.appointments.initializer.SecurityHeaderInterceptor.CONTENT_SECURITY_POLICY_VALUE;
import static au.gov.dto.dibp.appointments.initializer.SecurityHeaderInterceptor.HPKP_MAX_AGE_SECONDS;
import static au.gov.dto.dibp.appointments.initializer.SecurityHeaderInterceptor.HSTS_MAX_AGE_SECONDS;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class SecurityHeaderInterceptorTest {
    @Test
    public void cspReportUriNotAddedWhenNotProvided() throws Exception {
        String cspReportUri = "";
        SecurityHeaderInterceptor interceptor = new SecurityHeaderInterceptor("", "", "https://example.com/hpkp_report", "https://example.com/hpkp_report_only", cspReportUri);
        MockHttpServletResponse response = new MockHttpServletResponse();

        interceptor.postHandle(null, response, null, null);

        List<String> headers = response.getHeaders("Content-Security-Policy");
        assertThat(headers.size(), equalTo(1));
        assertThat(headers.get(0), equalTo(CONTENT_SECURITY_POLICY_VALUE));
    }

    @Test
    public void cspReportUriAddedWhenProvided() throws Exception {
        SecurityHeaderInterceptor interceptor = new SecurityHeaderInterceptor("", "", "https://example.com/hpkp_report", "https://example.com/hpkp_report_only", "https://example.com/csp_report");
        MockHttpServletResponse response = new MockHttpServletResponse();

        interceptor.postHandle(null, response, null, null);

        List<String> headers = response.getHeaders("Content-Security-Policy");
        assertThat(headers.size(), equalTo(1));
        assertThat(headers.get(0), equalTo(CONTENT_SECURITY_POLICY_VALUE + "; report-uri='https://example.com/csp_report'"));
    }

    @Test
    public void hstsHeaderAlwaysAdded() throws Exception {
        SecurityHeaderInterceptor interceptor = new SecurityHeaderInterceptor("", "", "", "https://example.com/hpkp_report_only", "");
        MockHttpServletResponse response = new MockHttpServletResponse();

        interceptor.postHandle(null, response, null, null);

        List<String> headers = response.getHeaders("Strict-Transport-Security");
        assertThat(headers.size(), equalTo(1));
        assertThat(headers.get(0), equalTo("max-age=" + HSTS_MAX_AGE_SECONDS));
    }

    @Test
    public void xFrameOptionsHeaderAlwaysAdded() throws Exception {
        SecurityHeaderInterceptor interceptor = new SecurityHeaderInterceptor("", "", "", "https://example.com/hpkp_report_only", "");
        MockHttpServletResponse response = new MockHttpServletResponse();

        interceptor.postHandle(null, response, null, null);

        List<String> headers = response.getHeaders("X-Frame-Options");
        assertThat(headers.size(), equalTo(1));
        assertThat(headers.get(0), equalTo("DENY"));
    }

    @Test
    public void xXssProtectionHeaderAlwaysAdded() throws Exception {
        SecurityHeaderInterceptor interceptor = new SecurityHeaderInterceptor("", "", "", "https://example.com/hpkp_report_only", "");
        MockHttpServletResponse response = new MockHttpServletResponse();

        interceptor.postHandle(null, response, null, null);

        List<String> headers = response.getHeaders("X-Xss-Protection");
        assertThat(headers.size(), equalTo(1));
        assertThat(headers.get(0), equalTo("1; mode=block"));
    }

    @Test
    public void xContentTypeOptionsHeaderAlwaysAdded() throws Exception {
        SecurityHeaderInterceptor interceptor = new SecurityHeaderInterceptor("", "", "", "https://example.com/hpkp_report_only", "");
        MockHttpServletResponse response = new MockHttpServletResponse();

        interceptor.postHandle(null, response, null, null);

        List<String> headers = response.getHeaders("X-Content-Type-Options");
        assertThat(headers.size(), equalTo(1));
        assertThat(headers.get(0), equalTo("nosniff"));
    }

    @Test
    public void hpkpHeadersNotAddedIfFingerprint1NotProvided() throws Exception {
        String fingerprint1 = "";
        SecurityHeaderInterceptor interceptor = new SecurityHeaderInterceptor(fingerprint1, "fingerprint2=", "https://example.com/hpkp_report", "https://example.com/hpkp_report_only", "https://example.com/csp_report");
        MockHttpServletResponse response = new MockHttpServletResponse();

        interceptor.postHandle(null, response, null, null);

        assertThat(response.getHeaders("Public-Key-Pins").size(), equalTo(0));
        assertThat(response.getHeaders("Public-Key-Pins-Report-Only").size(), equalTo(0));
    }

    @Test
    public void hpkpHeadersNotAddedIfFingerprint2NotProvided() throws Exception {
        String fingerprint2 = "";
        SecurityHeaderInterceptor interceptor = new SecurityHeaderInterceptor("fingerprint1=", fingerprint2, "https://example.com/hpkp_report", "https://example.com/hpkp_report_only", "https://example.com/csp_report");
        MockHttpServletResponse response = new MockHttpServletResponse();

        interceptor.postHandle(null, response, null, null);

        assertThat(response.getHeaders("Public-Key-Pins").size(), equalTo(0));
        assertThat(response.getHeaders("Public-Key-Pins-Report-Only").size(), equalTo(0));
    }

    @Test
    public void hpkpHeadersAddedEvenIfHpkpReportUrisNotProvided() throws Exception {
        String hpkpReportUriEnforced = "";
        String hpkpReportUriReportOnly = "";
        SecurityHeaderInterceptor interceptor = new SecurityHeaderInterceptor("fingerprint1=", "fingerprint2=", hpkpReportUriEnforced, hpkpReportUriReportOnly, "https://example.com/csp_report");
        MockHttpServletResponse response = new MockHttpServletResponse();

        interceptor.postHandle(null, response, null, null);

        List<String> publicKeyPinsHeaders = response.getHeaders("Public-Key-Pins");
        assertThat(publicKeyPinsHeaders.size(), equalTo(1));
        assertThat(publicKeyPinsHeaders.get(0), equalTo("pin-sha256='fingerprint1='; pin-sha256='fingerprint2='; max-age=" + HPKP_MAX_AGE_SECONDS));

        List<String> publicKeyPinsReportOnlyHeaders = response.getHeaders("Public-Key-Pins-Report-Only");
        assertThat(publicKeyPinsReportOnlyHeaders.size(), equalTo(1));
        assertThat(publicKeyPinsReportOnlyHeaders.get(0), equalTo("pin-sha256='fingerprint1='; pin-sha256='fingerprint2='"));
    }

    @Test
    public void hpkpHeadersAddedWithHpkpReportUrisWhenProvided() throws Exception {
        SecurityHeaderInterceptor interceptor = new SecurityHeaderInterceptor("fingerprint1=", "fingerprint2=", "https://example.com/hpkp_report", "https://example.com/hpkp_report_only", "https://example.com/csp_report");
        MockHttpServletResponse response = new MockHttpServletResponse();

        interceptor.postHandle(null, response, null, null);

        List<String> publicKeyPinsHeaders = response.getHeaders("Public-Key-Pins");
        assertThat(publicKeyPinsHeaders.size(), equalTo(1));
        assertThat(publicKeyPinsHeaders.get(0), equalTo("pin-sha256='fingerprint1='; pin-sha256='fingerprint2='; max-age=" + HPKP_MAX_AGE_SECONDS + "; report-uri='https://example.com/hpkp_report'"));

        List<String> publicKeyPinsReportOnlyHeaders = response.getHeaders("Public-Key-Pins-Report-Only");
        assertThat(publicKeyPinsReportOnlyHeaders.size(), equalTo(1));
        assertThat(publicKeyPinsReportOnlyHeaders.get(0), equalTo("pin-sha256='fingerprint1='; pin-sha256='fingerprint2='; report-uri='https://example.com/hpkp_report_only'"));
    }
}
