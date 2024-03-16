package fabulator.ui.fabric;

import fabulator.geometry.*;
import fabulator.lookup.BitstreamConfiguration;
import fabulator.lookup.LineMap;
import fabulator.object.Location;
import fabulator.settings.Config;
import fabulator.ui.builder.LineBuilder;
import fabulator.ui.builder.RectangleBuilder;
import fabulator.ui.fabric.element.ElementType;
import fabulator.ui.fabric.element.FabricElement;
import fabulator.ui.view.fabric.LodManager;
import fabulator.util.TileColorUtils;
import javafx.collections.ObservableList;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * A Class representing a tile of the fabric.
 * Parts of the tiles, such as switch matrices,
 * are modelled using classes. for wires,
 * however, the incurred performance penalty
 * is too large when a JavaFX Group is set up
 * for each wire, thus wires are set up here.
 */
@Getter
@Setter
public class Tile extends Group implements FabricElement {

    private Fabric fabric;
    private int fabricX;
    private int fabricY;

    private TileGeometry geometry;
    private Location location;
    private Rectangle tileRect;
    private Bounds tileBounds;

    private SwitchMatrix switchMatrix;
    private Rectangle lowLodSubstitute;

    private List<Bel> bels;

    public Tile(TileGeometry geometry, Location tileLoc, Fabric fabric, int fabricX, int fabricY) {
        this.geometry = geometry;
        this.location = tileLoc;
        this.fabric = fabric;
        this.fabricX = fabricX;
        this.fabricY = fabricY;
        this.build();
    }

    private void build() {
        this.setTranslateX(this.location.getX());
        this.setTranslateY(this.location.getY());

        Color tileColor = Color.BLACK;
        if (Config.getInstance().getColorCodeTiles().get()) {
            tileColor = TileColorUtils.colorOfTile(this.geometry.getName());
        }

        this.tileRect = new RectangleBuilder()
                .setDims(this.geometry.getWidth(), this.geometry.getHeight())
                .setStroke(Color.LIGHTGRAY, 0.1)
                .setFill(tileColor)
                .setOpacity(0.2)
                .build();
        this.getChildren().add(this.tileRect);

        this.tileBounds = new BoundingBox(
                this.location.getX(),
                this.location.getY(),
                this.geometry.getWidth(),
                this.geometry.getHeight()
        );

        SwitchMatrixGeometry smGeom = this.geometry.getSmGeometry();
        this.switchMatrix = new SwitchMatrix(smGeom, this);
        this.getChildren().add(this.switchMatrix);

        this.lowLodSubstitute = new RectangleBuilder()
                .setDims(smGeom.getWidth(), smGeom.getHeight())
                .setArcDims(6, 6)
                .setTranslateX(smGeom.getRelX())
                .setTranslateY(smGeom.getRelY())
                .setStroke(Color.WHITE, 1)
                .setFill(Color.BLACK)
                .build();
        this.lowLodSubstitute.setVisible(false);
        this.getChildren().add(this.lowLodSubstitute);

        this.bels = new ArrayList<>();
        for (BelGeometry belGeom : this.geometry.getBelGeometryList()) {
            Bel bel = new Bel(belGeom, this);
            this.bels.add(bel);
            this.getChildren().add(bel);
        }

        ObservableList<Node> children = this.getChildren();
        LineMap lineMap = this.fabric.getLineMap();

        boolean genWireTooltips = Config.getInstance().getGenWireTooltips().get();

        for (WireGeometry wireGeom : this.geometry.getWireGeometryList()) {
            List<Location> path = wireGeom.getPath();

            for (int pathCounter = path.size(); pathCounter >= 2; pathCounter--) {
                Location start = path.get(pathCounter - 1);
                Location end = path.get(pathCounter - 2);

                Line line = new LineBuilder()
                        .setStart(start.getX(), start.getY())
                        .setEnd(end.getX(), end.getY())
                        .setStroke(Color.WHITE, 0.2)
                        .addTooltipIf(genWireTooltips, wireGeom.getName())
                        .build();

                line.setOnContextMenuRequested(event -> this.fabric.openLineMenu(line, event));

                children.add(line);
                lineMap.add(line);
            }
        }
    }

    public void displayBitstreamConfig(List<BitstreamConfiguration.ConnectedPorts> connectedPortsList) {
        this.switchMatrix.displayBitstreamConfig(connectedPortsList);
    }

    public void clearNets() {
        this.switchMatrix.clearNets();
    }

    public Location displayNetEntry(BitstreamConfiguration.ConnectedPorts connectedPorts) {
        Location location = this.switchMatrix.displayNetEntry(
                connectedPorts.getPortA(),
                connectedPorts.getPortB()
        );
        return location;
    }

    public void setLod(double zoomLevel, Bounds viewPortBounds) {
        LodManager.Lod lod = LodManager.Lod.of(zoomLevel);

        switch (lod) {
            case MEDIUM -> {
                this.lowLodSubstitute.setVisible(true);
                this.switchMatrix.setVisible(false);
            }
            case HIGH -> {
                this.switchMatrix.setVisible(true);
                this.lowLodSubstitute.setVisible(false);
            }
            default -> {
            }
        }

        boolean render = this.tileBounds.intersects(viewPortBounds);
        this.setVisible(render);
    }

    public void filterAndAdd(List<FabricElement> result, ElementType type, Pattern regex) {
        switch (type) {
            case PORT, SWITCH_MATRIX -> this.switchMatrix.filterAndAdd(result, type, regex);
            case BEL -> {
                for (Bel bel : this.bels) {
                    bel.filterAndAdd(result, type, regex);
                }
            }
            case ANY -> {
                this.switchMatrix.filterAndAdd(result, type, regex);
                for (Bel bel : this.bels) {
                    bel.filterAndAdd(result, type, regex);
                }
            }
        }
    }

    @Override
    public String getName() {
        return this.geometry.getName();
    }

    @Override
    public ElementType getType() {
        return ElementType.TILE;
    }

    @Override
    public Location getGlobalLocation() {
        double globalX = this.getTranslateX() + this.getGeometry().getWidth() / 2;
        double globalY = this.getTranslateY() + this.getGeometry().getHeight() / 2;

        Location globalLocation = new Location(globalX, globalY);
        return globalLocation;
    }

    @Override
    public double getViewableZoom() {
        return 2;
    }
}
