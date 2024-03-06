package fabulator.ui.fabric;

import fabulator.geometry.BelGeometry;
import fabulator.geometry.Location;
import fabulator.geometry.PortGeometry;
import fabulator.ui.builder.RectangleBuilder;
import fabulator.ui.fabric.element.ElementType;
import fabulator.ui.fabric.element.FabricElement;
import fabulator.ui.fabric.port.BelPort;
import fabulator.util.FileUtils;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class representing a single bel of a tile.
 */
@Getter
@Setter
public class Bel extends Group implements FabricElement {

    private BelGeometry geometry;
    private Tile tile;

    private Rectangle belRect;

    public Bel(BelGeometry geometry, Tile tile) {
        this.geometry = geometry;
        this.tile = tile;
        this.build();
    }

    private void build() {
        this.setTranslateX(this.geometry.getRelX());
        this.setTranslateY(this.geometry.getRelY());

        this.belRect = new RectangleBuilder()
                .setDims(this.geometry.getWidth(), this.geometry.getHeight())
                .setArcDims(3, 3)
                .setStroke(Color.WHITE, 1)
                .setFill(Color.BLACK)
                .addTooltip(this.geometry.getName())
                .setOnMouseClicked(this::onClicked)
                .build();

        this.getChildren().add(this.belRect);

        for (PortGeometry portGeom : this.geometry.getPortGeometryList()) {
            BelPort belPort = new BelPort(portGeom, this);
            this.getChildren().add(belPort);
        }
    }

    private void onClicked(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            FileUtils.openHdlFile(this.geometry.getSrc());
        }
    }

    public void filterAndAdd(List<FabricElement> result, ElementType type, Pattern regex) {
        if (type == ElementType.ANY || type == ElementType.PORT) {
            for (Node node : this.getChildren()) {
                if (!(node instanceof BelPort belPort)) continue;

                String portName = belPort.getGeometry().getName();
                Matcher matcher = regex.matcher(portName);

                if (matcher.matches() || portName.contains(regex.pattern())) {
                    result.add(belPort);
                }
            }
        }

        if (type == ElementType.ANY || type == ElementType.BEL) {
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
        return ElementType.BEL;
    }

    @Override
    public Location getGlobalLocation() {
        Tile tile = this.getTile();

        double globalX = tile.getTranslateX()
                + this.getTranslateX()
                + this.getBelRect().getWidth() / 2;
        double globalY = tile.getTranslateY()
                + this.getTranslateY()
                + this.getBelRect().getHeight() / 2;

        Location globalLocation = new Location(globalX, globalY);
        return globalLocation;
    }

    @Override
    public double getViewableZoom() {
        return 12;
    }
}
