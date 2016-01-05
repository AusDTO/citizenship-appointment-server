package au.gov.dto.dibp.appointments.util;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ResponseWrapper {

    private final int code;
    private final String message;
    private Document responseBody;

    public ResponseWrapper(int code, String stringMessage) {
        this.code = code;
        this.message = stringMessage;
        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = domFactory.newDocumentBuilder();
            responseBody = builder.parse(new ByteArrayInputStream(stringMessage.getBytes(StandardCharsets.UTF_8)));
        } catch (ParserConfigurationException|SAXException|IOException e) {
            throw new RuntimeException("Error parsing SOAP response", e);
        }
    }

    public String getStringAttribute(String thepath) {
        try {
            XPathExpression expr = getXpath().compile(thepath);
            return (String) expr.evaluate(this.responseBody, XPathConstants.STRING);
        } catch (XPathExpressionException e) {
            throw new RuntimeException("Error evaluating Xpath: " + thepath, e);
        }
    }

    public NodeList getNodeListAttribute(String thepath) {
        try {
            XPathExpression expr = getXpath().compile(thepath);
            return (NodeList) expr.evaluate(this.responseBody, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            throw new RuntimeException("Error evaluating Xpath: " + thepath, e);
        }
    }

    public Node getNodeAttribute(String thepath) {
        try {
            XPathExpression expr = getXpath().compile(thepath);
            return (Node) expr.evaluate(this.responseBody, XPathConstants.NODE);
        } catch (XPathExpressionException e) {
            throw new RuntimeException("Error evaluating Xpath: " + thepath, e);
        }
    }

    private XPath getXpath(){
        return XPathFactory.newInstance().newXPath();
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
