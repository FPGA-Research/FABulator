package fabulator.geometry;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A class for storing information about a single switch
 * matrix of a tile. Objects of this class are contained in
 * {@link TileGeometry} objects, which contain the geometry
 * information of the tile.
 */
@Getter
@Setter
public class SwitchMatrixGeometry {

    /**
     * Name of the switch matrix.
     */
    private String name;

    /**
     * X coordinate of the switch matrix, relative to the
     * top left of the {@link TileGeometry} containing it.
     */
    private double relX;

    /**
     * Y coordinate of the switch matrix, relative to the
     * top left of the {@link TileGeometry} containing it.
     */
    private double relY;

    /**
     * Width of the switch matrix.
     */
    private double width;

    /**
     * Height of the switch matrix.
     */
    private double height;

    /**
     * HDL source file of the switch matrix.
     */
    private String src;

    /**
     * CSV file of the switch matrix. Can be read using
     * {@link fabulator.parse.SwitchMatrixParser}.
     */
    private String csv;

    /**
     * List of all {@link PortGeometry} objects of the
     * regular ports of the switch matrix.
     */
    private List<PortGeometry> portGeometryList = new ArrayList<>();

    /**
     * List of all {@link PortGeometry} objects of the jump
     * ports of the switch matrix.
     */
    private List<PortGeometry> jumpPortGeometryList = new ArrayList<>();

    /**
     * Constructs a {@link SwitchMatrixGeometry} object with
     * the name of the corresponding switch matrix.
     *
     * @param name name of the switch matrix
     */
    public SwitchMatrixGeometry(String name) {
        this.name = name;
    }

    public int getNumberPorts() {
        return this.portGeometryList.size() + this.jumpPortGeometryList.size();
    }

    public long getNumberNorthPorts() {
        return numberPortsAt(Side.NORTH);
    }

    public long getNumberSouthPorts() {
        return numberPortsAt(Side.SOUTH);
    }

    public long getNumberEastPorts() {
        return numberPortsAt(Side.EAST);
    }

    public long getNumberWestPorts() {
        return numberPortsAt(Side.WEST);
    }

    public long getNumberBelPorts() {
        return this.numberPortsAt(Side.ANY);
    }

    public long getNumberJumpPorts() {
        return this.jumpPortGeometryList.size();
    }

    private long numberPortsAt(Side side) {
        return this.portGeometryList.stream()
                .filter(portGeom -> portGeom.getSide() == side)
                .count();
    }

    public List<PortGeometry> getNorthPorts() {
        return portsAt(Side.NORTH);
    }

    public List<PortGeometry> getSouthPorts() {
        return portsAt(Side.SOUTH);
    }

    public List<PortGeometry> getEastPorts() {
        return portsAt(Side.EAST);
    }

    public List<PortGeometry> getWestPorts() {
        return portsAt(Side.WEST);
    }

    public List<PortGeometry> getBelPorts() {
        return portsAt(Side.ANY);
    }

    public List<PortGeometry> getJumpPorts() {
        return this.jumpPortGeometryList;
    }

    public List<PortGeometry> portsAt(Side side) {
        return this.portGeometryList.stream()
                .filter(portGeom -> portGeom.getSide() == side)
                .collect(Collectors.toList());
    }
}
