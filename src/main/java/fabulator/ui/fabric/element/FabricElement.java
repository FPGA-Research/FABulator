package fabulator.ui.fabric.element;

import fabulator.object.Location;

public interface FabricElement {

    String getName();

    ElementType getType();

    Location getGlobalLocation();

    double getViewableZoom();
}
