package au.gov.dto.dibp.appointments.util;

import org.w3c.dom.Node;

import javax.xml.xpath.*;

public class NodeParser {

    private final Node node;

    public NodeParser(Node node) {
        this.node = node;
    }

    public String getStringAttribute(String thepath) {
        try {
            String nodeValue = getXpath().evaluate(thepath, this.node);
            return nodeValue.replaceAll("\\n"," ");
        } catch (XPathExpressionException e) {
            throw new RuntimeException("Error evaluating Xpath: " + thepath, e);
        }
    }

    public int getIntegerAttribute(String thepath) {
       return Integer.parseInt(getStringAttribute(thepath));
    }

    private XPath getXpath() {
        return XPathFactory.newInstance().newXPath();
    }
}
