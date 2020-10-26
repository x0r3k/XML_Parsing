package main.parsers.sax.implementation;

import main.constants.Constants;
import main.xmlClass.cars.Car;
import main.xmlClass.cars.CarsList;
import main.xmlClass.cars.Engine;
import main.xmlClass.orders.*;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;

public class OrdersSaxParser extends DefaultHandler {

    private String currentTag;

    private OrdersList ordersList;
    private Order order;
    private OrderInfo orderInfo;
    private UserInfo userInfo;
    private ShoppingCart shoppingCart;
    private ShoppingCartItem shoppingCartItem;

    public void error(org.xml.sax.SAXParseException e) throws SAXException {
        throw e; // throw exception if xml not valid
    }

    public OrdersList parseXML(InputStream in) throws ParserConfigurationException, SAXException, IOException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        SAXParser saxParser = factory.newSAXParser();
        saxParser.parse(in, this);
        return ordersList;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
//        System.out.println("start_" + localName);
        currentTag = localName;
        if (Constants.TAG_ORDERS_LIST.equals(localName)) {
            ordersList = new OrdersList();
        }
        else if (Constants.TAG_ORDER.equals(localName)) {
            order = new Order();
            if (attributes.getLength() > 0){
                order.setId(new BigInteger(attributes.getValue("id")));
            }
        }
        else if (Constants.TAG_ORDER_INFO.equals(localName)) {
            orderInfo = new OrderInfo();
        }
        else if (Constants.TAG_USER_INFO.equals(localName)) {
            userInfo = new UserInfo();
        }
        else if (Constants.TAG_SHOPPING_CART.equals(localName)) {
            shoppingCart = new ShoppingCart();
        }
        else if (Constants.TAG_SHOPPING_CART_ITEM.equals(localName)) {
            shoppingCartItem = new ShoppingCartItem();
            if (attributes.getLength() > 0){
                shoppingCartItem.setId(new BigInteger(attributes.getValue("id")));
            }
        }
    }

    public void characters(char[] ch, int start, int length) {
//        System.out.println("characters_" + new String(ch, start, length));
        if (Constants.TAG_DATE.equals(currentTag)) {
            XMLGregorianCalendar result = null;
            try {
                result = DatatypeFactory.newInstance().newXMLGregorianCalendar(new String(ch, start, length));
            } catch (DatatypeConfigurationException e) {
                e.printStackTrace();
            }
            orderInfo.setDate(result);
        }
        else if (Constants.TAG_STATUS.equals(currentTag)) {
            orderInfo.setStatus(OrderStatus.fromValue(new String(ch, start, length)));
        }
        else if (Constants.TAG_TOTAL_PRICE.equals(currentTag)) {
            orderInfo.setTotalPrice(new BigDecimal(new String(ch, start, length)));
        }
        else if (Constants.TAG_NAME.equals(currentTag)) {
            userInfo.setName(new String(ch, start, length));
        }
        else if (Constants.TAG_GENDER.equals(currentTag)) {
            userInfo.setGender(Gender.fromValue(new String(ch, start, length)));
        }
        else if (Constants.TAG_CITY.equals(currentTag)) {
            userInfo.setCity(new String(ch, start, length));
        }
        else if (Constants.TAG_BIRTHDATE.equals(currentTag)) {
            XMLGregorianCalendar result = null;
            try {
                result = DatatypeFactory.newInstance().newXMLGregorianCalendar(new String(ch, start, length));
            } catch (DatatypeConfigurationException e) {
                e.printStackTrace();
            }
            userInfo.setBirthDate(result);
        }
        else if (Constants.TAG_EMAIL.equals(currentTag)) {
            userInfo.setEmail(new String(ch, start, length));
        }
        else if (Constants.TAG_TITLE.equals(currentTag)) {
            shoppingCartItem.setTitle(new String(ch, start, length));
        }
        else if (Constants.TAG_PRICE.equals(currentTag)) {
            shoppingCartItem.setPrice(new BigDecimal(new String(ch, start, length)));
        }
        else if (Constants.TAG_COUNT.equals(currentTag)) {
            shoppingCartItem.setCount(new BigInteger(new String(ch, start, length)));
        }
        currentTag = null;
    }

    public void endElement(String uri, String localName, String qName) {
//        System.out.println("end_" + localName);
        if (Constants.TAG_ORDER_INFO.equals(localName)) {
            order.setOrderInfo(orderInfo);
        }
        else if(Constants.TAG_USER_INFO.equals(localName)) {
            order.setUserInfo(userInfo);
        }
        else if(Constants.TAG_SHOPPING_CART.equals(localName)) {
            order.setShoppingCart(shoppingCart);
        }
        else if(Constants.TAG_SHOPPING_CART_ITEM.equals(localName)) {
            shoppingCart.getShoppingCartItem().add(shoppingCartItem);
        }
        else if(Constants.TAG_ORDER.equals(localName)) {
            ordersList.getOrder().add(order);
        }
    }

    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
        System.out.println("START OF ORDERS PARSE");
        OrdersSaxParser parser = new OrdersSaxParser();
        parser.parseXML(new FileInputStream(Constants.XML_ORDERS_LIST_PATH));
        OrdersList ordersList = parser.ordersList;
        ordersList.getOrder().forEach(order -> {
            System.out.println(order.getOrderInfo().getStatus());
        });
        System.out.println("END OF ORDERS PARSE\n");
    }
}
