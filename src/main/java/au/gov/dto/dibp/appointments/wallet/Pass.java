package au.gov.dto.dibp.appointments.wallet;

import de.brendamour.jpasskit.PKPass;
import de.brendamour.jpasskit.signing.IPKPassTemplate;
import de.brendamour.jpasskit.signing.IPKSigningUtil;
import de.brendamour.jpasskit.signing.PKSigningException;
import de.brendamour.jpasskit.signing.PKSigningInformation;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class Pass {
    private final PKPass pkPass;
    private final PKSigningInformation pkSigningInformation;
    private final IPKPassTemplate passTemplate;
    private final IPKSigningUtil signingUtil;

    public Pass(PKPass pkPass,
                IPKPassTemplate passTemplate,
                PKSigningInformation pkSigningInformation,
                IPKSigningUtil signingUtil) {
        this.pkPass = pkPass;
        this.pkSigningInformation = pkSigningInformation;
        this.passTemplate = passTemplate;
        this.signingUtil = signingUtil;
    }

    public InputStream getInputStream() {
        try {
            byte[] passArchive = signingUtil.createSignedAndZippedPkPassArchive(pkPass, passTemplate, pkSigningInformation);
            return new ByteArrayInputStream(passArchive);
        } catch (PKSigningException e) {
            throw new RuntimeException("Problem creating signed and zipped pass archive", e);
        }
    }

    PKPass getUnsignedPass() {
        return pkPass;
    }
}
