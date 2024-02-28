package fabulator.util;

import javafx.scene.paint.Color;

public class TileColorUtils {

    public static Color colorOfTile(String tileName) {
        String nameUpper = tileName.toUpperCase();

        Color color = Color.PURPLE;

        if (nameUpper.contains("TERM")) {
            color = Color.LIGHTGRAY;
        } else if (nameUpper.contains("IO")) {
            color = Color.LIGHTYELLOW;
        } else if (nameUpper.contains("LUT")) {
            color = Color.LIGHTBLUE;
        } else if (nameUpper.contains("REG")) {
            color = Color.INDIANRED;
        } else if (nameUpper.contains("DSP")) {
            color = Color.LIGHTGREEN;
        }
        return color;
    }
}

