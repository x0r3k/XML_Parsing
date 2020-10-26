package main.parsers.jaxb;

import main.constants.Constants;
import main.parsers.jaxb.implementation.JaxbParser;
import main.xmlClass.cars.CarsList;
import main.xmlClass.details.DetailsList;
import main.xmlClass.orders.OrdersList;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;

public class JaxbMain {

    public static void main(String[] args) throws JAXBException, SAXException {
        JaxbParser jaxbParser = new JaxbParser();

        DetailsList detailsList = jaxbParser.parse(Constants.XML_DETAILS_LIST_PATH);
        CarsList carsList = jaxbParser.parse(Constants.XML_CARS_LIST_PATH);
        OrdersList ordersList = jaxbParser.parse(Constants.XML_ORDERS_LIST_PATH);

        Constants.ConsoleDetailsList(detailsList);
        Constants.ConsoleCars(carsList);
        Constants.ConsoleOrders(ordersList);

        try {
            jaxbParser.save(detailsList, Constants.XML_DETAILS_LIST_PATH + ".jaxb.xml");
            jaxbParser.save(carsList, Constants.XML_CARS_LIST_PATH + ".jaxb.xml");
            jaxbParser.save(ordersList, Constants.XML_ORDERS_LIST_PATH + ".jaxb.xml");
        } catch (Exception ex) {
            System.err.println("====================================");
            System.err.println("Object tree not valid against XSD.");
            System.err.println(ex.getClass().getName());
            System.err.println("====================================");
        }
    }
}
