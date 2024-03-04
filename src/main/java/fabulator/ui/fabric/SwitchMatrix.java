package fabulator.ui.fabric;

import fabulator.geometry.*;
import fabulator.lookup.BitstreamConfiguration;
import fabulator.parse.SwitchMatrixParser;
import fabulator.settings.Config;
import fabulator.ui.builder.ArcBuilder;
import fabulator.ui.builder.LineBuilder;
import fabulator.ui.builder.RectangleBuilder;
import fabulator.ui.fabric.element.ElementType;
import fabulator.ui.fabric.element.FabricElement;
import fabulator.ui.fabric.port.AbstractPort;
import fabulator.ui.fabric.port.JumpPort;
import fabulator.ui.fabric.port.SmPort;
import fabulator.util.FileUtils;
import javafx.beans.property.Property;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


// TODO: There is a lot of redundant/duplicate code in this class

/**
 * A Class representing a single switch matrix of a tile.
 */
@Getter
@Setter
public class SwitchMatrix extends Group implements FabricElement {

    private Tile tile;
    private SwitchMatrixGeometry geometry;
    private Rectangle smRect;

    private List<Shape> displayedConnections;
    private List<Shape> displayedBitstreamConfig;

    /**
     * A HashMap mapping Port Locations to all Shapes such
     * as Lines, Arcs that represent a connection of
     * the bitstream config to or from that Port.
     */
    private HashMap<DiscreteLocation, Set<Shape>> bitstreamConMap;
    private List<Shape> netWires;

    private HashMap<String, AbstractPort> namePortMap;

    public SwitchMatrix(SwitchMatrixGeometry geometry, Tile tile) {
        this.geometry = geometry;
        this.tile = tile;

        this.initialize();
        this.build();
    }

    private void initialize() {
        this.displayedConnections = new ArrayList<>();
        this.displayedBitstreamConfig = new ArrayList<>();
        this.bitstreamConMap = new HashMap<>();
        this.netWires = new ArrayList<>();
    }

    private void build() {
        this.setTranslateX(this.geometry.getRelX());
        this.setTranslateY(this.geometry.getRelY());

        this.smRect = new RectangleBuilder()
                .setDims(this.geometry.getWidth(), this.geometry.getHeight())
                .setArcDims(6, 6)
                .setStroke(Color.WHITE, 1)
                .setFill(Color.BLACK)
                .addTooltip(this.geometry.getName())
                .setOnMouseClicked(this::onClicked)
                .build();

        this.getChildren().add(this.smRect);

        int initialSize = (this.geometry.getPortGeometryList().size() * 3) / 2;
        this.namePortMap = new HashMap<>(initialSize);

        for (PortGeometry portGeom : this.geometry.getPortGeometryList()) {
            SmPort port = new SmPort(portGeom, this);
            this.getChildren().add(port);
            this.namePortMap.put(portGeom.getName(), port);
        }

        for (PortGeometry jumpPortGeom : this.geometry.getJumpPortGeometryList()) {
            JumpPort jumpPort = new JumpPort(jumpPortGeom, this);
            this.getChildren().add(jumpPort);
            this.namePortMap.put(jumpPortGeom.getSourceName(), jumpPort);
            this.namePortMap.put(jumpPortGeom.getDestName(), jumpPort);
        }
    }

