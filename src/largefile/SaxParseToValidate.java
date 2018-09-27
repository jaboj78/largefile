package largefile;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * SAX parse a XML file to validate if XMl file is well formed.
 * 
 * @author Jacob Boje
 */
public class SaxParseToValidate {
    public static void main(String args[]) {
        //args = new String[] {"/C:/largefile/largefile.xml"};
        
        if(args.length<1) {
            System.err.println("Usage:");
            System.err.println("  " + SaxParseToValidate.class.getName() + " [path]");
            System.err.println();
            System.err.println("Where [path] is the relative or absolute path to a xml file.");
            System.exit(1);
        }

        try {
            final SAXParserFactory factory = SAXParserFactory.newInstance();
            final SAXParser saxParser = factory.newSAXParser();

            final DefaultHandler handler = new DefaultHandler() {

                @Override
                public void startElement(final String uri, final String localName, final String qName,
                        final Attributes attributes) throws SAXException {

                }

                @Override
                public void endElement(final String uri, final String localName, final String qName)
                        throws SAXException {

                }

                @Override
                public void characters(final char ch[], final int start, final int length) throws SAXException {
                }
            };

            System.out.println("Parse file...");
            saxParser.parse(args[0], handler);
            System.out.println("Completed succesfully");
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
}

