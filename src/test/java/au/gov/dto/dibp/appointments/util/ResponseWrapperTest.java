package au.gov.dto.dibp.appointments.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class ResponseWrapperTest {

    @Test
    public void getStringAttribute_shouldReturnStringValueOfXpath() {
        ResponseWrapper parser = new ResponseWrapper(0, getBasicResponseBody());
        assertThat(parser.getStringAttribute("//c"), is("Some C Node text here"));
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

    private String getBasicResponseBody(){
       return
            "<a>" +
                "<b>"+
                    "<b1>B1 node text</b1>" +
                "</b>" +
                "<c>Some C Node text here</c>" +
                "<d>"+
                    "<e>List Elem 1</e>"+
                    "<e>List Elem 2</e>"+
                    "<e>List Elem 3</e>"+
                "</d>" +
            "</a>";
    }
}