    private void onClicked(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            FileUtils.openHdlFile(this.geometry.getSrc());
        }
    }

    // TODO: might be wise to cache SmConnectivity
    public void displayConnections(AbstractPort selectedPort) {
        Location origin = new Location(
                selectedPort.getGeometry().getRelX(),
                selectedPort.getGeometry().getRelY()
        );

        SwitchMatrixParser parser = new SwitchMatrixParser(this.geometry.getCsv());
        Map<String, Boolean> connectedNames = parser.getConnection().connectedNamesOf(selectedPort);

        this.getChildren().removeAll(this.displayedConnections);
        this.displayedConnections.clear();

        Config config = Config.getInstance();

        for (String name : connectedNames.keySet()) {
            AbstractPort port = this.namePortMap.get(name);
            if (port == null) continue;     // TODO: this should not happen
            Location portLoc = new Location(
                    port.getGeometry().getRelX(),
                    port.getGeometry().getRelY()
            );
            DiscreteLocation discreteLocA = new DiscreteLocation(origin);
            DiscreteLocation discreteLocB = new DiscreteLocation(portLoc);

            // For jump ports
            if (discreteLocA.equals(discreteLocB)) continue;

            String destName = port.getGeometry().getName();
            Property<Color> colorProp = config.getSmConnJumpColor();
            if (connectedNames.get(destName) != null) {
                colorProp = connectedNames.get(destName)
                        ? config.getSmConnInColor()
                        : config.getSmConnOutColor();
            }

            boolean drawCurve = false;
            int offsetX = 0;
            int offsetY = 0;

            boolean xEqual = discreteLocA.getX() == discreteLocB.getX();
            boolean atLeftOrRightBorder = discreteLocA.getX() == 0 || discreteLocA.getX() == this.geometry.getWidth();

            boolean yEqual = discreteLocA.getY() == discreteLocB.getY();
            boolean atTopOrBottomBorder = discreteLocA.getY() == 0 || discreteLocA.getY() == this.geometry.getHeight();

            if (xEqual && atLeftOrRightBorder) {
                offsetX = discreteLocA.getX() == 0 ? 1 : -1;
                drawCurve = true;
            } else if (yEqual && atTopOrBottomBorder) {
                offsetY = discreteLocA.getY() == 0 ? 1 : -1;
                drawCurve = true;
            }

            if (drawCurve) {
                Line startLine = new LineBuilder()
                        .setStart(discreteLocA.getX(), discreteLocA.getY())
                        .setEnd(discreteLocA.getX() + offsetX, discreteLocA.getY() + offsetY)
                        .setStroke(colorProp, 0.2)
                        .build();
                this.displayedConnections.add(startLine);

                Line endLine = new LineBuilder()
                        .setStart(discreteLocB.getX() + offsetX, discreteLocB.getY() + offsetY)
                        .setEnd(discreteLocB.getX(), discreteLocB.getY())
                        .setStroke(colorProp, 0.2)
                        .build();
                this.displayedConnections.add(endLine);

                int startAngle = -offsetX * 90 + offsetY * (90 + 90 * offsetY);
                double diffX = Math.abs(discreteLocA.getX() - discreteLocB.getX());
                double diffY = Math.abs(discreteLocA.getY() - discreteLocB.getY());
                double radiusX = (diffY / 4) * (offsetX * offsetX) + (diffX / 2) * (offsetY * offsetY);
                double radiusY = (diffX / 4) * (offsetY * offsetY) + (diffY / 2) * (offsetX * offsetX);

                Arc arc = new ArcBuilder()
                        .setCenterX((double) (discreteLocA.getX() + discreteLocB.getX()) / 2 + offsetX)
                        .setCenterY((double) (discreteLocA.getY() + discreteLocB.getY()) / 2 + offsetY)
                        .setRadius(radiusX, radiusY)
                        .setStartAngle(startAngle)
                        .setLength(180)
                        .setStroke(colorProp, 0.2)
                        .setFill(Color.TRANSPARENT)
                        .build();
                this.displayedConnections.add(arc);

            } else {
                Line line = new LineBuilder()
                        .setStart(discreteLocA.getX(), discreteLocA.getY())
                        .setEnd(discreteLocB.getX(), discreteLocB.getY())
                        .setStroke(colorProp, 0.2)
                        .build();
                this.displayedConnections.add(line);
            }
        }
        this.getChildren().addAll(this.displayedConnections);
    }


    public void displayBitstreamConfig(List<BitstreamConfiguration.ConnectedPorts> connectedPortsList) {
        this.getChildren().removeAll(this.displayedBitstreamConfig);
        this.displayedBitstreamConfig.clear();
        this.bitstreamConMap.clear();
        this.netWires.clear();

        for (BitstreamConfiguration.ConnectedPorts ports : connectedPortsList) {
            AbstractPort portA = this.namePortMap.get(ports.getPortA());
            AbstractPort portB = this.namePortMap.get(ports.getPortB());

            //  ports might be null as the fasm contains start and end ports
            //  of wires, that information is redundant in this model though.
            //  but it might allow for more efficient lookups.
            if (portA == null || portB == null) {
                continue;
            }

            IO ioA = portA.getGeometry().getIo();
            IO ioB = portB.getGeometry().getIo();

            if (!((ioA == IO.INPUT || ioA == IO.INOUT) && (ioB == IO.OUTPUT || ioB == IO.INOUT))) {
                continue;
            }

            Location portLocA = new Location(
                    portA.getGeometry().getRelX(),
                    portA.getGeometry().getRelY()
            );
            Location portLocB = new Location(
                    portB.getGeometry().getRelX(),
                    portB.getGeometry().getRelY()
            );
            DiscreteLocation discreteLocA = new DiscreteLocation(portLocA);
            DiscreteLocation discreteLocB = new DiscreteLocation(portLocB);

            // For jump ports
            if (discreteLocA.equals(discreteLocB)) continue;

            this.bitstreamConMap.computeIfAbsent(discreteLocA, k -> new HashSet<>());
            this.bitstreamConMap.computeIfAbsent(discreteLocB, k -> new HashSet<>());

            Fabric fabric = this.getTile().getFabric();
            fabric.highlightWire(portA);
            fabric.highlightWire(portB);

            boolean drawCurve = false;
            int offsetX = 0;
            int offsetY = 0;

            boolean xEqual = portLocA.getX() == portLocB.getX();
            boolean atLeftOrRightBorder = portLocA.getX() == 0 || portLocA.getX() == this.geometry.getWidth();

            boolean yEqual = portLocA.getY() == portLocB.getY();
            boolean atTopOrBottomBorder = portLocA.getY() == 0 || portLocA.getY() == this.geometry.getHeight();

            if (xEqual && atLeftOrRightBorder) {
                offsetX = portLocA.getX() == 0 ? 1 : -1;
                drawCurve = true;
            } else if (yEqual && atTopOrBottomBorder) {
                offsetY = portLocA.getY() == 0 ? 1 : -1;
                drawCurve = true;
            }

            Config config = Config.getInstance();

            if (drawCurve) {
                Line startLine = new LineBuilder()
                        .setStart(portLocA.getX(), portLocA.getY())
                        .setEnd(portLocA.getX() + offsetX, portLocA.getY() + offsetY)
                        .setStroke(config.getUserDesignColor(), 0.2)
                        .build();
                this.displayedBitstreamConfig.add(startLine);
                this.bitstreamConMap.get(discreteLocA).add(startLine);
                this.bitstreamConMap.get(discreteLocB).add(startLine);

                Line endLine = new LineBuilder()
                        .setStart(portLocB.getX() + offsetX, portLocB.getY() + offsetY)
                        .setEnd(portLocB.getX(), portLocB.getY())
                        .setStroke(config.getUserDesignColor(), 0.2)
                        .build();
                this.displayedBitstreamConfig.add(endLine);
                this.bitstreamConMap.get(discreteLocA).add(endLine);
                this.bitstreamConMap.get(discreteLocB).add(endLine);

                int startAngle = -offsetX * 90 + offsetY * (90 + 90 * offsetY);
                double diffX = Math.abs(portLocA.getX() - portLocB.getX());
                double diffY = Math.abs(portLocA.getY() - portLocB.getY());
                double radiusX = (diffY / 4) * (offsetX * offsetX) + (diffX / 2) * (offsetY * offsetY);
                double radiusY = (diffX / 4) * (offsetY * offsetY) + (diffY / 2) * (offsetX * offsetX);

                Arc arc = new ArcBuilder()
                        .setCenterX((portLocA.getX() + portLocB.getX()) / 2 + offsetX)
                        .setCenterY((portLocA.getY() + portLocB.getY()) / 2 + offsetY)
                        .setRadius(radiusX, radiusY)
                        .setStartAngle(startAngle)
                        .setLength(180)
                        .setStroke(config.getUserDesignColor(), 0.2)
                        .setFill(Color.TRANSPARENT)
                        .build();

                this.displayedBitstreamConfig.add(arc);
                this.bitstreamConMap.get(discreteLocA).add(arc);
                this.bitstreamConMap.get(discreteLocB).add(arc);

            } else {
                Line line = new LineBuilder()
                        .setStart(portLocA.getX(), portLocA.getY())
                        .setEnd(portLocB.getX(), portLocB.getY())
                        .setStroke(config.getUserDesignColor(), 0.2)
                        .build();
                this.displayedBitstreamConfig.add(line);
                this.bitstreamConMap.get(discreteLocA).add(line);
                this.bitstreamConMap.get(discreteLocB).add(line);
            }
        }
        this.getChildren().addAll(this.displayedBitstreamConfig);
    }

    public void clearNets() {
        Config config = Config.getInstance();
        Property<Color> colorProp = config.getUserDesignColor();

        for (Shape wire : this.netWires) {
            wire.strokeProperty().bind(colorProp);
        }
        this.netWires.clear();
    }

    public Location displayNetEntry(String portNameA, String portNameB) {
        AbstractPort portA = this.namePortMap.get(portNameA);
        AbstractPort portB = this.namePortMap.get(portNameB);

        if (portA == null || portB == null) return null;

        IO ioA = portA.getGeometry().getIo();
        IO ioB = portB.getGeometry().getIo();

        if (!((ioA == IO.INPUT || ioA == IO.INOUT) && (ioB == IO.OUTPUT || ioB == IO.INOUT))) {
            return null;
        }

        Location portLocA = new Location(
                portA.getGeometry().getRelX(),
                portA.getGeometry().getRelY()
        );
        Location portLocB = new Location(
                portB.getGeometry().getRelX(),
                portB.getGeometry().getRelY()
        );
        Fabric fabric = this.getTile().getFabric();
        this.netWires.addAll(fabric.highlightNet(portA));
        this.netWires.addAll(fabric.highlightNet(portB));

        Location average = Location.averageOf(portLocA, portLocB);
        average.add(this.geometry.getRelX(), this.geometry.getRelY());
        average.add(this.getTile().getLocation());

        DiscreteLocation discreteLocA = new DiscreteLocation(portLocA);
        DiscreteLocation discreteLocB = new DiscreteLocation(portLocB);

        Set<Shape> consAtPorts = new HashSet<>(
                this.bitstreamConMap.get(discreteLocA)
        );
        Set<Shape> consAtPortB = this.bitstreamConMap.get(discreteLocB);
        consAtPortB = consAtPortB == null ? Set.of() : consAtPortB;
        consAtPorts.retainAll(consAtPortB);

        Config config = Config.getInstance();

        for (Shape con : consAtPorts) {
            con.strokeProperty().bind(config.getUserDesignMarkedColor());
        }
        this.netWires.addAll(consAtPorts);

        return average;
    }

    public void filterAndAdd(List<FabricElement> result, ElementType type, Pattern regex) {
        if (type == ElementType.ANY || type == ElementType.PORT) {
            for (Node node : this.getChildren()) {
                if (!(node instanceof SmPort smPort)) continue;

                String portName = smPort.getGeometry().getName();
                Matcher matcher = regex.matcher(portName);

                if (matcher.matches() || portName.contains(regex.pattern())) {
                    result.add(smPort);
                }
            }
        }

        if (type == ElementType.ANY || type == ElementType.SWITCH_MATRIX) {
            String name = this.geometry.getName();
            Matcher matcher = regex.matcher(name);

            if (matcher.matches() || name.contains(regex.pattern())) {
                result.add(this);
            }
        }
    }

    @Override
    public String getName() {
        return this.geometry.getName();
    }

    @Override
    public ElementType getType() {
        return ElementType.SWITCH_MATRIX;
    }

    @Override
    public Location getGlobalLocation() {
        Tile tile = this.getTile();

        double globalX = tile.getTranslateX()
                + this.getTranslateX()
                + this.getSmRect().getWidth() / 2;
        double globalY = tile.getTranslateY()
                + this.getTranslateY()
                + this.getSmRect().getHeight() / 2;

        Location globalLocation = new Location(globalX, globalY);
        return globalLocation;
    }

    @Override
    public double getViewableZoom() {
        return 2.5;
    }
}
