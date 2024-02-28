package fabulator.geometry;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * A Class for storing Information about a tile of the fabric.
 */
@Getter
@Setter
public class TileGeometry {

    private String name;
    private SwitchMatrixGeometry smGeometry;
    private List<BelGeometry> belGeometryList;
    private List<WireGeometry> wireGeometryList;
    private double width;
    private double height;

    public TileGeometry(String name) {
        this.name = name;
        this.belGeometryList = new ArrayList<>();
        this.wireGeometryList = new ArrayList<>();
    }
}
