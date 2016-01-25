package au.gov.dto.dibp.appointments.qflowintegration;

import okhttp3.internal.tls.OkHostnameVerifier;
import org.springframework.stereotype.Component;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.security.cert.X509Certificate;

@Component
class WildcardCertificateHostnameVerifier implements HostnameVerifier {
    private final OkHostnameVerifier okHostnameVerifier = OkHostnameVerifier.INSTANCE;

    @Override
    public boolean verify(String hostname, SSLSession sslSession) {
        if (okHostnameVerifier.verify(hostname, sslSession)) {
            return true;
        }
        try {
            X509Certificate certificate = sslSession.getPeerCertificateChain()[0];
            String distinguishedName = certificate.getSubjectDN().getName();
            String trimmedDistinguishedName = distinguishedName.replace("CN=*", "");
            return hostname.endsWith(trimmedDistinguishedName);
        } catch (SSLException e) {
            throw new RuntimeException("Could not verify hostname " + hostname, e);
        }
    }
}
