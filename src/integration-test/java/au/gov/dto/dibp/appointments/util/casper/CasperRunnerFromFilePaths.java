package au.gov.dto.dibp.appointments.util.casper;

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
        try {
            CasperJsTestCase testCase = new CasperJsTestCase(clazz, new File("node_modules/citizenship-appointment-client/test/monitor/bookAppointment.js").toURI().toURL());
            return Arrays.asList(testCase);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid URL for CasperJS test script", e);
        }
    }
}
