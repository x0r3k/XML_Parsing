package main.parsers.dom.implementation;

import main.constants.Constants;
import main.parsers.dom.DomParserUtils;
import main.xmlClass.cars.*;

import main.xmlClass.details.Detail;
import main.xmlClass.details.DetailsList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;

public class CarsDomParser {
    private static final String ATTR_ID = "id";

    private static final String TAG_CYLINDER = "Cylinder";
    private static final String TAG_CAPACITY = "Capacity";
    private static final String TAG_POWER = "Power";

    private static final String TAG_BRAND = "Brand";
    private static final String TAG_MODEL = "Model";
    private static final String TAG_YEAR = "Year";
    private static final String TAG_ENGINE = "Engine";

    private static final String TAG_CAR = "Car";

    private static final String XSD_SCHEMA_PATH = "src/main/resources/AutoShop.xsd";
    private static final String XML_CARS_LIST_PATH = "src/main/resources/Cars.xml";

    private Engine parseEngine (Node node) {
        Engine engine = new Engine();
        NodeList nodes = node.getChildNodes();

        for (int i = 0; i < nodes.getLength(); i++) {
            Node item = nodes.item(i);
            if (TAG_CYLINDER.equals(item.getLocalName())){
                engine.setCylinder(new BigInteger(item.getTextContent()));
            }
            else if(TAG_CAPACITY.equals(item.getLocalName())) {
                engine.setCapacity(new BigDecimal(item.getTextContent()));
            }
            else if(TAG_POWER.equals(item.getLocalName())) {
                engine.setPower(new BigDecimal(item.getTextContent()));
            }
        }
        return engine;
    }

    private Car parseCar (Node node) {
        Car car = new Car();
        NodeList nodes = node.getChildNodes();

        if (node.hasAttributes()) {
            NamedNodeMap attrs = node.getAttributes();
            Node item = attrs.getNamedItem(ATTR_ID);
            car.setId(new BigInteger(item.getTextContent()));
        }

        for (int i = 0; i < nodes.getLength(); i++) {
            Node item = nodes.item(i);
            if (TAG_BRAND.equals(item.getLocalName())){
                car.setBrand(item.getTextContent());
            }
            else if(TAG_MODEL.equals(item.getLocalName())) {
                car.setModel(item.getTextContent());
            }
            else if(TAG_YEAR.equals(item.getLocalName())) {
                car.setYear(Integer.parseInt(item.getTextContent()));
            }
            else if(TAG_ENGINE.equals(item.getLocalName())) {
                car.setEngine(parseEngine(item));
            }
        }
        return car;
    }

    public CarsList parse (InputStream in, Schema schema) throws ParserConfigurationException, SAXException, IOException {
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

        CarsList carsList = new CarsList();

        Element element = root.getDocumentElement();
        NodeList xmlDetails = element.getElementsByTagName(TAG_CAR);
        for (int i = 0; i < xmlDetails.getLength(); i++) {
            carsList.getCar().add(parseCar(xmlDetails.item(i)));
        }
        return carsList;
    }

    public void write (CarsList carsList, File file, DocumentBuilder documentBuilder) throws TransformerException {
        Document document = documentBuilder.newDocument();
        Element rootElement = document.createElement("tns:CarsList");
        rootElement.setAttribute("xmlns:tns", "http://autoshop.com/details");
        document.appendChild(rootElement);
        for (Car car : carsList.getCar()) {
            Element carEl = document.createElement(Constants.TAG_CAR);
            carEl.setAttributeNode(DomParserUtils.createAttribute(document, Constants.ATTR_ID, car.getId().toString()));
            carEl.appendChild(DomParserUtils.createElement(document, Constants.TAG_BRAND, car.getBrand()));
            carEl.appendChild(DomParserUtils.createElement(document, Constants.TAG_MODEL, car.getModel()));
            carEl.appendChild(DomParserUtils.createElement(document, Constants.TAG_YEAR, Integer.toString(car.getYear())));
            Element engineEl = document.createElement(Constants.TAG_ENGINE);
            Engine engine = car.getEngine();
            if(engine.getCylinder() !=null) {
                engineEl.appendChild(DomParserUtils.createElement(document, Constants.TAG_CYLINDER, engine.getCylinder().toString()));
            }
            if(engine.getCapacity() !=null) {
                engineEl.appendChild(DomParserUtils.createElement(document, Constants.TAG_CAPACITY, engine.getCapacity().toString()));
            }
            if(engine.getPower() !=null) {
                engineEl.appendChild(DomParserUtils.createElement(document, Constants.TAG_POWER, engine.getPower().toString()));
            }
            carEl.appendChild(engineEl);
            rootElement.appendChild(carEl);
        }
        DomParserUtils.convertToXML(document, file);
    }
}
