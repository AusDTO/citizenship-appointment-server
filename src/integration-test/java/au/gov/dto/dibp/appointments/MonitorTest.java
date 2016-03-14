package au.gov.dto.dibp.appointments;

import au.gov.dto.dibp.appointments.casper.CasperRunnerFromFilePaths;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@Ignore
@RunWith(CasperRunnerFromFilePaths.class)
public class MonitorTest {

    @Before
    public void before() {
        System.setProperty("casperjs.executable", System.getProperty("user.dir")+"/node_modules/.bin/casperjs");
        System.setProperty("phantomjs.executable", System.getProperty("user.dir")+"/node_modules/.bin/phantomjs");
        Application.main();
    }

    @Test
    public void foo() throws Exception {
        System.out.println("Make Gradle happy.");
    }
}
