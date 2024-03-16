package fabulator.ui.view.fabric;


import javafx.geometry.Bounds;
import lombok.Getter;

/**
 * A class that manages the displayed level of detail of the fabric,
 * depending on how far the managing view zooms in on it.
 */
public class LodManager {

    private static final int UPDATE_INTERVAL_MILLIS = 40;

    public interface LodHandler {
        void handle(double zoomLevel, Bounds viewPortBounds);
    }

    public enum Lod {
        LOW(0.15), // for Bels (Rect -> Rects)
        MEDIUM(1.2), // for Wires (Rect -> Lines)
        HIGH(1.7); // for Ports (Line -> Circles)

        @Getter
        private double threshold;

        Lod(double threshold) {
            this.threshold = threshold;
        }

        public static Lod of(double zoomLevel) {
            if (zoomLevel < LOW.threshold) {
                return Lod.LOW;
            } else if (zoomLevel < MEDIUM.threshold) {
                return Lod.MEDIUM;
            } else {
                return Lod.HIGH;
            }
        }
    }

    private LodHandler lodHandler;

    private long lastUpdated;

    public LodManager() {
        this.lastUpdated = System.currentTimeMillis();
        this.setDefaultHandler();
    }

    private void setDefaultHandler() {
        this.lodHandler = (zoomLevel, viewPortCenter) -> {};
    }

    public void onViewChanged(double zoomLevel, Bounds viewPortBounds) {
        long millis = System.currentTimeMillis();

        if (millis - this.lastUpdated > UPDATE_INTERVAL_MILLIS) {
            this.updateLod(zoomLevel, viewPortBounds);
            this.lastUpdated = millis;
        }
    }

    public void setOnUpdate(LodHandler handler) {
        this.lodHandler = handler;
    }

    private void updateLod(double zoomLevel, Bounds viewPortBounds) {
        this.lodHandler.handle(zoomLevel, viewPortBounds);
    }
}
