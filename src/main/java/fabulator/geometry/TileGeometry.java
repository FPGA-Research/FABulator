package fabulator.geometry;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * A class for storing information about a tile of the
 * fabric. Objects of this class are contained in a
 * {@link FabricGeometry} object, which contains the
 * geometry information of the fabric.
 */
@Getter
@Setter
public class TileGeometry {

    /**
     * Name of the tile
     */
    private String name;

    /**
     * {@link SwitchMatrixGeometry} object of the switch
     * matrix of the tile.
     */
    private SwitchMatrixGeometry smGeometry;

    /**
     * List of all {@link BelGeometry} objects of the bels
     * of the tile.
     */
    private List<BelGeometry> belGeometryList = new ArrayList<>();

    /**
     * List of all {@link WireGeometry} objects of the wires
     * of the tile.
     */
    private List<WireGeometry> wireGeometryList = new ArrayList<>();

    /**
     * Width of the tile.
     */
    private double width;

    /**
     * Height of the tile
     */
    private double height;

    /**
     * Construct a {@link TileGeometry} object with the name
     * of the corresponding tile.
     *
     * @param name name of the tile
     */
    public TileGeometry(String name) {
        this.name = name;
    }
}
