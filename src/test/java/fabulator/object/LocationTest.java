package fabulator.object;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LocationTest {

    @Test
    @DisplayName("Test creation of Location objects")
    public void testCreationOfLocationObjects() {
        Location l1 = new Location(1,2);
        Location l2 = new Location (42.0, 6.9);

        assertEquals(1, l1.getX());
        assertEquals(2, l1.getY());

        assertEquals(42.0, l2.getX());
        assertEquals(6.9, l2.getY());

        assertTrue(l1.valid());
        assertTrue(l2.valid());
    }

    @Test
    @DisplayName("Test comparison of Location objects")
    public void testComparisonOfLocationObjects() {
        Location l1 = new Location(12.3, 4.56);
        Location l2 = new Location(12.3, 4.56);
        Location l3 = new Location(12.34, 4.56);

        assertEquals(l1, l2);
        assertNotEquals(l2, l3);
    }

    @Test
    @DisplayName("Test invalid Location")
    public void testInvalidLocation() {
        Location l1 = new Location(Double.NaN,12.3456);
        Location l2 = new Location(1, Double.NaN);

        assertFalse(l1.valid());
        assertFalse(l2.valid());
    }

    @Test
    @DisplayName("Test adding Location objects")
    public void testAddingLocationObjects() {
        Location l1 = new Location(-1, 2);
        Location l2 = new Location(44, 33);

        l1.add(l2);
        assertEquals(43, l1.getX());
        assertEquals(35, l1.getY());

        l2.add(-1, 1);
        assertEquals(43, l2.getX());
        assertEquals(34, l2.getY());
    }

    @Test
    @DisplayName("Test scaleInverse method of Location")
    public void testScaleInverseMethodOfLocation() {
        Location l1 = new Location(3, 6);
        Location l2 = new Location(123, -123);

        l1.scaleInverse(3);
        l2.scaleInverse(7);

        assertEquals(1, l1.getX());
        assertEquals(2, l1.getY());

        assertEquals(123d/7d, l2.getX());
        assertEquals(-123d/7d, l2.getY());
    }

    @Test
    @DisplayName("Test average of Location objects")
    public void testAverageOfLocationObjects() {
        Location l1 = new Location(0, 22);
        Location l2 = new Location(-14, 7);
        Location l3 = new Location(3, -2);

        assertEquals(l1, Location.averageOf(l1));
        assertEquals(l2, Location.averageOf(l2));

        Location totalAverage = Location.averageOf(l1, l2, l3);

        assertEquals(-11d/3d, totalAverage.getX());
        assertEquals(27d/3d, totalAverage.getY());
    }
}
