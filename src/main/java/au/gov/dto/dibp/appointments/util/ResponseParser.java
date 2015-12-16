package au.gov.dto.dibp.appointments.util;

import com.squareup.okhttp.Response;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.IOException;
import java.io.InputStream;

public class ResponseParser {

    private Document responseBody;

    public ResponseParser(InputStream stream) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = domFactory.newDocumentBuilder();
        responseBody = builder.parse(stream);
    }

    public String getStringAttribute(String thepath) throws XPathExpressionException {
        XPathExpression expr = getXpath().compile(thepath);
        return (String) expr.evaluate(this.responseBody, XPathConstants.STRING);
    }

    public int getIntegerAttribute(String thepath) throws XPathExpressionException {
        XPathExpression expr = getXpath().compile(thepath);
        return ((Double)expr.evaluate(this.responseBody, XPathConstants.NUMBER)).intValue();
    }

    public NodeList getNodeListAttribute(String thepath) throws XPathExpressionException {
        XPathExpression expr = getXpath().compile(thepath);
        return (NodeList) expr.evaluate(this.responseBody, XPathConstants.NODESET);
    }

    public Node getNodeAttribute(String thepath) throws XPathExpressionException {
        XPathExpression expr = getXpath().compile(thepath);
        return (Node) expr.evaluate(this.responseBody, XPathConstants.NODE);
    }

    private XPath getXpath(){
        return XPathFactory.newInstance().newXPath();
    }
}
