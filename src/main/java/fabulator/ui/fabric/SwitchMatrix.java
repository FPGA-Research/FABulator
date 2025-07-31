package fabulator.ui.fabric;

import fabulator.FABulator;
import fabulator.geometry.IO;
import fabulator.geometry.PortGeometry;
import fabulator.geometry.SwitchMatrixGeometry;
import fabulator.language.Text;
import fabulator.lookup.BitstreamConfiguration;
import fabulator.object.*;
import fabulator.parse.SwitchMatrixParser;
import fabulator.settings.Config;
import fabulator.ui.builder.LineBuilder;
import fabulator.ui.builder.RectangleBuilder;
import fabulator.ui.fabric.element.ElementType;
import fabulator.ui.fabric.element.FabricElement;
import fabulator.ui.fabric.port.AbstractPort;
import fabulator.ui.fabric.port.JumpPort;
import fabulator.ui.fabric.port.SmPort;
import fabulator.util.FileUtils;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Pair;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * A Class representing a single switch matrix of a tile.
 */
@Getter
@Setter
public class SwitchMatrix extends Group implements FabricElement {

    private Statistics statistics;

    private Tile tile;
    private SwitchMatrixGeometry geometry;
    private Rectangle smRect;

    private List<Shape> displayedConnections = new ArrayList<>();
    private List<Shape> displayedBitstreamConfig = new ArrayList<>();

    /**
     * A HashMap mapping Port Locations to all Shapes such
     * as Lines, Arcs that represent a connection of
     * the bitstream config to or from that Port.
     */
    private HashMap<DiscreteLocation, Set<Shape>> bitstreamConMap = new HashMap<>();
    private List<AbstractPort> bitstreamConPorts = new ArrayList<>();
    private List<Shape> netWires = new ArrayList<>();

    private HashMap<String, AbstractPort> namePortMap;

    public SwitchMatrix(SwitchMatrixGeometry geometry, Tile tile) {
        this.geometry = geometry;
        this.tile = tile;

        this.build();
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

    // TODO: is this needed?
    private void onClicked(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            FileUtils.openHdlFile(this.geometry.getSrc());

            FABulator.getApplication()
                    .getMainView()
                    .openStats(this.getStatistics());
        }
    }

