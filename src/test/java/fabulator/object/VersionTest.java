package fabulator.object;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class VersionTest {

    @Test
    @DisplayName("Test creating Version from String")
    public void testCreateVersionFromString() {
        Version v1 = new Version("1.2.3");

        assertEquals(1, v1.getMajor());
        assertEquals(2, v1.getMinor());
        assertEquals(3, v1.getPatch());

        Version v2 = new Version("3.22.111");

        assertEquals(3, v2.getMajor());
        assertEquals(22, v2.getMinor());
        assertEquals(111, v2.getPatch());
    }

    @Test
    @DisplayName("Test comparing Version objects")
    public void testComparingVersionObjects() {
        Version v1 = new Version("1.2.3");
        Version v2 = new Version("1.2.3");
        Version v3 = new Version("2.2.4");
        Version v4 = new Version("0.99.99");
        Version v5 = new Version("0.100.0");

        assertFalse(Version.outdated(v1, v2));
        assertFalse(Version.outdated(v1, v3));
        assertTrue(Version.outdated(v3, v1));
        assertTrue(Version.outdated(v1, v4));
        assertFalse(Version.outdated(v4, v5));
        assertTrue(Version.outdated(v5, v4));
    }

    @Test
    @DisplayName("Test Version to String conversion")
    public void testVersionToStringConversion() {
        assertEquals("1.2.3", new Version("1.2.3").toString());
        assertEquals("1.22.333", new Version("1.22.333").toString());
        assertEquals("4.0.4", new Version("4.0.4").toString());
        assertEquals("0.0.0", new Version("0.0.0").toString());
    }

    @Test
    @DisplayName("Test Version creation with faulty input")
    public void testVersionCreationWithFaultyInput() {
        assertThrows(NumberFormatException.class, () -> new Version("a.b.c"));
        assertThrows(NumberFormatException.class, () -> new Version("1.2.3.4"));
        assertThrows(NumberFormatException.class, () -> new Version("5.6"));
        assertThrows(NumberFormatException.class, () -> new Version(""));
        assertThrows(NumberFormatException.class, () -> new Version(null));
    }
}
