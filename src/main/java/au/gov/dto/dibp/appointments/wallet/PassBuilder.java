package au.gov.dto.dibp.appointments.wallet;

import au.gov.dto.dibp.appointments.appointmentdetails.AppointmentDetails;
import au.gov.dto.dibp.appointments.client.Client;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.brendamour.jpasskit.PKBarcode;
import de.brendamour.jpasskit.PKField;
import de.brendamour.jpasskit.PKPass;
import de.brendamour.jpasskit.enums.PKBarcodeFormat;
import de.brendamour.jpasskit.enums.PKDateStyle;
import de.brendamour.jpasskit.passes.PKEventTicket;
import de.brendamour.jpasskit.signing.PKInMemorySigningUtil;
import de.brendamour.jpasskit.signing.PKPassTemplateInMemory;
import de.brendamour.jpasskit.signing.PKSigningInformation;
import de.brendamour.jpasskit.signing.PKSigningInformationUtil;
import org.apache.commons.codec.binary.Base64InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;

@Service
class PassBuilder {
    private static final Logger LOG = LoggerFactory.getLogger(PassBuilder.class);
    private static final DateTimeFormatter WALLET_DATE_TIME_FORMATTER = new DateTimeFormatterBuilder()
            .append(DateTimeFormatter.ISO_LOCAL_DATE)
            .appendLiteral('T')
            .appendValue(ChronoField.HOUR_OF_DAY, 2)
            .appendLiteral(':')
            .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
            .appendOffsetId()
            .toFormatter();

    private final ObjectMapper objectMapper;
    private final String passTypeIdentifier;
    private final String teamIdentifier;
    private final String privateKeyP12Base64;
    private final String privateKeyPassPhrase;

    @Autowired
    public PassBuilder(@Value("${wallet.pass.type.identifier}") String passTypeIdentifier,
                       @Value("${wallet.team.identifier}") String teamIdentifier,
                       @Value("${wallet.private.key.p12.base64}") String privateKeyP12Base64,
                       @Value("${wallet.private.key.passphrase}") String privateKeyPassPhrase) {
        this.objectMapper = new ObjectMapper();  // ensure jpasskit has its own ObjectMapper instance
        this.passTypeIdentifier = passTypeIdentifier;
        this.teamIdentifier = teamIdentifier;
        this.privateKeyP12Base64 = privateKeyP12Base64;
        this.privateKeyPassPhrase = privateKeyPassPhrase;
    }

    public Pass createAppointmentPassForClient(Client client, AppointmentDetails appointment, URL walletWebServiceUrl) {
        PKPass passDetails = createPkPass(client, appointment, walletWebServiceUrl);
        PKPassTemplateInMemory passTemplate = createPassTemplate();
        PKSigningInformation signingInformation = createSigningInformation();
        PKInMemorySigningUtil signingUtil = new PKInMemorySigningUtil(objectMapper);
        return new Pass(passDetails, passTemplate, signingInformation, signingUtil);
    }

