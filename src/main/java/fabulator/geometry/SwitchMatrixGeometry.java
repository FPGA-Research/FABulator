package fabulator.geometry;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * A Class for storing Information about a single switch matrix of a tile.
 */
@Getter
@Setter
public class SwitchMatrixGeometry {

    private String name;
    private double relX;
    private double relY;
    private double width;
    private double height;
    private String src;
    private String csv;

    private List<PortGeometry> portGeometryList;
    private List<PortGeometry> jumpPortGeometryList;

    public SwitchMatrixGeometry(String name) {
        this.name = name;
        this.portGeometryList = new ArrayList<>();
        this.jumpPortGeometryList = new ArrayList<>();
    }
}
