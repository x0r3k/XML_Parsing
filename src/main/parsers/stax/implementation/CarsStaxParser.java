package main.parsers.stax.implementation;

import com.sun.corba.se.impl.orbutil.closure.Constant;
import main.constants.Constants;
import main.xmlClass.cars.Car;
import main.xmlClass.cars.CarsList;
import main.xmlClass.cars.Engine;
import main.xmlClass.details.Detail;
import main.xmlClass.details.DetailsList;

import javax.xml.namespace.QName;
import javax.xml.stream.*;
import javax.xml.stream.events.*;
import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;

public class CarsStaxParser {

    private String currentTag;

    private CarsList carsList;
    private Car car;
    private Engine engine;

    public CarsList parse(InputStream in) throws XMLStreamException {
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
                    if (Constants.TAG_CARS_LIST.equals(currentTag)) {
                        carsList = new CarsList();
                    } else if (Constants.TAG_CAR.equals(currentTag)) {
                        car = new Car();
                        Attribute attr = startElement.getAttributeByName(new QName(Constants.ATTR_ID));
                        if (attr != null) {
                            car.setId(new BigInteger(attr.getValue()));
                        }
                    } else if (Constants.TAG_ENGINE.equals(currentTag)) {
                        engine = new Engine();
                    }
                    break;
                case XMLStreamConstants.CHARACTERS:
                    Characters characters = event.asCharacters();
                    if (Constants.TAG_BRAND.equals(currentTag)) {
                        car.setBrand(characters.getData());
                    } else if (Constants.TAG_MODEL.equals(currentTag)) {
                        car.setModel(characters.getData());
                    } else if (Constants.TAG_YEAR.equals(currentTag)) {
                        car.setYear(Integer.parseInt(characters.getData()));
                    } else if (Constants.TAG_CYLINDER.equals(currentTag)) {
                        engine.setCylinder(new BigInteger(characters.getData()));
                    } else if (Constants.TAG_CAPACITY.equals(currentTag)) {
                        engine.setCapacity(new BigDecimal(characters.getData()));
                    } else if (Constants.TAG_POWER.equals(currentTag)) {
                        engine.setPower(new BigDecimal(characters.getData()));
                    }
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    EndElement endElement = event.asEndElement();
                    currentTag = endElement.getName().getLocalPart();
                    if (Constants.TAG_ENGINE.equals(currentTag)) {
                        car.setEngine(engine);
                    } else if (Constants.TAG_CAR.equals(currentTag)) {
                        carsList.getCar().add(car);
                    }
                    break;
                default:
            }
        }
        return carsList;
    }

    public void write(CarsList carsList, File file) throws IOException, XMLStreamException {
        XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
        XMLStreamWriter xmlStreamWriter = xmlOutputFactory.createXMLStreamWriter(new FileWriter(file));
        xmlStreamWriter.writeStartDocument();
        xmlStreamWriter.writeStartElement("tns:CarsList");
        xmlStreamWriter.writeAttribute("xmlns:tns", "http://autoshop.com/details");
        for (Car car : carsList.getCar()) {
            xmlStreamWriter.writeStartElement(Constants.TAG_CAR);
            xmlStreamWriter.writeAttribute(Constants.ATTR_ID, car.getId().toString());

            Constants.createElement(xmlStreamWriter, Constants.TAG_BRAND, car.getBrand());
            Constants.createElement(xmlStreamWriter, Constants.TAG_MODEL, car.getModel());
            Constants.createElement(xmlStreamWriter, Constants.TAG_YEAR, car.getYear()+"");
            xmlStreamWriter.writeStartElement(Constants.TAG_ENGINE);
                if(car.getEngine().getCylinder() != null) {
                    Constants.createElement(xmlStreamWriter, Constants.TAG_CYLINDER, car.getEngine().getCylinder().toString());
                }
                if(car.getEngine().getCapacity() != null) {
                    Constants.createElement(xmlStreamWriter, Constants.TAG_CAPACITY, car.getEngine().getCapacity().toString());
                }
                if(car.getEngine().getPower() != null) {
                    Constants.createElement(xmlStreamWriter, Constants.TAG_POWER, car.getEngine().getPower().toString());
                }
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeEndElement();
        }
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.flush();
        xmlStreamWriter.close();
    }
}
