package fabulator.geometry;

import fabulator.object.Location;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * A class for storing information about a single wire
 * segment of a tile. Note that this segment, unlike wires
 * in the internal FABulous model, is fully contained in a
 * tile. Objects of this class are contained in
 * {@link TileGeometry} objects, which contain the geometry
 * information of the tile.
 */
@Getter
@Setter
public class WireGeometry {

    /**
     * Name of the wire
     */
    private String name;

    /**
     * List of {@link Location} objects, representing the
     * path the wire is on.
     */
    private List<Location> path = new ArrayList<>();    // TODO: save pathLength and replace list by array to safe space?

    /**
     * Constructs a {@link WireGeometry} object with the
     * name of the corresponding wire.
     *
     * @param name name of the wire
     */
    public WireGeometry(String name) {
        this.name = name;
    }
}
