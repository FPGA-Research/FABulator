package testutil;

import javafx.application.Platform;

public class TestUtils {

    public static void initializeJavaFX() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException ignored) {
            // JavaFX already running
        }
    }
}