    private List<Line> buildConnection(
            AbstractPort portA,
            AbstractPort portB,
            Property<Color> colorProperty) {

        List<Line> lines = new ArrayList<>();

        Location locA = new Location(
                portA.getGeometry().getRelX(),
                portA.getGeometry().getRelY()
        );
        Location locB = new Location(
                portB.getGeometry().getRelX(),
                portB.getGeometry().getRelY()
        );

        boolean xEqual = locA.getX() == locB.getX();
        boolean atLeftOrRightBorder = locA.getX() == 0 || locA.getX() == this.geometry.getWidth();
        boolean yEqual = locA.getY() == locB.getY();
        boolean atTopOrBottomBorder = locA.getY() == 0 || locA.getY() == this.geometry.getHeight();
        boolean drawCurve = false;
        int offsetX = 0;
        int offsetY = 0;

        if (xEqual && atLeftOrRightBorder) {
            offsetX = locA.getX() == 0 ? 1 : -1;
            drawCurve = true;
        } else if (yEqual && atTopOrBottomBorder) {
            offsetY = locA.getY() == 0 ? 1 : -1;
            drawCurve = true;
        }

        String tooltipString = String.format(
                "%s, %s",
                portA.getName(),
                portB.getName()
        );

        if (drawCurve) {
            double diffX = Math.abs(locA.getX() - locB.getX());
            double diffY = Math.abs(locA.getY() - locB.getY());
            double midX = (locA.getX() + locB.getX()) / 2;
            double midY = (locA.getY() + locB.getY()) / 2;

            Location midPoint = new Location(
                    midX + 0.5 * diffY * offsetX,
                    midY + 0.5 * diffX * offsetY
            );

            Line lineA = new LineBuilder()
                    .setStart(locA.getX(), locA.getY())
                    .setEnd(midPoint.getX(), midPoint.getY())
                    .setStroke(colorProperty, 0.2)
                    .addTooltip(tooltipString)
                    .build();
            Line lineB = new LineBuilder()
                    .setStart(locB.getX(), locB.getY())
                    .setEnd(midPoint.getX(), midPoint.getY())
                    .setStroke(colorProperty, 0.2)
                    .addTooltip(tooltipString)
                    .build();
            lines.addAll(List.of(lineA, lineB));
        } else {
            Line line = new LineBuilder()
                    .setStart(locA.getX(), locA.getY())
                    .setEnd(locB.getX(), locB.getY())
                    .setStroke(colorProperty, 0.2)
                    .addTooltip(tooltipString)
                    .build();
            lines.add(line);
        }
        return lines;
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

            this.displayedConnections.addAll(
                    this.buildConnection(selectedPort, port, colorProp)
            );
        }
        this.getChildren().addAll(this.displayedConnections);
    }

    public void clearBitstreamConfig() {
        this.getChildren().removeAll(this.displayedBitstreamConfig);
        this.displayedBitstreamConfig.clear();
        this.bitstreamConMap.clear();
        this.netWires.clear();

        Fabric fabric = this.getTile().getFabric();
        Property<Color> regularColor = new SimpleObjectProperty<>(Color.WHITE);
        for (AbstractPort port : this.bitstreamConPorts) {
            fabric.colorWire(port, regularColor);
        }
        this.bitstreamConPorts.clear();
    }

    public void displayBitstreamConfig(List<BitstreamConfiguration.ConnectedPorts> connectedPortsList) {
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

            Config config = Config.getInstance();
            Property<Color> colorProp = config.getUserDesignColor();
            Fabric fabric = this.getTile().getFabric();
            fabric.colorWire(portA, colorProp);
            fabric.colorWire(portB, colorProp);
            this.bitstreamConPorts.add(portA);
            this.bitstreamConPorts.add(portB);

            List<Line> connectionLines = this.buildConnection(portA, portB, colorProp);
            this.displayedBitstreamConfig.addAll(connectionLines);
            this.bitstreamConMap.get(discreteLocA).addAll(connectionLines);
            this.bitstreamConMap.get(discreteLocB).addAll(connectionLines);
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

    @Override
    public Statistics getStatistics() {
        if (this.statistics == null) {
            this.buildStatistics();
        }
        return this.statistics;
    }

    private void buildStatistics() {
        this.statistics = Statistics.of(
                StatisticsCategory.of(
                        Text.NAME,
                        this.geometry.getName()
                ),
                StatisticsCategory.of(
                        Text.AMOUNT_PORTS_TOTAL,
                        this.geometry.getNumberPorts(),
                        StatisticsSection.of(
                                Text.AMOUNT_PORTS_N,
                                this.geometry.getNumberNorthPorts(),
                                entriesOf(this.geometry.getNorthPorts())
                        ),
                        StatisticsSection.of(
                                Text.AMOUNT_PORTS_S,
                                this.geometry.getNumberSouthPorts(),
                                entriesOf(this.geometry.getSouthPorts())
                        ),
                        StatisticsSection.of(
                                Text.AMOUNT_PORTS_E,
                                this.geometry.getNumberEastPorts(),
                                entriesOf(this.geometry.getEastPorts())
                        ),
                        StatisticsSection.of(
                                Text.AMOUNT_PORTS_W,
                                this.geometry.getNumberWestPorts(),
                                entriesOf(this.geometry.getWestPorts())
                        ),
                        StatisticsSection.of(
                                Text.AMOUNT_PORTS_J,
                                this.geometry.getNumberJumpPorts(),
                                entriesOf(this.geometry.getJumpPorts())
                        ),
                        StatisticsSection.of(
                                Text.AMOUNT_PORTS_B,
                                this.geometry.getNumberBelPorts(),
                                entriesOf(this.geometry.getBelPorts())
                        )
                )
        );
    }

    private List<Pair<String, String>> entriesOf(List<PortGeometry> geoms) {
        AtomicInteger counter = new AtomicInteger(1);

        return geoms.stream()
                .map(portGeom -> new Pair<>(
                        String.valueOf(counter.getAndIncrement()),
                        portGeom.getName()
                ))
                .collect(Collectors.toList());
    }
}
