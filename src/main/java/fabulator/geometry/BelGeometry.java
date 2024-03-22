package fabulator.geometry;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * A class for storing information about a single bel of a
 * tile. Objects of this class are contained in
 * {@link TileGeometry} objects, which contain the geometry
 * information of the tile.
 */
@Getter
@Setter
public class BelGeometry {

    /**
     * Name of the bel.
     */
    private String name;

    /**
     * X coordinate of the bel, relative to the top left of
     * the {@link TileGeometry} containing it.
     */
    private double relX;

    /**
     * Y coordinate of the bel, relative to the top left of
     * the {@link TileGeometry} containing it.
     */
    private double relY;

    /**
     * Width of the bel.
     */
    private double width;

    /**
     * Height of the bel.
     */
    private double height;

    /**
     * HDL source file of the bel.
     */
    private String src;

    /**
     * List of all {@link PortGeometry} objects of the ports
     * of the bel.
     */
    private List<PortGeometry> portGeometryList = new ArrayList<>();

    /**
     * Constructs a {@link BelGeometry} object with the name
     * of the corresponding bel.
     *
     * @param name name of the bel
     */
    public BelGeometry(String name) {
        this.name = name;
    }

    public int getNumberPorts() {
        return this.portGeometryList.size();
    }
}
