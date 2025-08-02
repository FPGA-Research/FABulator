package fabulator.geometry;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LowLodWiresGeometry {

    /**
     * X coordinate of the object, relative to the top left
     * of the {@link TileGeometry} containing it.
     */
    private double relX;

    /**
     * Y coordinate of the object, relative to the top left
     * of the {@link TileGeometry} containing it.
     */
    private double relY;

    /**
     * Width of the object.
     */
    private double width;

    /**
     * Height of the object.
     */
    private double height;

    boolean significant() {
        return this.width >= 2 && this.height >= 2;
    }
}
