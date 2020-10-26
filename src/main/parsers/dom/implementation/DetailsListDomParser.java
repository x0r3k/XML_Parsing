package main.parsers.dom.implementation;

//import com.sun.org.apache.xpath.internal.objects.XString;
import main.constants.Constants;
import main.parsers.dom.DomParserUtils;
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
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerException;
import javax.xml.validation.Schema;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;

import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class DetailsListDomParser {
    private Detail parseDetail(Node node) {
        Detail detail = new Detail();

        if (node.hasAttributes()) {
            NamedNodeMap attrs = node.getAttributes();
            Node item = attrs.getNamedItem(Constants.ATTR_ID);
            detail.setId(new BigInteger(item.getTextContent()));
        }

        NodeList nodes = node.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node item = nodes.item(i);
            if (Constants.TAG_TITLE.equals(item.getLocalName())){
                detail.setTitle(item.getTextContent());
            }
            else if(Constants.TAG_PRICE.equals(item.getLocalName())) {
                detail.setPrice(new BigDecimal(item.getTextContent()));
            }
        }
        return detail;
    }

    public DetailsList parse (InputStream in, Schema schema) throws ParserConfigurationException, SAXException, IOException {
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

        DetailsList detailsList = new DetailsList();

        Element element = root.getDocumentElement();
        NodeList xmlDetails = element.getElementsByTagName(Constants.TAG_DETAIL);
        for (int i = 0; i < xmlDetails.getLength(); i++) {
            detailsList.getDetail().add(parseDetail(xmlDetails.item(i)));
        }
        return detailsList;
    }

    public void write (DetailsList detailsList, File file, DocumentBuilder documentBuilder) throws TransformerException {
        Document document = documentBuilder.newDocument();
        Element rootElement = document.createElement("tns:DetailsList");
        rootElement.setAttribute("xmlns:tns", "http://autoshop.com/details");
        document.appendChild(rootElement);
        for (Detail detail : detailsList.getDetail()) {
            Element detailEl = document.createElement(Constants.TAG_DETAIL);
            detailEl.setAttributeNode(DomParserUtils.createAttribute(document, Constants.ATTR_ID, detail.getId().toString()));
            detailEl.appendChild(DomParserUtils.createElement(document, Constants.TAG_TITLE, detail.getTitle()));
            detailEl.appendChild(DomParserUtils.createElement(document, Constants.TAG_PRICE, detail.getPrice().toString()));
            rootElement.appendChild(detailEl);
        }
        DomParserUtils.convertToXML(document, file);
    }
}
