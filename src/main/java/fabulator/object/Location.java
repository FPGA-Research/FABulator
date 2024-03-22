package fabulator.object;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * A class for storing a location on the fabric.
 * These should be handled carefully, as they might be relative
 * to their context, e.g. a location of a switch matrix is a
 * position relative to that of its tile.
 */
@Getter
@Setter
@AllArgsConstructor
public class Location {

    private double x;
    private double y;

    public Location() {
        this.x = 0;
        this.y = 0;
    }

    public void add(Location other) {
        this.x += other.getX();
        this.y += other.getY();
    }

    public void add(double x, double y) {
        this.x += x;
        this.y += y;
    }

    public void scaleInverse(double value) {
        this.x /= value;
        this.y /= value;
    }

    public static Location averageOf(Location... locations) {
        Location average = new Location();

        for (Location location : locations) {
            average.add(location);
        }
        average.scaleInverse(locations.length);
        return average;
    }

    public boolean valid() {
        return !(Double.isNaN(this.x) || Double.isNaN(this.y));
    }

    @Override
    public boolean equals(Object object) {
        boolean equal = false;
        if (object instanceof Location other) {
            equal = (this.x == other.x && this.y == other.y);
        }
        return equal;
    }
}
