package au.gov.dto.dibp.appointments.qflowintegration;

import au.gov.dto.dibp.appointments.util.ResponseWrapper;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import org.junit.Test;

import java.util.HashMap;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class DefaultApiCallsSenderServiceTest {
    private static final String SUCCESSFUL_RESPONSE =
            "<s:Envelope xmlns:s=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:a=\"http://www.w3.org/2005/08/addressing\">\n" +
            "   <s:Header/>\n" +
            "   <s:Body>\n" +
            "      <GetByPersonalIdResponse xmlns=\"http://www.qnomy.com/Services\">\n" +
            "         <GetByPersonalIdResult xmlns:b=\"http://schemas.datacontract.org/2004/07/QFlow.Library\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
            "            <b:Active>true</b:Active>\n" +
            "            <b:PersonalId>11111111111</b:PersonalId>\n" +
            "            <b:Id>1</b:Id>\n" +
            "            <b:Name>Lastname Firstname</b:Name>\n" +
            "            <b:EMail>noemail@test.com</b:EMail>\n" +
            "            <b:TelNumber1>0400000000</b:TelNumber1>" +
            "            <b:FirstName>Firstname</b:FirstName>\n" +
            "            <b:LastName>Lastname</b:LastName>\n" +
            "         </GetByPersonalIdResult>\n" +
            "      </GetByPersonalIdResponse>\n" +
            "   </s:Body>\n" +
            "</s:Envelope>";

    private static final String INVALID_SESSION_ID_RESPONSE =
            "<s:Envelope xmlns:s=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:a=\"http://www.w3.org/2005/08/addressing\">\n" +
                    "    <s:Header>\n" +
                    "        <a:Action s:mustUnderstand=\"1\">http://www.qnomy.com/Services/IsvcCalendar/GetDynamicSuggestedSlots2QFlowAPISecurityExceptionFault\n" +
                    "        </a:Action>\n" +
                    "        <a:RelatesTo>urn:uuid:0c7958a0-a7d7-496c-b37b-af8c5778e00f</a:RelatesTo>\n" +
                    "    </s:Header>\n" +
                    "    <s:Body>\n" +
                    "        <s:Fault>\n" +
                    "            <s:Code>\n" +
                    "                <s:Value>s:Sender</s:Value>\n" +
                    "            </s:Code>\n" +
                    "            <s:Reason>\n" +
                    "                <s:Text xml:lang=\"en-US\">The current session was terminated by the server. (Invalid Session ID)</s:Text>\n" +
                    "            </s:Reason>\n" +
                    "            <s:Detail>\n" +
                    "                <QFlowAPISecurityException xmlns=\"http://schemas.datacontract.org/2004/07/QFlow.Web.API\">\n" +
                    "                    <ErrorType>InvalidAPISessionId</ErrorType>\n" +
                    "                </QFlowAPISecurityException>\n" +
                    "            </s:Detail>\n" +
                    "        </s:Fault>\n" +
                    "    </s:Body>\n" +
                    "</s:Envelope>";

    private static final String OTHER_ERROR_MESSAGE =
            "<s:Envelope xmlns:s=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:a=\"http://www.w3.org/2005/08/addressing\">\n" +
                    "    <s:Header>\n" +
                    "        <a:Action s:mustUnderstand=\"1\">\n" +
                    "            http://www.qnomy.com/Services/IsvcCustomer/GetByPersonalIdQFlowAPIApplicationExceptionFault\n" +
                    "        </a:Action>\n" +
                    "        <a:RelatesTo>urn:uuid:f8cf6e26-2950-11e6-be23-6c40089d4690</a:RelatesTo>\n" +
                    "    </s:Header>\n" +
                    "    <s:Body>\n" +
                    "        <s:Fault>\n" +
                    "            <s:Code>\n" +
                    "                <s:Value>s:Sender</s:Value>\n" +
                    "            </s:Code>\n" +
                    "            <s:Reason>\n" +
                    "                <s:Text xml:lang=\"en-US\">Record not found</s:Text>\n" +
                    "            </s:Reason>\n" +
                    "            <s:Detail>\n" +
                    "                <QFlowAPIApplicationException xmlns=\"http://schemas.datacontract.org/2004/07/QFlow.Web.API\">\n" +
                    "                    <ErrorNumber>51010</ErrorNumber>\n" +
                    "                </QFlowAPIApplicationException>\n" +
                    "            </s:Detail>\n" +
                    "        </s:Fault>\n" +
                    "    </s:Body>\n" +
                    "</s:Envelope>";

    @Test
    public void sendsSigninThenRequestThenSignoutOnSuccess() throws Exception {
        StubApiSessionService apiSessionService = new StubApiSessionService();
        StubHttpClientAlwaysSuccessful httpClient = new StubHttpClientAlwaysSuccessful();
        ApiCallsSenderService service = new DefaultApiCallsSenderService(apiSessionService, httpClient);
        Template template = Mustache.compiler().compile("template");

        ResponseWrapper response = service.sendRequest(template, new HashMap<>(), "serviceAddress");

        assertThat(response.getCode(), equalTo(200));
        assertThat(apiSessionService.getNumberOfSessionsCreated(), equalTo(1));
        assertThat(apiSessionService.getNumberOfSessionsClosed(), equalTo(1));
    }

    @Test
    public void retriesSigninOnInvalidSessionIdResponse() throws Exception {
        StubApiSessionService apiSessionService = new StubApiSessionService();
        StubHttpClientInvalidSessionIdButEventuallySuccessful httpClient = new StubHttpClientInvalidSessionIdButEventuallySuccessful(DefaultApiCallsSenderService.MAX_ATTEMPTS);
        ApiCallsSenderService service = new DefaultApiCallsSenderService(apiSessionService, httpClient);
        Template template = Mustache.compiler().compile("");

        ResponseWrapper response = service.sendRequest(template, new HashMap<>(), "serviceAddress");

        assertThat(response.getCode(), equalTo(200));
        assertThat(response.getMessage(), equalTo(SUCCESSFUL_RESPONSE));
        assertThat(apiSessionService.getNumberOfSessionsCreated(), equalTo(DefaultApiCallsSenderService.MAX_ATTEMPTS));
        assertThat(apiSessionService.getNumberOfSessionsClosed(), equalTo(DefaultApiCallsSenderService.MAX_ATTEMPTS));
    }

    @Test
    public void givesUpAfterMaxRetriesSOnInvalidSessionIdResponse() throws Exception {
        StubApiSessionService apiSessionService = new StubApiSessionService();
        StubHttpClientInvalidSessionIdButEventuallySuccessful httpClient = new StubHttpClientInvalidSessionIdButEventuallySuccessful(DefaultApiCallsSenderService.MAX_ATTEMPTS + 1);
        ApiCallsSenderService service = new DefaultApiCallsSenderService(apiSessionService, httpClient);
        Template template = Mustache.compiler().compile("");

        ResponseWrapper response = service.sendRequest(template, new HashMap<>(), "serviceAddress");

        assertThat(response.getCode(), equalTo(500));
        assertThat(response.getMessage(), equalTo(INVALID_SESSION_ID_RESPONSE));
        assertThat(apiSessionService.getNumberOfSessionsCreated(), equalTo(DefaultApiCallsSenderService.MAX_ATTEMPTS));
        assertThat(apiSessionService.getNumberOfSessionsClosed(), equalTo(DefaultApiCallsSenderService.MAX_ATTEMPTS));
    }

    @Test(expected = ApiResponseNotSuccessfulException.class)
    public void doesNotRetrySigninOnOtherErrorResponse() throws Exception {
        StubApiSessionService apiSessionService = new StubApiSessionService();
        StubHttpClientOtherError httpClient = new StubHttpClientOtherError();
        ApiCallsSenderService service = new DefaultApiCallsSenderService(apiSessionService, httpClient);
        Template template = Mustache.compiler().compile("");

        service.sendRequest(template, new HashMap<>(), "serviceAddress");
    }

    private static class StubApiSessionService extends ApiSessionService {
        private int numberOfSessionsCreated;
        private int numberOfSessionsClosed;

        private StubApiSessionService() {
            super(null, null);
        }

        @Override
        public ApiSession createSession() {
            numberOfSessionsCreated++;
            return new ApiSession("apiSessionId", "userId");
        }

        @Override
        public void closeSession(String apiSessionId) {
            numberOfSessionsClosed++;
        }

        int getNumberOfSessionsCreated() {
            return numberOfSessionsCreated;
        }

        int getNumberOfSessionsClosed() {
            return numberOfSessionsClosed;
        }
    }

    private static class StubHttpClientAlwaysSuccessful extends HttpClient {
        @Override
        public ResponseWrapper post(String url, String messageBody, String messageId) {
            return new ResponseWrapper(200, SUCCESSFUL_RESPONSE);
        }
    }

    private class StubHttpClientInvalidSessionIdButEventuallySuccessful extends HttpClient {
        private final int numberOfFailures;
        private int numberOfRequestsSent = 0;

        StubHttpClientInvalidSessionIdButEventuallySuccessful(int numberOfFailures) {
            this.numberOfFailures = numberOfFailures;
        }

        @Override
        public ResponseWrapper post(String url, String messageBody, String messageId) {
            numberOfRequestsSent++;
            if (numberOfRequestsSent >= numberOfFailures) {
                return new ResponseWrapper(200, SUCCESSFUL_RESPONSE);
            }
            return new ResponseWrapper(500, INVALID_SESSION_ID_RESPONSE);
        }
    }

    private class StubHttpClientOtherError extends HttpClient {
        @Override
        public ResponseWrapper post(String url, String messageBody, String messageId) {
            return new ResponseWrapper(500, OTHER_ERROR_MESSAGE);
        }
    }
}
