package main.parsers.dom.implementation;

import com.sun.org.apache.xpath.internal.operations.Or;
import main.constants.Constants;
import main.parsers.dom.DomParserUtils;
import main.xmlClass.cars.Car;
import main.xmlClass.cars.CarsList;
import main.xmlClass.cars.Engine;
import main.xmlClass.orders.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.XMLConstants;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


public class OrdersDomParser {
    private static final String ATTR_ID = "id";

    private static final String TAG_ORDER = "Order";

    private static final String TAG_ORDER_INFO = "OrderInfo";
    private static final String TAG_DATE = "Date";
    private static final String TAG_STATUS = "Status";
    private static final String TAG_TOTAL_PRICE = "TotalPrice";

    private static final String TAG_USER_INFO = "UserInfo";
    private static final String TAG_NAME = "Name";
    private static final String TAG_GENDER = "Gender";
    private static final String TAG_EMAIL = "Email";
    private static final String TAG_BIRTHDATE = "BirthDate";
    private static final String TAG_CITY = "City";

    private static final String TAG_SHOPPING_CART = "ShoppingCart";
    private static final String TAG_SHOPPING_CART_ITEM = "ShoppingCartItem";
    private static final String TAG_TITLE = "Title";
    private static final String TAG_PRICE = "Price";
    private static final String TAG_COUNT = "Count";

    private static final String XSD_SCHEMA_PATH = "src/main/resources/AutoShop.xsd";
    private static final String XML_ORDERS_LIST_PATH = "src/main/resources/Orders.xml";

    private ShoppingCartItem parseSCI (Node node) {
        ShoppingCartItem shoppingCartItem = new ShoppingCartItem();
        NodeList nodes = node.getChildNodes();

        if (node.hasAttributes()) {
            NamedNodeMap attrs = node.getAttributes();
            Node item = attrs.getNamedItem(ATTR_ID);
            shoppingCartItem.setId(new BigInteger(item.getTextContent()));
        }

        for (int i = 0; i < nodes.getLength(); i++) {
            Node item = nodes.item(i);
            if (TAG_TITLE.equals(item.getLocalName())){
                shoppingCartItem.setTitle(item.getTextContent());
            }
            else if(TAG_PRICE.equals(item.getLocalName())) {
                shoppingCartItem.setPrice(new BigDecimal(item.getTextContent()));
            }
            else if(TAG_COUNT.equals(item.getLocalName())) {
                shoppingCartItem.setCount(new BigInteger(item.getTextContent()));
            }
        }
        return shoppingCartItem;
    }

