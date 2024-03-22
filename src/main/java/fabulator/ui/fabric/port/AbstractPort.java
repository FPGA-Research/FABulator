package fabulator.ui.fabric.port;

import fabulator.geometry.PortGeometry;
import fabulator.object.Statistics;
import fabulator.ui.fabric.element.ElementType;
import fabulator.ui.fabric.element.FabricElement;
import javafx.scene.shape.Circle;
import lombok.Getter;

@Getter
public abstract class AbstractPort extends Circle implements FabricElement {

    protected PortGeometry geometry;

    public AbstractPort(PortGeometry geometry) {
        this.geometry = geometry;

        this.setRadius(0.4);
        this.setTranslateX(this.geometry.getRelX());
        this.setTranslateY(this.geometry.getRelY());
    }

    @Override
    public String getName() {
        return this.geometry.getName();
    }

    @Override
    public ElementType getType() {
        return ElementType.PORT;
    }

    @Override
    public double getViewableZoom() {
        return 64;
    }

    @Override
    public Statistics getStatistics() {         // TODO
        return new Statistics();
    }
}
