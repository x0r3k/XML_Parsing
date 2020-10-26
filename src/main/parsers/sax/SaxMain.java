package main.parsers.sax;

import main.parsers.sax.implementation.CarsSaxParser;
import main.parsers.sax.implementation.DetailsListSaxParser;
import main.parsers.sax.implementation.OrdersSaxParser;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class SaxMain {
    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {
        DetailsListSaxParser.main(new String[] {});
        CarsSaxParser.main(new String[] {});
        OrdersSaxParser.main(new String[] {});
    }
}
