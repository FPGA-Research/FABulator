package fabulator.util;

import fabulator.geometry.DiscreteLocation;
import fabulator.geometry.Location;
import javafx.scene.paint.Color;

public class StringUtils {

    /**
     * Parses a DiscreteLocation object from
     * a location String such as "X6Y12".
     *
     * @param   locString   The string encoding the location
     * @return              parsed DiscreteLocation object
     */
    public static DiscreteLocation discreteLocFrom(String locString) {
        int xStart = locString.indexOf("X");
        int yStart = locString.indexOf("Y");

        String xString = locString.substring(xStart + 1, yStart);
        String yString = locString.substring(yStart + 1);

        int x = Integer.parseInt(xString);
        int y = Integer.parseInt(yString);

        DiscreteLocation location = new DiscreteLocation(x, y);
        return location;
    }

    /**
     * Parses a Location object from
     * a location String such as "123/456".
     *
     * @param   locString   The string encoding the location
     * @return              parsed Location object
     */
    public static Location locFrom(String locString) {
        String[] coords = locString.split("/");
        Location location = new Location(
                Double.parseDouble(coords[0]),
                Double.parseDouble(coords[1])
        );
        return location;
    }

    /**
     * Creates a hex string for a given color that can
     * be used for (de)serialization.
     *
     * @param   color   The given Color object
     * @return          Hex string for the given color
     */
    public static String toHexString(Color color) {
        int r = ((int) Math.round(color.getRed()     * 255)) << 24;
        int g = ((int) Math.round(color.getGreen()   * 255)) << 16;
        int b = ((int) Math.round(color.getBlue()    * 255)) << 8;
        int a = ((int) Math.round(color.getOpacity() * 255));

        String hexString = String.format("#%08X", (r + g + b + a));
        return hexString;
    }

    /**
     * Returns the whether the given String object is valid,
     * meaning that it is neither null nor the literal "null"
     *
     * @param   string  The given String object
     * @return          Whether the string is valid
     */
    public static boolean valid(String string) {
        return string != null && !string.equals("null");
    }
}
