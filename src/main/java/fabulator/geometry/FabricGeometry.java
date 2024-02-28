package fabulator.geometry;

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

    private String name;
    private int numberOfRows;
    private int numberOfColumns;
    private int width;
    private int height;
    private int numberOfLines;

    private List<List<String>> tileNames;
    private List<List<Location>> tileLocations;
    private Map<String, TileGeometry> tileGeomMap;
}
