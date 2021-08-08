import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.dom.DOMSource;
import java.io.InputStream;
import java.util.Stack;

public class TestMain {
    public Document parse(InputStream inputStream) throws Exception {
        final Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        final DefaultHandler handler = new DefaultHandler() {
            private final Stack<Element> elementStack = new Stack<>();
            private Locator locator;
            @Override
            public void setDocumentLocator(Locator locator) {
                this.locator = locator;
            }
            @Override
            public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                Element el = document.createElement(qName);
                for (int i = 0; i < attributes.getLength(); i++) {
                    String name = attributes.getQName(i);
                    String value = attributes.getValue(i);
                    el.setAttribute(name, value);
                }
                el.setUserData(
                        "location",
                        locator.getLineNumber() + ":" + locator.getColumnNumber(),
                        null
                );
                this.elementStack.push(el);
            }

            @Override
            public void endElement(String uri, String localName, String qName) throws SAXException {
                Element closedEl = elementStack.pop();
                if (elementStack.isEmpty()) { // Is this the root element?
                    document.appendChild(closedEl);
                } else {
                    Element parentEl = elementStack.peek();
                    parentEl.appendChild(closedEl);
                }
            }
        };
        SAXParserFactory.newInstance().newSAXParser().parse(inputStream, handler);
        return document;
    }

    @Test
    public void test() throws Exception {
        // Parse
        Document document = parse(TestMain.class.getResourceAsStream("sample.xml"));

        // Unmarshal
        JAXBContext jaxbContext = JAXBContext.newInstance(Hello.class, World.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        unmarshaller.setAdapter(new World.JaxbAdapter(jaxbContext));
        Hello hello = unmarshaller.unmarshal(new DOMSource(document), Hello.class)
                .getValue();
        System.out.println("hello = " + hello);

        assert hello.getWorld().get(0).getOrder() == 1;
        assert hello.getWorld().get(0).getLocation() != null;
        assert hello.getWorld().get(1).getOrder() == 1;
        assert hello.getWorld().get(1).getLocation() != null;
    }
}
