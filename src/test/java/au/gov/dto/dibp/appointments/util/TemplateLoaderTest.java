package au.gov.dto.dibp.appointments.util;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import org.junit.Test;
import org.springframework.core.io.DefaultResourceLoader;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class TemplateLoaderTest {

    private static final String UNIT_ID = "2";
    private static final String API_SESSION = "23";
    private static final String SERVICE_ADDRESS = "233";
    private static final String UUID = "3232";

    @Test
    public void test_loadTemplateByPath_shouldLoadATemplateAndCompileItSuccessfully_basedOnGetServiceDetailsTemplate(){

        TemplateLoader templateLoader = new TemplateLoader(Mustache.compiler(), new DefaultResourceLoader());
        Template template = templateLoader.loadTemplateByPath("GetUnit.mustache");

        Map<String, String> data = new HashMap<>();
        data.put("unitId", UNIT_ID);
        data.put("apiSessionId", API_SESSION);
        data.put("serviceAddress", SERVICE_ADDRESS);
        data.put("messageUUID", UUID);
        String requestBody = template.execute(data);

        assertThat(getExpectedTemplateFormat().replaceAll("\\s",""), is(requestBody.replaceAll("\\s","")));
    }

    private String getExpectedTemplateFormat(){
        return
        "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\"\n" +
        "               xmlns:a=\"http://www.w3.org/2005/08/addressing\"\n" +
        "               xmlns:ser=\"http://www.qnomy.com/Services\">\n" +
        "    <soap:Header>\n" +
        "        <a:Action>http://www.qnomy.com/Services/IsvcUnit/Get</a:Action>\n" +
        "        <a:MessageID>urn:uuid:3232</a:MessageID>\n" +
        "        <a:ReplyTo>\n" +
        "            <a:Address>http://www.w3.org/2005/08/addressing/anonymous</a:Address>\n" +
        "        </a:ReplyTo>\n " +
        "       <a:To>\n" +
        "            233\n" +
        "        </a:To>\n" +
        "    </soap:Header>\n" +
        "    <soap:Body>\n" +
        "        <ser:Get>\n" +
        "            <ser:apiSessionId>23</ser:apiSessionId>\n" +
        "            <ser:unitId>2</ser:unitId>\n" +
        "        </ser:Get>\n" +
        "    </soap:Body>\n" +
        "</soap:Envelope>\n";
    }



}
