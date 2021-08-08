import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "hello")
public class Hello {
    @XmlElement(name = "world")
    @XmlJavaTypeAdapter(World.JaxbAdapter.class)
    private List<World> world = new ArrayList<>();

    public List<World> getWorld() {
        return world;
    }
}
