package fabulator.geometry;

import fabulator.object.Location;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * A class for storing information of a fabric.
 */
@Getter
@Setter
@AllArgsConstructor
public class FabricGeometry {

    /**
     * Name of the fabric.
     */
    private String name;

    /**
     * Number of rows of the fabric.
     */
    private int numberOfRows;

    /**
     * Number of columns of the fabric
     */
    private int numberOfColumns;

    /**
     * Width of the fabric.
     */
    private int width;

    /**
     * Height of the fabric.
     */
    private int height;

    /**
     * the total amount of lines (segments) of wires of the
     * fabrics routing. Can, for instance, be used to
     * initialize the size of datastructures.
     */
    private int numberOfLines;

    /**
     * Matrix of tileNames in the fabric, representing the
     * tile layout of the fabric. A null-tile is represented
     * by a null value.
     */
    private List<List<String>> tileNames;

    /**
     * Matrix of locations of the tiles of the fabric,
     * represented by their top left location. These
     * locations correspond to the tiles represented by the
     * name in {@link FabricGeometry#getTileNames()}.
     */
    private List<List<Location>> tileLocations;

    /**
     * Map of the {@link TileGeometry} of each tile by name.
     */
    private Map<String, TileGeometry> tileGeomMap;
}
