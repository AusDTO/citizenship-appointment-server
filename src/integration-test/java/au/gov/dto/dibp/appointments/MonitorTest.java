package au.gov.dto.dibp.appointments;

import au.gov.dto.dibp.appointments.util.casper.CasperRunnerFromFilePaths;
import org.junit.Before;
import org.junit.runner.RunWith;

@RunWith(CasperRunnerFromFilePaths.class)
public class MonitorTest {
    @Before
    public void before() {
        System.setProperty("casperjs.executable", System.getProperty("user.dir") + "/node_modules/.bin/casperjs");
        System.setProperty("phantomjs.executable", System.getProperty("user.dir") + "/node_modules/.bin/phantomjs");
        Application.main();
    }
}
