package fabulator.ui.fabric.element;

import fabulator.object.Location;
import fabulator.object.Statistics;

public interface FabricElement {

    String getName();

    ElementType getType();

    Location getGlobalLocation();

    double getViewableZoom();

    Statistics getStatistics();
}
