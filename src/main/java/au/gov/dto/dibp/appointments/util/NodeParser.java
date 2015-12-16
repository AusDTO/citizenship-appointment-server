package au.gov.dto.dibp.appointments.util;

import org.w3c.dom.Node;

import javax.xml.xpath.*;

public class NodeParser {

    private final Node node;

    public NodeParser(Node node) {
        this.node = node;
    }

    public String getStringAttribute(String thepath) throws XPathExpressionException {
        return getXpath().evaluate(thepath, this.node);
    }

    public int getIntegerAttribute(String thepath) throws XPathExpressionException {
       return Integer.parseInt(getStringAttribute(thepath));
    }

    private XPath getXpath() {
        return XPathFactory.newInstance().newXPath();
    }
}
