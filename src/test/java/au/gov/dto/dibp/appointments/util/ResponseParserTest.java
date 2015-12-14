package au.gov.dto.dibp.appointments.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

@RunWith(MockitoJUnitRunner.class)
public class ResponseParserTest {

    @Test
    public void getStringAttribute_shouldReturnStringValueOfXpath(){
        try {
            ResponseParser parser = new ResponseParser(getBasicResponseBody());
            assertThat(parser.getStringAttribute("//c"), is("Some C Node text here"));
        } catch (ParserConfigurationException | IOException | SAXException e) {
            fail(e.getMessage());
        } catch (XPathExpressionException e) {
            fail("XPath expression failed");
        }
    }

    @Test
    public void getNodeAttribute_shouldReturnNodeMatchingXpath(){
        try {

            ResponseParser parser = new ResponseParser(getBasicResponseBody());
            Node nodeB = parser.getNodeAttribute("//b");
            assertThat(nodeB.getNodeName(), is("b"));
            assertThat(nodeB.getChildNodes().getLength(), is(1));

        } catch (ParserConfigurationException | IOException | SAXException e) {
            fail(e.getMessage());
        } catch (XPathExpressionException e) {
            fail("XPath expression failed");
        }
    }

    @Test
    public void getNodeListAttribute_shouldReturnNodesListMatchingXpath(){
        try {

            ResponseParser parser = new ResponseParser(getBasicResponseBody());
            NodeList nodeList = parser.getNodeListAttribute("//d/e");
            assertThat(nodeList.getLength(), is(3));
            assertThat(nodeList.item(0).getTextContent(), is("List Elem 1"));

        } catch (ParserConfigurationException | IOException | SAXException e) {
            fail(e.getMessage());
        } catch (XPathExpressionException e) {
            fail("XPath expression failed");
        }
    }

    private InputStream getBasicResponseBody(){
        String document =
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
        return new ByteArrayInputStream(document.getBytes(StandardCharsets.UTF_8));
    }
}
