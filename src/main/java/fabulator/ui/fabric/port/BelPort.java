package fabulator.ui.fabric.port;

import fabulator.ui.fabric.Bel;
import fabulator.settings.Config;
import fabulator.ui.fabric.Tile;
import fabulator.geometry.Location;
import fabulator.geometry.PortGeometry;
import javafx.beans.property.Property;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import lombok.Getter;

/**
 * A class representing a single port of a bel.
 */
@Getter
public class BelPort extends AbstractPort {

    private Bel bel;

    public BelPort(PortGeometry geometry, Bel bel) {
        super(geometry);

        this.bel = bel;
        this.build();
    }

    private void build() {
        Property<Color> portColorProperty = Config.getInstance().getBelPortColor();
        this.fillProperty().bind(portColorProperty);

        String tooltipString = String.format(
                "%s\n\nSource: \t%s\nSink: \t%s\nIO: \t\t%s",
                this.geometry.getName(),
                this.geometry.getSourceName(),
                this.geometry.getDestName(),
                this.geometry.getIo()
        );
        Tooltip tooltip = new Tooltip(tooltipString);
        tooltip.setShowDuration(Duration.INDEFINITE);
        Tooltip.install(this, tooltip);
    }

    @Override
    public Location getGlobalLocation() {
        Bel bel = this.getBel();
        Tile tile = bel.getTile();

        double globalX = tile.getTranslateX()
                + bel.getTranslateX()
                + this.getTranslateX();
        double globalY = tile.getTranslateY()
                + bel.getTranslateY()
                + this.getTranslateY();

        Location globalLocation = new Location(globalX, globalY);
        return globalLocation;
    }
}
