package main.constants;

import main.xmlClass.cars.CarsList;
import main.xmlClass.details.DetailsList;
import main.xmlClass.orders.OrdersList;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public final class Constants {
    //details constants
    public static final String ATTR_ID = "id";
    public static final String TAG_TITLE = "Title";
    public static final String TAG_PRICE = "Price";
    public static final String TAG_DETAIL = "Detail";
    public static final String TAG_DETAILS_LIST = "DetailsList";

    //car constants
    public static final String TAG_CYLINDER = "Cylinder";
    public static final String TAG_CAPACITY = "Capacity";
    public static final String TAG_POWER = "Power";
    public static final String TAG_BRAND = "Brand";
    public static final String TAG_MODEL = "Model";
    public static final String TAG_YEAR = "Year";
    public static final String TAG_ENGINE = "Engine";
    public static final String TAG_CAR = "Car";
    public static final String TAG_CARS_LIST = "CarsList";

    //order constants
    public static final String TAG_ORDERS_LIST = "OrdersList";
    public static final String TAG_ORDER = "Order";
    public static final String TAG_ORDER_INFO = "OrderInfo";
    public static final String TAG_DATE = "Date";
    public static final String TAG_STATUS = "Status";
    public static final String TAG_TOTAL_PRICE = "TotalPrice";
    public static final String TAG_USER_INFO = "UserInfo";
    public static final String TAG_NAME = "Name";
    public static final String TAG_GENDER = "Gender";
    public static final String TAG_EMAIL = "Email";
    public static final String TAG_BIRTHDATE = "BirthDate";
    public static final String TAG_CITY = "City";
    public static final String TAG_SHOPPING_CART = "ShoppingCart";
    public static final String TAG_SHOPPING_CART_ITEM = "ShoppingCartItem";
    public static final String TAG_COUNT = "Count";
    public static final Class<?> OBJECT_FACTORY = main.xmlClass.ObjectFactory.class;

    //file paths
    public static final String XML_CARS_LIST_PATH = "src/main/resources/Cars.xml";
    public static final String XSD_SCHEMA_PATH = "src/main/resources/AutoShop.xsd";
    public static final String XML_DETAILS_LIST_PATH = "src/main/resources/DetailsList.xml";
    public static final String XML_ORDERS_LIST_PATH = "src/main/resources/Orders.xml";

    public static void log(String text) {
        System.out.println(text);
    }

    public static void ConsoleDetailsList(DetailsList detailsList) {
        log("START OF DETAILS PARSE");
        log(detailsList.toString());
        log("END OF DETAILS PARSE\n");
    }

    public static void ConsoleCars(CarsList carsList) {
        log("START OF CARS PARSE");
        log(carsList.toString());
        log("END OF CARS PARSE\n");
    }

    public static void ConsoleOrders(OrdersList ordersList) {
        log("START OF ORDERS PARSE");
        log(ordersList.toString());
        log("END OF ORDERS PARSE\n");
    }

    public static void createElement(XMLStreamWriter xmlStreamWriter, String elementName, String data) throws XMLStreamException {
        xmlStreamWriter.writeStartElement(elementName);
        xmlStreamWriter.writeCharacters(data);
        xmlStreamWriter.writeEndElement();
    }

}
