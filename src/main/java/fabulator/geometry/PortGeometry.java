package fabulator.geometry;

import lombok.Getter;
import lombok.Setter;

/**
 * A class for storing information about a single port.
 * Can be part of a {@link BelGeometry} object or a
 * {@link SwitchMatrixGeometry} object.
 */
@Getter
@Setter
public class PortGeometry {

    /**
     * Name of the port
     */
    private String name;

    /**
     * Name of the port source
     */
    private String sourceName;

    /**
     * Name of the port destination
     */
    private String destName;

    /**
     * IO direction of the port
     */
    private IO io;

    /**
     * X coordinate of the port, relative to the top
     * left position of its parent, i.e. {@link BelGeometry}
     * or {@link SwitchMatrixGeometry}.
     */
    private double relX;

    /**
     * Y coordinate of the port, relative to the top
     * left position of its parent, i.e. {@link BelGeometry}
     * or {@link SwitchMatrixGeometry}.
     */
    private double relY;

    /**
     * Constructs a {@link PortGeometry} object with the
     * name of the corresponding port.
     *
     * @param name name of the port
     */
    public PortGeometry(String name) {
        this.name = name;
    }
}
