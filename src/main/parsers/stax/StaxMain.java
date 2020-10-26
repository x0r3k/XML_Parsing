package main.parsers.stax;

import main.constants.Constants;
import main.parsers.stax.implementation.CarsStaxParser;
import main.parsers.stax.implementation.DetailsListStaxParser;
import main.parsers.stax.implementation.OrdersStaxParser;
import main.xmlClass.cars.CarsList;
import main.xmlClass.details.DetailsList;
import main.xmlClass.orders.OrdersList;

import java.io.File;
import java.io.FileInputStream;

public class StaxMain {
    public static void main(String[] args) throws Exception {
        DetailsListStaxParser detailsListStaxParser = new DetailsListStaxParser();
        CarsStaxParser carsStaxParser = new CarsStaxParser();
        OrdersStaxParser ordersStaxParser = new OrdersStaxParser();

        DetailsList detailsList = detailsListStaxParser.parse(new FileInputStream(Constants.XML_DETAILS_LIST_PATH));
        CarsList carsList = carsStaxParser.parse(new FileInputStream(Constants.XML_CARS_LIST_PATH));
        OrdersList ordersList = ordersStaxParser.parse(new FileInputStream(Constants.XML_ORDERS_LIST_PATH));

        Constants.ConsoleDetailsList(detailsList);
        Constants.ConsoleCars(carsList);
        Constants.ConsoleOrders(ordersList);

        try {
            detailsListStaxParser.write(detailsList, new File(Constants.XML_DETAILS_LIST_PATH + ".Stax.xml"));
            carsStaxParser.write(carsList, new File(Constants.XML_CARS_LIST_PATH + ".Stax.xml"));
            ordersStaxParser.write(ordersList, new File(Constants.XML_ORDERS_LIST_PATH + ".Stax.xml"));
        } catch (Exception ex) {
            System.err.println("====================================");
            System.err.println("Object tree not valid against XSD.");
            System.err.println(ex.getClass().getName());
            System.err.println("====================================");
        }
    }
}
