package fabulator.util;

import javafx.scene.layout.*;

public class LayoutUtils {

    public static Region hSpacer() {
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        return spacer;
    }

    public static Region vSpacer() {
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        return spacer;
    }
}
