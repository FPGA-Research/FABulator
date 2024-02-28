package fabulator.geometry;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * A class for storing information about a single bel of a tile.
 */
@Getter
@Setter
public class BelGeometry {

    private String name;
    private double relX;
    private double relY;
    private double width;
    private double height;
    private String src;

    private List<PortGeometry> portGeometryList;

    public BelGeometry(String name) {
        this.name = name;
        this.portGeometryList = new ArrayList<>();
    }
}
