package main.parsers.dom;

import main.constants.Constants;
import main.parsers.dom.implementation.CarsDomParser;
import main.parsers.dom.implementation.DetailsListDomParser;
import main.parsers.dom.implementation.OrdersDomParser;
import main.parsers.sax.implementation.CarsSaxParser;
import main.parsers.sax.implementation.DetailsListSaxParser;
import main.parsers.sax.implementation.OrdersSaxParser;
import main.xmlClass.cars.CarsList;
import main.xmlClass.details.DetailsList;
import main.xmlClass.orders.OrdersList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class DomMain {
    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException, DatatypeConfigurationException, TransformerException {
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = sf.newSchema(new File(Constants.XSD_SCHEMA_PATH));
        DocumentBuilder documentBuilder = DomParserUtils.generateDocumentBuilder(schema);

        DetailsListDomParser detailsListDomParser = new DetailsListDomParser();
        CarsDomParser carsDomParser = new CarsDomParser();
        OrdersDomParser ordersDomParser = new OrdersDomParser();

        DetailsList detailsList = detailsListDomParser.parse(new FileInputStream(Constants.XML_DETAILS_LIST_PATH), schema);
        CarsList carsList = carsDomParser.parse(new FileInputStream(Constants.XML_CARS_LIST_PATH), schema);
        OrdersList ordersList = ordersDomParser.parse(new FileInputStream(Constants.XML_ORDERS_LIST_PATH), schema);

        Constants.ConsoleDetailsList(detailsList);
        Constants.ConsoleCars(carsList);
        Constants.ConsoleOrders(ordersList);

        detailsListDomParser.write(detailsList, new File(Constants.XML_DETAILS_LIST_PATH + "Dom.xml"), documentBuilder);
        carsDomParser.write(carsList, new File(Constants.XML_CARS_LIST_PATH + "Dom.xml"), documentBuilder);
        ordersDomParser.write(ordersList, new File(Constants.XML_ORDERS_LIST_PATH + "Dom.xml"), documentBuilder);
    }
}
