package au.gov.dto.dibp.appointments.util.casper;

import com.github.raonifn.casperjs.junit.CasperExecutor;
import com.github.raonifn.casperjs.junit.CasperJSTestCase;
import com.github.raonifn.casperjs.junit.JUnitMethodCaller;
import org.junit.AssumptionViolatedException;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class CasperJsTestCase extends CasperJSTestCase {
    private final URL jsUrl;

    public CasperJsTestCase(Class clazz, URL jsUrl) {
        setUrl(clazz, jsUrl);
        this.jsUrl = jsUrl;
    }

    @Override
    public void run(RunNotifier notifier, JUnitMethodCaller jUnitMethodCaller) {
        notifier.fireTestStarted(getDescription());
        jUnitMethodCaller.before();
        try {
            CasperExecutor executor = new CasperExecutor();

            for (Map.Entry<String, String> mapEnv : jUnitMethodCaller.getCasperEnvironment().entrySet()) {
                executor.addEnv(mapEnv.getKey(), mapEnv.getValue());
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            executor.pipeOut(out);
            executor.pipeOut(System.out);
            String fileName = jsUrl.getPath().replaceAll(".*\\/(.*)", "$1");
            int result = executor.executeCasper(jsUrl.getPath(), "--script-name= " + fileName);
            if (result == 0) {
                notifier.fireTestFinished(getDescription());
                return;
            }

            String msg = this.readMessage(out);
            AssumptionViolatedException ex = new AssumptionViolatedException(msg);
            notifier.fireTestFailure(new Failure(getDescription(), ex));

        } catch (Exception ex) {
            notifier.fireTestFailure(new Failure(getDescription(), ex));
        } finally {
            jUnitMethodCaller.after();
        }
    }

    private String readMessage(ByteArrayOutputStream out) throws IOException {
        byte[] array = out.toByteArray();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(array);
        InputStreamReader reader = new InputStreamReader(byteArrayInputStream, StandardCharsets.UTF_8);
        BufferedReader bufferedReader = new BufferedReader(reader);
        StringBuilder msgBuffer = new StringBuilder();
        String line;
        do {
            line = bufferedReader.readLine();
            if (msgBuffer.length() > 0) {
                msgBuffer.append('\n');
            }
            if (line != null) {
                msgBuffer.append(line);
            }
        } while (line != null);

        return msgBuffer.toString();
    }
}