    /**
     * References:
     * https://developer.apple.com/library/ios/documentation/UserExperience/Reference/PassKit_Bundle/Chapters/TopLevel.html
     * https://developer.apple.com/library/ios/documentation/UserExperience/Reference/PassKit_Bundle/Chapters/LowerLevel.html
     * https://developer.apple.com/library/ios/documentation/UserExperience/Reference/PassKit_Bundle/Chapters/FieldDictionary.html
     */
    PKPass createPkPass(Client client, AppointmentDetails appointment, URL walletWebServiceUrl) {
        PKPass pkPass = new PKPass();
        pkPass.setDescription("Australian citizenship appointment");
        pkPass.setFormatVersion(1);
        pkPass.setOrganizationName("Australian citizenship");
        pkPass.setPassTypeIdentifier(passTypeIdentifier);
        pkPass.setSerialNumber("citizenship");
        pkPass.setTeamIdentifier(teamIdentifier);

        ZonedDateTime appointmentDateTime = appointment.getDateTimeWithTimeZone();
        pkPass.setRelevantDate(Date.from(appointmentDateTime.toInstant()));

        PKField appointmentField = new PKField("appointment", "APPOINTMENT", "Australian citizenship");

        PKField dateField = new PKField("when", "WHEN", WALLET_DATE_TIME_FORMATTER.format(appointmentDateTime));
        dateField.setDateStyle(PKDateStyle.PKDateStyleFull);
        dateField.setTimeStyle(PKDateStyle.PKDateStyleShort);
        dateField.setChangeMessage("Your appointment is on %@");

        String venueStreetAddress = appointment.getUnitAddress();
        String venueNameAndAddress = String.format("Visa and Citizenship Office, %s", venueStreetAddress);

        PKField locationField = new PKField("where", "WHERE", venueNameAndAddress);

        PKField addressField = new PKField("address", "ADDRESS", venueNameAndAddress);

        URI rescheduleUri = null;
        try {
            rescheduleUri = walletWebServiceUrl.toURI().resolve("/login?id=" + client.getClientId());
        } catch (URISyntaxException e) {
            throw new RuntimeException("Problem creating reschedule URI", e);
        }
        PKField rescheduleField = new PKField("reschedule", "CHANGE YOUR APPOINTMENT",  rescheduleUri.toASCIIString());

        PKEventTicket eventTicket = new PKEventTicket();
        eventTicket.setPrimaryFields(Collections.singletonList(appointmentField));
        eventTicket.setSecondaryFields(Collections.singletonList(dateField));
        eventTicket.setAuxiliaryFields(Collections.singletonList(locationField));
        eventTicket.setBackFields(Arrays.asList(addressField, rescheduleField));
        pkPass.setEventTicket(eventTicket);

        PKBarcode pdf417Barcode = new PKBarcode();
        pdf417Barcode.setFormat(PKBarcodeFormat.PKBarcodeFormatPDF417);
        pdf417Barcode.setMessageEncoding(StandardCharsets.ISO_8859_1); // recommended character set for most barcode readers
        pdf417Barcode.setMessage(client.getClientId());
        pdf417Barcode.setAltText("Client ID: " + client.getClientId());
        //noinspection deprecation
        pkPass.setBarcode(pdf417Barcode); // required for iOS 6-8
        pkPass.setBarcodes(Collections.singletonList(pdf417Barcode)); // used by iOS 9+

        pkPass.setBackgroundColor("rgb(0, 74, 164)");
        pkPass.setForegroundColor("rgb(255, 255, 255)");
        pkPass.setLabelColor("rgb(255, 255, 255)");

        String credentials = String.format("%s:%s", client.getClientId(), client.getCustomerId());
        String authenticationToken = new String(Base64.getMimeEncoder(Integer.MAX_VALUE, new byte[]{'\n'}).encode(credentials.getBytes(StandardCharsets.ISO_8859_1)), StandardCharsets.ISO_8859_1);
        pkPass.setAuthenticationToken(authenticationToken);
        pkPass.setWebServiceURL(walletWebServiceUrl);

        if (!pkPass.isValid()) {
            throw new RuntimeException("Invalid pass: " + pkPass.getValidationErrors());
        }
        return pkPass;
    }

    PKPassTemplateInMemory createPassTemplate() {
        PKPassTemplateInMemory passTemplate = new PKPassTemplateInMemory();
        for (String templateFile : new String[]{
                "icon.png", "icon@2x.png", "icon@3x.png",
                "logo.png", "logo@2x.png", "logo@3x.png"
        }) {
            try {
                passTemplate.addFile(templateFile, getClass().getClassLoader().getResourceAsStream("wallet/" + templateFile));
            } catch (IOException e) {
                throw new RuntimeException("Problem creating pass template", e);
            }
        }
        return passTemplate;
    }

    PKSigningInformation createSigningInformation() {
        InputStream appleWwdrcaAsStream = getClass().getClassLoader().getResourceAsStream("wallet/AppleWWDRCA.pem");
        InputStream base64EncodedPrivateKeyAndCertificatePkcs12AsStream = new ByteArrayInputStream(privateKeyP12Base64.getBytes(StandardCharsets.UTF_8));
        Base64InputStream privateKeyAndCertificatePkcs12AsStream = new Base64InputStream(base64EncodedPrivateKeyAndCertificatePkcs12AsStream);
        try {
            return new PKSigningInformationUtil().loadSigningInformationFromPKCS12AndIntermediateCertificate(privateKeyAndCertificatePkcs12AsStream, privateKeyPassPhrase, appleWwdrcaAsStream);
        } catch (IOException | NoSuchAlgorithmException | CertificateException | KeyStoreException | NoSuchProviderException | UnrecoverableKeyException e) {
            throw new RuntimeException("Problem creating pass signing information", e);
        }
    }
}
