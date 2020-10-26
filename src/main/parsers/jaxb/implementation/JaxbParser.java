package main.parsers.jaxb.implementation;

import main.constants.Constants;
import main.xmlClass.details.DetailsList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.*;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;

public class JaxbParser {
    private static String xsdFileName = Constants.XSD_SCHEMA_PATH;
    private static Class<?> objectFactory = Constants.OBJECT_FACTORY;

    public <T> T parse(final String xmlFileName) throws JAXBException, SAXException, JAXBException {
        System.out.println(xmlFileName);
        JAXBContext jc = JAXBContext.newInstance(objectFactory);
        Unmarshaller unmarshaller = jc.createUnmarshaller();

        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

        if (xsdFileName != null) { // <-- setup validation on
            Schema schema = sf.newSchema(new File(xsdFileName));
            unmarshaller.setSchema(schema);
            unmarshaller.setEventHandler(new ValidationEventHandler() {
                // this method will be invoked if XML is NOT valid
                @Override
                public boolean handleEvent(ValidationEvent event) {
                    System.err.println("====================================");
                    System.err.println(xmlFileName + " is NOT valid against "
                            + xsdFileName + ":\n" + event.getMessage());
                    System.err.println("====================================");
                    return true;
                }
            });
        }

        return (T) unmarshaller.unmarshal(new File(xmlFileName));
    }

    public <T> void save(T object, final String xmlFileName) throws JAXBException, SAXException {
        Marshaller marshaller = JAXBContext.newInstance(objectFactory).createMarshaller();

        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

        if (xsdFileName != null) {
            Schema schema = sf.newSchema(new File(xsdFileName));

            marshaller.setSchema(schema);
            marshaller.setEventHandler(new ValidationEventHandler() {
                @Override
                public boolean handleEvent(ValidationEvent event) {
                    System.err.println("====================================");
                    System.err.println(xmlFileName + " is NOT valid against "
                            + xsdFileName + ":\n" + event.getMessage());
                    System.err.println("====================================");
                    return false;
                }
            });
        }
        marshaller.marshal(object, new File(xmlFileName));
    }

//    public static void main(String[] args) throws JAXBException, SAXException {
//        System.out.println("START OF JAXB PARSER");
//
//        DetailsList detailsList = parse(Constants.XML_DETAILS_LIST_PATH);
//        detailsList.getDetail().forEach(detail -> {
//            System.out.println(detail.getTitle());
//        });
//        System.out.println("END OF JAXB PARSER\n");
//
//        try {
//            save(detailsList, Constants.XML_DETAILS_LIST_PATH + ".jaxb.xml");
//        } catch (Exception ex) {
//            System.err.println("====================================");
//            System.err.println("Object tree not valid against XSD.");
//            System.err.println(ex.getClass().getName());
//            System.err.println("====================================");
//        }
//    }
}