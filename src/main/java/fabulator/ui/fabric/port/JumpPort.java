package fabulator.ui.fabric.port;

import fabulator.ui.fabric.SwitchMatrix;
import fabulator.geometry.PortGeometry;
import fabulator.settings.Config;
import javafx.beans.property.Property;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * A class representing a single (merged) jump port
 * of a switch matrix.
 */
public class JumpPort extends SmPort {

    public JumpPort(PortGeometry geometry, SwitchMatrix matrix) {
        super(geometry, matrix);
    }

    @Override
    protected void build() {
        Property<Color> portColorProperty = Config.getInstance().getJumpPortColor();
        this.fillProperty().bind(portColorProperty);

        String tooltipString = String.format(
                "%s\n%s\n\nSource: \t%s\nSink: \t%s\nIO: \t\t%s",
                this.geometry.getSourceName(),
                this.geometry.getDestName(),
                this.geometry.getSourceName(),
                this.geometry.getDestName(),
                this.geometry.getIo()
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
}
