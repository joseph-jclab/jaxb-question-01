import org.w3c.dom.Node;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlAdapter;

@XmlRootElement
public class World {
    @XmlAttribute(name = "order")
    private Integer order;

    @XmlTransient
    private String location = null;

    public World() {}

    public Integer getOrder() {
        return order;
    }

    public String getLocation() {
        return location;
    }

    public static class JaxbAdapter extends XmlAdapter<Object, World> {
        private final JAXBContext jaxbContext;

        public JaxbAdapter(JAXBContext jaxbContext) {
            this.jaxbContext = jaxbContext;
        }

        @Override
        public World unmarshal(Object v) throws Exception {
            Node node = (Node) v;
            System.out.println(v);
            World world = this.jaxbContext.createUnmarshaller()
                    .unmarshal(node, World.class)
                    .getValue();
            world.location = (String) node.getUserData("location");
            return world;
        }

        @Override
        public Object marshal(World v) throws Exception {
            return v;
        }
    }
}
