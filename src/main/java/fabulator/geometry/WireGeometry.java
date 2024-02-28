package fabulator.geometry;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


/**
 * A Class for storing Information about a single wire on the fabric.
 */
@Getter
@Setter
public class WireGeometry {

    private String name;
    private List<Location> path;    // TODO: save pathLength and replace list by array to safe space?

    public WireGeometry(String name) {
        this.name = name;
        this.path = new ArrayList<>();
    }

}
