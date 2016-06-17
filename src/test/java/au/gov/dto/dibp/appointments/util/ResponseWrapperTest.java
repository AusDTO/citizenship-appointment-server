package au.gov.dto.dibp.appointments.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class ResponseWrapperTest {
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

    private static final String FAULT_BUT_NOT_INVALID_SESSION_ID_RESPONSE =
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
    public void getStringAttribute_shouldReturnStringValueOfXpath() {
        ResponseWrapper parser = new ResponseWrapper(0, getBasicResponseBody());
        assertThat(parser.getStringAttribute("//c"), is("Some C Node text here"));
    }

    @Test
    public void getStringAttribute_shouldReturnStringValueOfXpathWithoutNewlines() {
        ResponseWrapper parser = new ResponseWrapper(0, getBasicResponseBody());
        assertThat(parser.getStringAttribute("//f"), is("Some Unit Address With Newline"));
    }

    @Test
    public void getStringAttribute_shouldReturnStringValueOfXpathWithoutCarriageReturns() {
        ResponseWrapper parser = new ResponseWrapper(0, getBasicResponseBody());
        assertThat(parser.getStringAttribute("//g"), is("Some Unit Address With Carriage Return"));
    }

    @Test
    public void getNodeAttribute_shouldReturnNodeMatchingXpath() {
        ResponseWrapper parser = new ResponseWrapper(0, getBasicResponseBody());
        Node nodeB = parser.getNodeAttribute("//b");
        assertThat(nodeB.getNodeName(), is("b"));
        assertThat(nodeB.getChildNodes().getLength(), is(1));
    }

    @Test
    public void getNodeListAttribute_shouldReturnNodesListMatchingXpath() {
        ResponseWrapper parser = new ResponseWrapper(0, getBasicResponseBody());
        NodeList nodeList = parser.getNodeListAttribute("//d/e");
        assertThat(nodeList.getLength(), is(3));
        assertThat(nodeList.item(0).getTextContent(), is("List Elem 1"));
    }

    @Test
    public void isInvalidSessionIdTrue() throws Exception {
        ResponseWrapper wrapper = new ResponseWrapper(0, INVALID_SESSION_ID_RESPONSE);
        assertThat(wrapper.isInvalidSessionId(), equalTo(true));
    }

    @Test
    public void isInvalidSessionIdFalse() throws Exception {
        ResponseWrapper wrapper = new ResponseWrapper(0, FAULT_BUT_NOT_INVALID_SESSION_ID_RESPONSE);
        assertThat(wrapper.isInvalidSessionId(), equalTo(false));
    }

    private String getBasicResponseBody() {
        return
                "<a>" +
                        "<b>" +
                        "<b1>B1 node text</b1>" +
                        "</b>" +
                        "<c>Some C Node text here</c>" +
                        "<d>" +
                        "<e>List Elem 1</e>" +
                        "<e>List Elem 2</e>" +
                        "<e>List Elem 3</e>" +
                        "</d>" +
                        "<f>Some Unit\nAddress With\nNewline</f>" +
                        "<g>Some Unit\rAddress With\rCarriage Return</g>" +
                        "</a>";
    }
}
