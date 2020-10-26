package main.parsers.sax.implementation;

import main.constants.Constants;
import main.xmlClass.cars.Car;
import main.xmlClass.cars.CarsList;
import main.xmlClass.cars.Engine;
import main.xmlClass.details.Detail;
import main.xmlClass.details.DetailsList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;

public class CarsSaxParser extends DefaultHandler {

    private String currentTag;

    private CarsList carsList;
    private Car car;
    private Engine engine;

    public void error(org.xml.sax.SAXParseException e) throws SAXException {
        throw e; // throw exception if xml not valid
    }

    public CarsList parseXML(InputStream in) throws ParserConfigurationException, SAXException, IOException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        SAXParser saxParser = factory.newSAXParser();
        saxParser.parse(in, this);
        return carsList;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
//        System.out.println("start_" + localName);
        currentTag = localName;
        if (Constants.TAG_CARS_LIST.equals(localName)) {
            carsList = new CarsList();
        }
        else if (Constants.TAG_CAR.equals(localName)) {
            car = new Car();
            if (attributes.getLength() > 0){
                car.setId(new BigInteger(attributes.getValue("id")));
            }
        }
        else if (Constants.TAG_ENGINE.equals(localName)) {
            engine = new Engine();
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) {
//        System.out.println("characters_" + new String(ch, start, length));
        if (Constants.TAG_BRAND.equals(currentTag)) {
            car.setBrand(new String(ch, start, length));
        }
        else if (Constants.TAG_MODEL.equals(currentTag)) {
            car.setModel(new String(ch, start, length));
        }
        else if (Constants.TAG_YEAR.equals(currentTag)) {
            car.setYear(Integer.parseInt(new String(ch, start, length)));
        }
        else if (Constants.TAG_CYLINDER.equals(currentTag)) {
            car.setYear(Integer.parseInt(new String(ch, start, length)));
        }
        else if (Constants.TAG_CAPACITY.equals(currentTag)) {
            engine.setCapacity(new BigDecimal(new String(ch, start, length)));
        }
        else if (Constants.TAG_POWER.equals(currentTag)) {
            engine.setPower(new BigDecimal(new String(ch, start, length)));
        }
        currentTag = null;
    }

    public void endElement(String uri, String localName, String qName) {
//        System.out.println("end_" + localName);
        if (Constants.TAG_ENGINE.equals(localName)) {
            car.setEngine(engine);
        }
        else if(Constants.TAG_CAR.equals(localName)) {
            carsList.getCar().add(car);
        }
    }

    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
        System.out.println("START OF CARS PARSE");
        CarsSaxParser parser = new CarsSaxParser();
        parser.parseXML(new FileInputStream(Constants.XML_CARS_LIST_PATH));
        CarsList carsList = parser.carsList;
        carsList.getCar().forEach(car -> {
            System.out.println(car.getBrand());
        });
        System.out.println("END OF DETAILS PARSE\n");
    }
}
