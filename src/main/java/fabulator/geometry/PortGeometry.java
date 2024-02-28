package fabulator.geometry;


import lombok.Getter;
import lombok.Setter;

/**
 * A Class for storing Information about a single port
 */
@Getter
@Setter
public class PortGeometry {

    private String name;
    private String sourceName;
    private String destName;
    private IO io;
    private double relX;
    private double relY;

    public PortGeometry(String name) {
        this.name = name;
    }
}
