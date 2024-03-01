package fabulator.util;

import fabulator.geometry.DiscreteLocation;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StringUtilsTest {

    @Test
    @DisplayName("Test String to DiscreteLoc conversion")
    void testStringToDiscreteLocConversion() {
        String[] sampleStrings = {
                "X10Y10",
                "X0Y0",
                "X-10Y-10",
                "X1Y111111",
                "X-0Y0"
        };

        DiscreteLocation[] expectedLocs = {
                new DiscreteLocation(10, 10),
                new DiscreteLocation(0, 0),
                new DiscreteLocation(-10, -10),
                new DiscreteLocation(1, 111111),
                new DiscreteLocation(0, 0)
        };

        assertEquals(sampleStrings.length, expectedLocs.length);

        for (int i = 0; i < sampleStrings.length; i++) {
            DiscreteLocation produced = StringUtils.discreteLocFrom(sampleStrings[i]);
            DiscreteLocation expected = expectedLocs[i];
            assertEquals(expected, produced);
        }
    }

    @Test
    @DisplayName("Test Color to HexString conversion")
    void textColorToHexStringConversion() {
        Color[] sampleColors = {
                Color.color(100d/255, 10d/255, 100d/255),
                Color.color(255d/255, 255d/255, 255d/255),
                Color.color(0d/255, 255d/255, 0d/255),
                Color.color(0d/255, 0d/255, 0d/255),
                Color.color(123d/255, 45d/255, 67d/255)
        };

        String[] expectedStrings = {
                "#640A64FF",
                "#FFFFFFFF",
                "#00FF00FF",
                "#000000FF",
                "#7B2D43FF"
        };

        assertEquals(sampleColors.length, expectedStrings.length);

        for (int i = 0; i < sampleColors.length; i++) {
            String produced = StringUtils.toHexString(sampleColors[i]);
            String expected = expectedStrings[i];
            assertEquals(expected, produced);
        }
    }
}
