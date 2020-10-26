package main.parsers.sax.implementation;

import main.constants.Constants;

import main.xmlClass.details.Detail;
import main.xmlClass.details.DetailsList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;

public class DetailsListSaxParser extends DefaultHandler {

    private String currentTag;

    private DetailsList detailsList;
    private Detail detail;

    public void error(org.xml.sax.SAXParseException e) throws SAXException {
        throw e; // throw exception if xml not valid
    }

    public DetailsList parseXML(InputStream in) throws ParserConfigurationException, SAXException, IOException {
        SAXParserFactory factory = SAXParserFactory.newInstance();

        factory.setNamespaceAware(true);


        SAXParser saxParser = factory.newSAXParser();
        saxParser.parse(in, this);

        return detailsList;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
//        System.out.println("start_" + localName);
        currentTag = localName;
        if (Constants.TAG_DETAILS_LIST.equals(localName)) {
            detailsList = new DetailsList();
        }
        else if (Constants.TAG_DETAIL.equals(localName)) {
            detail = new Detail();
            if (attributes.getLength() > 0){
                detail.setId(new BigInteger(attributes.getValue("id")));
            }
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) {
//        System.out.println("characters_" + new String(ch, start, length));
        if (Constants.TAG_TITLE.equals(currentTag)) {
            detail.setTitle(new String(ch, start, length));
        }
        else if (Constants.TAG_PRICE.equals(currentTag)) {
            detail.setPrice(new BigDecimal(new String(ch, start, length)));
        }
        currentTag = null;
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
//        System.out.println("end_" + localName);
        if (Constants.TAG_DETAIL.equals(localName)) {
            detailsList.getDetail().add(detail);
        }
    }

    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
        System.out.println("START OF DETAILS PARSE");
        DetailsListSaxParser parser = new DetailsListSaxParser();
        parser.parseXML(new FileInputStream(Constants.XML_DETAILS_LIST_PATH));
        DetailsList detailsList = parser.detailsList;
        detailsList.getDetail().forEach(detail -> {
            System.out.println(detail.getTitle());
        });
        System.out.println("END OF DETAILS PARSE\n");
    }
}
