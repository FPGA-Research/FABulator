package fabulator.ui.fabric;

import fabulator.geometry.*;
import fabulator.lookup.BitstreamConfiguration;
import fabulator.lookup.LineMap;
import fabulator.object.DiscreteLocation;
import fabulator.object.Location;
import fabulator.settings.Config;
import fabulator.ui.builder.MenuItemBuilder;
import fabulator.ui.builder.RectangleBuilder;
import fabulator.ui.fabric.element.ElementType;
import fabulator.ui.fabric.element.FabricElement;
import fabulator.ui.fabric.manager.WireManager;
import fabulator.ui.fabric.port.AbstractPort;
import fabulator.ui.fabric.manager.LodManager;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.util.Pair;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * A class representing a fabric.
 */
@Getter
@Setter
public class Fabric extends Group {

    public static final double MARKER_PADDING = Math.pow(2, 16);

    private FabricGeometry geometry;
    private LineMap lineMap;
    private Line currentlySelected;
    private ContextMenu lineMenu;

    private LodManager lodManager = new LodManager();
    private WireManager wireManager = new WireManager();

    private Rectangle topLeft;
    private Rectangle topRight;
    private Rectangle bottomLeft;
    private Rectangle bottomRight;

    private List<List<Tile>> tiles;

    public Fabric(FabricGeometry geometry) {
        this.geometry = geometry;

        this.initializeLineHandling();
        this.build();
        this.buildMarkers();
        this.initLodSystem();
    }

    private void initializeLineHandling() {
        int initialCapacity = (geometry.getNumberOfLines() * 3) / 2;
        this.lineMap = new LineMap(initialCapacity);

        ColorPicker colorPicker = new ColorPicker();
        EventHandler<ActionEvent> colorPickedHandler = event -> {
            Color pickedColor = colorPicker.getValue();
            Property<Color> pickedColorProp = new SimpleObjectProperty<>(pickedColor);
            this.colorWire(pickedColorProp);
        };

        MenuItem colorItem = new MenuItemBuilder()
                .setGraphic(colorPicker)
                .setOnAction(colorPickedHandler)
                .build();

        this.lineMenu = new ContextMenu(colorItem);
    }

    public void build() {
        List<List<Location>> tileLocations = this.geometry.getTileLocations();
        List<List<String>> tileNames = this.geometry.getTileNames();
        Map<String, TileGeometry> tileGeomMap = this.geometry.getTileGeomMap();

        this.tiles = new ArrayList<>();
        for (int x = 0; x < tileLocations.size(); x++) {
            List<Location> locRow = tileLocations.get(x);

            List<Tile> tileRow = new ArrayList<>();
            for (int y = 0; y < locRow.size(); y++) {
                Location tileLoc = tileLocations.get(x).get(y);
                if (tileLoc == null) {
                    tileRow.add(null);
                    continue;
                }

                String tileName = tileNames.get(x).get(y);
                TileGeometry tileGeom = tileGeomMap.get(tileName);
                Tile tile = new Tile(tileGeom, tileLoc, this, x, y);
                tileRow.add(tile);
                this.getChildren().add(tile);
            }
            this.tiles.add(tileRow);
        }
    }

    private void buildMarkers() {
        this.topLeft = new RectangleBuilder()
                .setDims(0, 0)
                .setFill(Color.TRANSPARENT)
                .setTranslateX(-MARKER_PADDING)
                .setTranslateY(-MARKER_PADDING)
                .build();

        this.topRight = new RectangleBuilder()
                .setDims(0, 0)
                .setFill(Color.TRANSPARENT)
                .setTranslateX(this.geometry.getWidth() + MARKER_PADDING)
                .setTranslateY(-MARKER_PADDING)
                .build();

        this.bottomLeft = new RectangleBuilder()
                .setDims(0, 0)
                .setFill(Color.TRANSPARENT)
                .setTranslateX(-MARKER_PADDING)
                .setTranslateY(this.geometry.getHeight() + MARKER_PADDING)
                .build();

        this.bottomRight = new RectangleBuilder()
                .setDims(0, 0)
                .setFill(Color.TRANSPARENT)
                .setTranslateX(this.geometry.getWidth() + MARKER_PADDING)
                .setTranslateY(this.geometry.getHeight() + MARKER_PADDING)
                .build();

        this.getChildren().addAll(
                this.topLeft,
                this.topRight,
                this.bottomLeft,
                this.bottomRight
        );
    }

