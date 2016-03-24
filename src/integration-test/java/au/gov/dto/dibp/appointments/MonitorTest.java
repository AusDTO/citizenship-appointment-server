package au.gov.dto.dibp.appointments;

import au.gov.dto.dibp.appointments.util.casper.CasperRunnerFromFilePaths;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;

@RunWith(CasperRunnerFromFilePaths.class)
@DirtiesContext(classMode= DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class MonitorTest {
    private ApplicationForTests applicationForTests;

    @Before
    public void before() {
        System.setProperty("casperjs.executable", System.getProperty("user.dir") + "/node_modules/.bin/casperjs");
        System.setProperty("phantomjs.executable", System.getProperty("user.dir") + "/node_modules/.bin/phantomjs");
        applicationForTests = new ApplicationForTests();
        applicationForTests.runTestApplication();
    }

    @After
    public void cleanUp(){
        applicationForTests.stopApplication();
    }
}
