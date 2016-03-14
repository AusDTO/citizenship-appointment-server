package au.gov.dto.dibp.appointments.casper;

import com.github.raonifn.casperjs.junit.CasperJSTestCase;
import com.github.raonifn.casperjs.junit.CasperRunner;
import org.junit.runners.model.InitializationError;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;

public class CasperRunnerFromFilePaths extends CasperRunner {

    private final Class<?> clazz;

    public CasperRunnerFromFilePaths(Class<?> clazz) throws InitializationError {
        super(clazz);
        this.clazz = clazz;
    }

    @Override
    protected List<CasperJSTestCase> getChildren() {
        CasperJSTestCase casperJSTestCase = new CasperJSTestCase();

        try {
            casperJSTestCase.setUrl(clazz, new File("node_modules/citizenship-appointment-client/test/monitor/bookAppointment.js").toURI().toURL());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        return Arrays.asList(casperJSTestCase);
    }
}
