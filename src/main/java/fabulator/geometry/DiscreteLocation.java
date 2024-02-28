package fabulator.geometry;

import javafx.geometry.Point2D;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * A data structure for storing a 2D integer location.
 * Can be used for efficient location-to-object lookups.
 */
@Getter
@Setter
@AllArgsConstructor
public class DiscreteLocation {

    private int x;
    private int y;

    public DiscreteLocation(Point2D point) {
        this.x = (int) point.getX();
        this.y = (int) point.getY();
    }

    public DiscreteLocation(Location location) {
        this.x = (int) location.getX();
        this.y = (int) location.getY();
    }

    @Override
    public boolean equals(Object object) {
        boolean equal = false;
        if (object instanceof DiscreteLocation other) {
            equal = (this.x == other.x && this.y == other.y);
        }
        return equal;
    }

    /**
     * Hashing using Cantors enumeration of pairs.
     *
     * @return hash, being the result of the Cantor pairing function.
     */
    @Override
    public int hashCode() {
        int w = x + y;
        return (((w + 1) * w) >> 1) + y;
    }
}