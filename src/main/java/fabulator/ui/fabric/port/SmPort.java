package fabulator.ui.fabric.port;

import fabulator.object.Location;
import fabulator.geometry.PortGeometry;
import fabulator.settings.Config;
import fabulator.ui.fabric.SwitchMatrix;
import fabulator.ui.fabric.Tile;
import javafx.beans.property.Property;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import lombok.Getter;

/**
 * A Class representing a single regular port of a switch matrix.
 */
@Getter
public class SmPort extends AbstractPort {

    protected SwitchMatrix matrix;

    public SmPort(PortGeometry geometry, SwitchMatrix matrix) {
        super(geometry);

        this.matrix = matrix;
        this.build();
    }

    protected void build() {
        Property<Color> portColorProperty = Config.getInstance().getRegularPortColor();
        this.fillProperty().bind(portColorProperty);

        String tooltipString = String.format(
                "%s\n\nSource: \t%s\nSink: \t%s\nIO: \t\t%s",
                this.geometry.getName(),
                this.geometry.getSourceName(),
                this.geometry.getDestName(),
                this.geometry.getIo().name()
        );
        Tooltip tooltip = new Tooltip(tooltipString);
        tooltip.setShowDuration(Duration.INDEFINITE);
        Tooltip.install(this, tooltip);

        this.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                this.matrix.displayConnections(this);
            }
        });
    }

    @Override
    public Location getGlobalLocation() {
        SwitchMatrix switchMatrix = this.getMatrix();
        Tile tile = switchMatrix.getTile();

        double globalX = tile.getTranslateX()
                + switchMatrix.getTranslateX()
                + this.getTranslateX();
        double globalY = tile.getTranslateY()
                + switchMatrix.getTranslateY()
                + this.getTranslateY();

        Location globalLocation = new Location(globalX, globalY);
        return globalLocation;
    }

    @Override
    public double getViewableZoom() {
        return 64;
    }
}
