package main.parsers.stax.implementation;

import main.constants.Constants;
import main.xmlClass.cars.Car;
import main.xmlClass.cars.CarsList;
import main.xmlClass.cars.Engine;
import main.xmlClass.details.DetailsList;
import main.xmlClass.orders.*;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.stream.*;
import javax.xml.stream.events.*;
import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;

public class OrdersStaxParser {

    private String currentTag;

    private OrdersList ordersList;
    private Order order;
    private OrderInfo orderInfo;
    private UserInfo userInfo;
    private ShoppingCart shoppingCart;
    private ShoppingCartItem shoppingCartItem;

    public OrdersList parse(InputStream in) throws XMLStreamException {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        factory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, true);
        XMLEventReader eventReader = factory.createXMLEventReader(in);

        while (eventReader.hasNext()) {
            XMLEvent event;
            try {
                event = eventReader.nextEvent();
                if (event.isCharacters() && event.asCharacters().isWhiteSpace()) {
                    continue;
                }
            } catch (Exception e) {
                System.err.println(e.getMessage());
                break;
            }

            switch (event.getEventType()) {
                case XMLStreamConstants.START_ELEMENT:
                    StartElement startElement = event.asStartElement();
                    currentTag = startElement.getName().getLocalPart();
                    if (Constants.TAG_ORDERS_LIST.equals(currentTag)) {
                        ordersList = new OrdersList();
                    }
                    else if (Constants.TAG_ORDER.equals(currentTag)) {
                        order = new Order();
                        Attribute attr = startElement.getAttributeByName(new QName(Constants.ATTR_ID));
                        if (attr != null) {
                            order.setId(new BigInteger(attr.getValue()));
                        }
                    }
                    else if (Constants.TAG_ORDER_INFO.equals(currentTag)) {
                        orderInfo = new OrderInfo();
                    }
                    else if (Constants.TAG_USER_INFO.equals(currentTag)) {
                        userInfo = new UserInfo();
                    }
                    else if (Constants.TAG_SHOPPING_CART.equals(currentTag)) {
                        shoppingCart = new ShoppingCart();
                    }
                    else if (Constants.TAG_SHOPPING_CART_ITEM.equals(currentTag)) {
                        shoppingCartItem = new ShoppingCartItem();
                        Attribute attr = startElement.getAttributeByName(new QName(Constants.ATTR_ID));
                        if (attr != null) {
                            shoppingCartItem.setId(new BigInteger(attr.getValue()));
                        }
                    }
                    break;
                case XMLStreamConstants.CHARACTERS:
                    Characters characters = event.asCharacters();
                    if (Constants.TAG_DATE.equals(currentTag)) {
                        XMLGregorianCalendar result = null;
                        try {
                            result = DatatypeFactory.newInstance().newXMLGregorianCalendar(characters.getData());
                        } catch (DatatypeConfigurationException e) {
                            e.printStackTrace();
                        }
                        orderInfo.setDate(result);
                    }
                    else if (Constants.TAG_STATUS.equals(currentTag)) {
                        orderInfo.setStatus(OrderStatus.fromValue(characters.getData()));
                    }
                    else if (Constants.TAG_TOTAL_PRICE.equals(currentTag)) {
                        orderInfo.setTotalPrice(new BigDecimal(characters.getData()));
                    }
                    else if (Constants.TAG_NAME.equals(currentTag)) {
                        userInfo.setName(characters.getData());
                    }
                    else if (Constants.TAG_GENDER.equals(currentTag)) {
                        userInfo.setGender(Gender.fromValue(characters.getData()));
                    }
                    else if (Constants.TAG_CITY.equals(currentTag)) {
                        userInfo.setCity(characters.getData());
                    }
                    else if (Constants.TAG_BIRTHDATE.equals(currentTag)) {
                        XMLGregorianCalendar result = null;
                        try {
                            result = DatatypeFactory.newInstance().newXMLGregorianCalendar(characters.getData());
                        } catch (DatatypeConfigurationException e) {
                            e.printStackTrace();
                        }
                        userInfo.setBirthDate(result);
                    }
                    else if (Constants.TAG_EMAIL.equals(currentTag)) {
                        userInfo.setEmail(characters.getData());
                    }
                    else if (Constants.TAG_TITLE.equals(currentTag)) {
                        shoppingCartItem.setTitle(characters.getData());
                    }
                    else if (Constants.TAG_PRICE.equals(currentTag)) {
                        shoppingCartItem.setPrice(new BigDecimal(characters.getData()));
                    }
                    else if (Constants.TAG_COUNT.equals(currentTag)) {
                        shoppingCartItem.setCount(new BigInteger(characters.getData()));
                    }
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    EndElement endElement = event.asEndElement();
                    currentTag = endElement.getName().getLocalPart();
                    if (Constants.TAG_ORDER_INFO.equals(currentTag)) {
                        order.setOrderInfo(orderInfo);
                    }
                    else if(Constants.TAG_USER_INFO.equals(currentTag)) {
                        order.setUserInfo(userInfo);
                    }
                    else if(Constants.TAG_SHOPPING_CART.equals(currentTag)) {
                        order.setShoppingCart(shoppingCart);
                    }
                    else if(Constants.TAG_SHOPPING_CART_ITEM.equals(currentTag)) {
                        shoppingCart.getShoppingCartItem().add(shoppingCartItem);
                    }
                    else if(Constants.TAG_ORDER.equals(currentTag)) {
                        ordersList.getOrder().add(order);
                    }
                    break;
                default:
            }
        }
        return ordersList;
    }

    public void write(OrdersList ordersList, File file) throws IOException, XMLStreamException {
        XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
        XMLStreamWriter xmlStreamWriter = xmlOutputFactory.createXMLStreamWriter(new FileWriter(file));
        xmlStreamWriter.writeStartDocument();
        xmlStreamWriter.writeStartElement("tns:OrdersList");
        xmlStreamWriter.writeAttribute("xmlns:tns", "http://autoshop.com/details");
        for (Order order : ordersList.getOrder()) {
            xmlStreamWriter.writeStartElement(Constants.TAG_ORDER);
            xmlStreamWriter.writeAttribute(Constants.ATTR_ID, order.getId().toString());

            xmlStreamWriter.writeStartElement(Constants.TAG_ORDER_INFO);
            Constants.createElement(xmlStreamWriter, Constants.TAG_DATE, order.getOrderInfo().getDate().toString());
            Constants.createElement(xmlStreamWriter, Constants.TAG_STATUS, order.getOrderInfo().getStatus().value());
            Constants.createElement(xmlStreamWriter, Constants.TAG_TOTAL_PRICE, order.getOrderInfo().getTotalPrice().toString());
            xmlStreamWriter.writeEndElement();

            xmlStreamWriter.writeStartElement(Constants.TAG_USER_INFO);
            Constants.createElement(xmlStreamWriter, Constants.TAG_NAME, order.getUserInfo().getName());
            Constants.createElement(xmlStreamWriter, Constants.TAG_GENDER, order.getUserInfo().getGender().toString());
            Constants.createElement(xmlStreamWriter, Constants.TAG_EMAIL, order.getUserInfo().getEmail());
            Constants.createElement(xmlStreamWriter, Constants.TAG_BIRTHDATE, order.getUserInfo().getBirthDate().toString());
            Constants.createElement(xmlStreamWriter, Constants.TAG_CITY, order.getUserInfo().getCity());
            xmlStreamWriter.writeEndElement();

            xmlStreamWriter.writeStartElement(Constants.TAG_SHOPPING_CART);
            for(ShoppingCartItem shoppingCartItem : order.getShoppingCart().getShoppingCartItem()) {
                xmlStreamWriter.writeStartElement(Constants.TAG_SHOPPING_CART_ITEM);
                xmlStreamWriter.writeAttribute(Constants.ATTR_ID, shoppingCartItem.getId().toString());
                Constants.createElement(xmlStreamWriter, Constants.TAG_TITLE, shoppingCartItem.getTitle());
                Constants.createElement(xmlStreamWriter, Constants.TAG_PRICE, shoppingCartItem.getPrice().toString());
                Constants.createElement(xmlStreamWriter, Constants.TAG_COUNT, shoppingCartItem.getCount().toString());
                xmlStreamWriter.writeEndElement();
            }
            xmlStreamWriter.writeEndElement();

            xmlStreamWriter.writeEndElement();
        }
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.flush();
        xmlStreamWriter.close();
    }
}
