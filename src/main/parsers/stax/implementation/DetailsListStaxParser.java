package main.parsers.stax.implementation;

import main.constants.Constants;
import main.xmlClass.details.Detail;
import main.xmlClass.details.DetailsList;

import javax.xml.namespace.QName;
import javax.xml.stream.*;
import javax.xml.stream.events.*;
import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;

public class DetailsListStaxParser {

    private String currentTag;
    private DetailsList detailsList;
    private Detail detail;

    public DetailsList parse(InputStream in) throws XMLStreamException {
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
                    if (Constants.TAG_DETAILS_LIST.equals(currentTag)) {
                        detailsList = new DetailsList();
                    }
                    else if (Constants.TAG_DETAIL.equals(currentTag)) {
                        detail = new Detail();
                        Attribute attr = startElement.getAttributeByName(new QName(Constants.ATTR_ID));
                        if (attr != null) {
                            detail.setId(new BigInteger(attr.getValue()));
                        }
                    }
                    break;
                case XMLStreamConstants.CHARACTERS:
                    Characters characters = event.asCharacters();
                    if (Constants.TAG_TITLE.equals(currentTag)) {
                        detail.setTitle(characters.getData());
                    }
                    else if (Constants.TAG_PRICE.equals(currentTag)) {
                        detail.setPrice(new BigDecimal(characters.getData()));
                    }
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    EndElement endElement = event.asEndElement();
                    currentTag = endElement.getName().getLocalPart();
                    if (Constants.TAG_DETAIL.equals(currentTag)) {
                        detailsList.getDetail().add(detail);
                    }
                    break;
                default:
            }
        }
        return detailsList;
    }

    public void write(DetailsList detailsList, File file) throws IOException, XMLStreamException {
        XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
        XMLStreamWriter xmlStreamWriter = xmlOutputFactory.createXMLStreamWriter(new FileWriter(file));
        xmlStreamWriter.writeStartDocument();
        xmlStreamWriter.writeStartElement("tns:DetailsList");
        xmlStreamWriter.writeAttribute("xmlns:tns", "http://autoshop.com/details");
        for (Detail detail : detailsList.getDetail()) {
            xmlStreamWriter.writeStartElement(Constants.TAG_DETAIL);
            xmlStreamWriter.writeAttribute(Constants.ATTR_ID, detail.getId().toString());

            Constants.createElement(xmlStreamWriter, Constants.TAG_TITLE, detail.getTitle());
            Constants.createElement(xmlStreamWriter, Constants.TAG_PRICE, detail.getPrice().toString());

            xmlStreamWriter.writeEndElement();
        }
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.flush();
        xmlStreamWriter.close();
    }
}
