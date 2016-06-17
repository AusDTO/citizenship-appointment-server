package au.gov.dto.dibp.appointments.util;

import org.junit.Test;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class NodeParserTest {
    @Test
    public void shouldSanitizeCarriageReturn() throws Exception {
        String response =
                "<s:Body>\n" +
                "      <GetResponse xmlns=\"http://www.qnomy.com/Services\">\n" +
                "         <GetResult xmlns:b=\"http://schemas.datacontract.org/2004/07/QFlow.Library\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
                "            <b:Active>true</b:Active>\n" +
                "            <b:Description/>\n" +
                "            <b:ExtRef/>\n" +
                "            <b:Id>5</b:Id>\n" +
                "            <b:Name>Sydney</b:Name>\n" +
                "            <b:ActiveDirectoryOU/>\n" +
                "            <b:Address>Level 9, 81 George Street\r\nSydney NSW 2000</b:Address>\n" +
                "            <b:CustomerPatience>0</b:CustomerPatience>\n" +
                "            <b:Level>2</b:Level>\n" +
                "            <b:MaxSeats>0</b:MaxSeats>\n" +
                "            <b:ParentUnitId>2</b:ParentUnitId>\n" +
                "            <b:Path>\\Test\\NSW\\Sydney</b:Path>\n" +
                "            <b:ServiceTime>0</b:ServiceTime>\n" +
                "            <b:SubTreeActive>True</b:SubTreeActive>\n" +
                "            <b:TelNumber/>\n" +
                "            <b:TimeZoneId>51</b:TimeZoneId>\n" +
                "            <b:TypeId>3</b:TypeId>\n" +
                "            <b:WaitingTimeTarget>0</b:WaitingTimeTarget>\n" +
                "            <b:WorkingHoursId>1</b:WorkingHoursId>\n" +
                "         </GetResult>\n" +
                "      </GetResponse>\n" +
                "   </s:Body>";

        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = domFactory.newDocumentBuilder();
        Document responseBody = builder.parse(new ByteArrayInputStream(response.getBytes(StandardCharsets.UTF_8)));

        NodeParser nodeParser = new NodeParser(responseBody);
        String address = nodeParser.getStringAttribute("//GetResponse/GetResult/Address");
        assertThat(address, equalTo("Level 9, 81 George Street Sydney NSW 2000"));
    }

}