    private void initLodSystem() {
        this.lodManager.setOnUpdate(((zoomLevel, viewPortBounds) -> {
            for (List<Tile> tileList : this.tiles) {
                for (Tile tile : tileList) {
                    if (tile != null) {
                        tile.setLod(zoomLevel, viewPortBounds);
                    }
                }
            }
            this.wireManager.updateWires(zoomLevel);
        }));
    }

    public void openLineMenu(Line line, ContextMenuEvent event) {
        this.currentlySelected = line;
        this.lineMenu.show(line, event.getScreenX(), event.getScreenY());
    }

    private void colorWire(Property<Color> colorProperty) {
        assert this.currentlySelected != null;

        Set<Line> toBeColored = this.lineMap.allLinesAt(this.currentlySelected);
        for (Line wire : toBeColored) {
            this.wireManager.highlight(wire, colorProperty);
        }
    }

    public void colorWire(AbstractPort port, Property<Color> colorProperty) {
        Set<Line> toBeColored = this.lineMap.allLinesAt(port);
        for (Line wire : toBeColored) {
            this.wireManager.highlight(wire, colorProperty);
        }
    }

    public Set<Line> highlightNet(AbstractPort port) {
        Config config = Config.getInstance();
        Property<Color> colorProp = config.getUserDesignMarkedColor();

        Set<Line> toBeColored = this.lineMap.allLinesAt(port);
        for (Line wire : toBeColored) {
            this.wireManager.highlight(wire, colorProp);
        }
        return toBeColored;
    }

    private void clearBitstreamConfig() {
        for (List<Tile> tileList : this.tiles) {
            for (Tile tile : tileList) {
                if (tile != null) {
                    tile.clearBitstreamConfig();
                }
            }
        }
    }

    public void displayBitstreamConfig(BitstreamConfiguration config) {
        this.clearBitstreamConfig();

        for (Map.Entry<DiscreteLocation, List<BitstreamConfiguration.ConnectedPorts>> entry : config.getConnectivityMap().entrySet()) {
            DiscreteLocation location = entry.getKey();
            Tile tile = this.getTile(location.getX(), location.getY());
            tile.displayBitstreamConfig(entry.getValue());
        }
    }

    // TODO: maybe improve efficiency by saving list of tiles
    //  that have been affected by displayNet and only apply to those
    public void clearNets() {
        for (List<Tile> tileList : this.tiles) {
            for (Tile tile : tileList) {
                if (tile != null) {
                    tile.clearNets();
                }
            }
        }
    }

    public Location displayNet(fabulator.lookup.Net net) {
        List<Pair<DiscreteLocation, BitstreamConfiguration.ConnectedPorts>> entries = net.getEntries();
        Location averageLocation = new Location();

        int displayedEntries = entries.size();

        for (Pair<DiscreteLocation, BitstreamConfiguration.ConnectedPorts> entry : entries) {
            DiscreteLocation location = entry.getKey();
            Tile tile = this.getTile(location.getX(), location.getY());
            Location entryLocation = tile.displayNetEntry(entry.getValue());

            if (entryLocation != null) {
                averageLocation.add(entryLocation);
            } else {
                displayedEntries--;
            }
        }
        averageLocation.scaleInverse(displayedEntries);
        return averageLocation;
    }

    public void updateLod(double zoomLevel, Bounds viewPortBounds) {
        this.lodManager.onViewChanged(zoomLevel, viewPortBounds);
    }

    public Tile getTile(int x, int y) {
        /*
        Tiles are saved as a list of rows, therefore we
        first select the row (y) and then the column (x)
        */
        return this.tiles.get(y).get(x);
    }

    public void filterAndAdd(List<FabricElement> result, ElementType type, Pattern regex) {
        for (List<Tile> tileList : this.tiles) {
            for (Tile tile : tileList) {
                if (tile != null) {
                    tile.filterAndAdd(result, type, regex);
                }
            }
        }
    }
}