    private ShoppingCart parseShoppingCart (Node node) {
        ShoppingCart shoppingCart = new ShoppingCart();
        NodeList nodes = node.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node item = nodes.item(i);
            if(TAG_SHOPPING_CART_ITEM.equals(item.getLocalName())){
                ShoppingCartItem shoppingCartItem = parseSCI(item);
                shoppingCart.getShoppingCartItem().add(shoppingCartItem);
            }
        }
        return shoppingCart;
    }

    private UserInfo parseUserInfo (Node node) throws DatatypeConfigurationException {
        UserInfo userInfo = new UserInfo();
        NodeList nodes = node.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node item = nodes.item(i);
            if(TAG_NAME.equals(item.getLocalName())){
                userInfo.setName(item.getTextContent());
            }
            else if(TAG_GENDER.equals(item.getLocalName())){
                userInfo.setGender(Gender.fromValue(item.getTextContent()));
            }
            else if(TAG_EMAIL.equals(item.getLocalName())){
                userInfo.setEmail(item.getTextContent());
            }
            else if(TAG_BIRTHDATE.equals(item.getLocalName())){
                XMLGregorianCalendar result = DatatypeFactory.newInstance().newXMLGregorianCalendar(item.getTextContent());
                userInfo.setBirthDate(result);
            }
            else if(TAG_CITY.equals(item.getLocalName())){
                userInfo.setCity(item.getTextContent());
            }
        }
        return userInfo;
    }

    private OrderInfo parseOrderInfo (Node node) throws DatatypeConfigurationException {
        OrderInfo orderInfo = new OrderInfo();
        NodeList nodes = node.getChildNodes();

        for (int i = 0; i < nodes.getLength(); i++) {
            Node item = nodes.item(i);
            if(TAG_DATE.equals(item.getLocalName())){
                XMLGregorianCalendar date = DatatypeFactory.newInstance().newXMLGregorianCalendar(item.getTextContent());
                orderInfo.setDate(date);
            }
            else if(TAG_STATUS.equals(item.getLocalName())){
                orderInfo.setStatus(OrderStatus.fromValue(item.getTextContent()));
            }
            else if(TAG_TOTAL_PRICE.equals(item.getLocalName())){
                orderInfo.setTotalPrice(new BigDecimal(item.getTextContent()));
            }
        }
        return orderInfo;
    }

    private Order parseOrder (Node node) throws DatatypeConfigurationException {
        Order order = new Order();
        NodeList nodes = node.getChildNodes();

        if (node.hasAttributes()) {
            NamedNodeMap attrs = node.getAttributes();
            Node item = attrs.getNamedItem(ATTR_ID);
            order.setId(new BigInteger(item.getTextContent()));
        }

        for (int i = 0; i < nodes.getLength(); i++) {
            Node item = nodes.item(i);
            if (TAG_ORDER_INFO.equals(item.getLocalName())) {
                order.setOrderInfo(parseOrderInfo(item));
            } else if (TAG_USER_INFO.equals(item.getLocalName())) {
                order.setUserInfo(parseUserInfo(item));
            } else if (TAG_SHOPPING_CART.equals(item.getLocalName())) {
                order.setShoppingCart(parseShoppingCart(item));
            }
        }
        return order;
    }

    public OrdersList parse (InputStream in, Schema schema) throws ParserConfigurationException, IOException, SAXException, DatatypeConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);

        dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        dbf.setSchema(schema);
        DocumentBuilder db = dbf.newDocumentBuilder();

        db.setErrorHandler(new DefaultHandler() {
            @Override
            public void error(SAXParseException e) throws SAXException {
                System.err.println(e.getMessage()); // log error
                throw e;
            }
        });

        Document root = db.parse(in);

        OrdersList ordersList = new OrdersList();

        Element element = root.getDocumentElement();
        NodeList xmlDetails = element.getElementsByTagName(TAG_ORDER);
        for (int i = 0; i < xmlDetails.getLength(); i++) {
            ordersList.getOrder().add(parseOrder(xmlDetails.item(i)));
        }
        return ordersList;
    }

    public void write (OrdersList ordersList, File file, DocumentBuilder documentBuilder) throws TransformerException {
        Document document = documentBuilder.newDocument();
        Element rootElement = document.createElement("tns:OrdersList");
        rootElement.setAttribute("xmlns:tns", "http://autoshop.com/details");
        document.appendChild(rootElement);
        for (Order order : ordersList.getOrder()) {
            Element orderEl = document.createElement(Constants.TAG_ORDER);
            Element orderInfoEl = document.createElement(Constants.TAG_ORDER_INFO);
            OrderInfo orderInfo = order.getOrderInfo();
            Element userInfoEl = document.createElement(Constants.TAG_USER_INFO);
            UserInfo userInfo = order.getUserInfo();
            Element shoppingCartEl = document.createElement(Constants.TAG_SHOPPING_CART);
            ShoppingCart shoppingCart = order.getShoppingCart();

            orderEl.setAttribute(Constants.ATTR_ID, order.getId().toString());

            orderInfoEl.appendChild(DomParserUtils.createElement(document, Constants.TAG_DATE, orderInfo.getDate().toString()));
            orderInfoEl.appendChild(DomParserUtils.createElement(document, Constants.TAG_STATUS, orderInfo.getStatus().value()));
            orderInfoEl.appendChild(DomParserUtils.createElement(document, Constants.TAG_TOTAL_PRICE, orderInfo.getTotalPrice().toString()));

            userInfoEl.appendChild(DomParserUtils.createElement(document, Constants.TAG_NAME, userInfo.getName()));
            userInfoEl.appendChild(DomParserUtils.createElement(document, Constants.TAG_GENDER, userInfo.getGender().value()));
            userInfoEl.appendChild(DomParserUtils.createElement(document, Constants.TAG_EMAIL, userInfo.getEmail()));
            userInfoEl.appendChild(DomParserUtils.createElement(document, Constants.TAG_BIRTHDATE, userInfo.getBirthDate().toString()));
            userInfoEl.appendChild(DomParserUtils.createElement(document, Constants.TAG_CITY, userInfo.getCity()));

            for(ShoppingCartItem shoppingCartItem : shoppingCart.getShoppingCartItem()) {
                Element shoppingCartItemEl = document.createElement(Constants.TAG_SHOPPING_CART_ITEM);
                shoppingCartItemEl.setAttribute(Constants.ATTR_ID, shoppingCartItem.getId().toString());
                shoppingCartItemEl.appendChild(DomParserUtils.createElement(document, Constants.TAG_TITLE, shoppingCartItem.getTitle()));
                shoppingCartItemEl.appendChild(DomParserUtils.createElement(document, Constants.TAG_PRICE, shoppingCartItem.getPrice().toString()));
                shoppingCartItemEl.appendChild(DomParserUtils.createElement(document, Constants.TAG_COUNT, shoppingCartItem.getCount().toString()));
                shoppingCartEl.appendChild(shoppingCartItemEl);
            }

            orderEl.appendChild(orderInfoEl);
            orderEl.appendChild(userInfoEl);
            orderEl.appendChild(shoppingCartEl);
            rootElement.appendChild(orderEl);
        }
        DomParserUtils.convertToXML(document, file);
    }
}
