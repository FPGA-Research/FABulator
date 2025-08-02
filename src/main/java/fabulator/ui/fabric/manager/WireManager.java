package fabulator.ui.fabric.manager;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class WireManager {

    private static final Property<Color> DEFAULT_COLOR = new SimpleObjectProperty<>(Color.WHITE);
    private static final double DEFAULT_STROKE_WIDTH = 0.2;

    private Set<Shape> highlightedWires = new HashSet<>();

    public void highlight(Shape wire, Property<Color> colorProperty) {
        wire.strokeProperty().bind(colorProperty);
        this.highlightedWires.add(wire);
    }

    public void highlightAll(Collection<Shape> wires, Property<Color> colorProperty) {
        for (Shape wire : wires) {
            this.highlight(wire, colorProperty);
        }
    }

    public void unHighlight(Shape wire) {
        wire.strokeProperty().bind(DEFAULT_COLOR);
        wire.setStrokeWidth(DEFAULT_STROKE_WIDTH);
        this.highlightedWires.remove(wire);
    }

    public void unHighlightAll(Collection<Shape> wires) {
        for (Shape wire : wires) {
            this.unHighlight(wire);
        }
    }

    public void updateWires(double zoomLevel) {
        final double LARGEST_THICKNESS = 4;
        final double SMALLEST_THICKNESS = 0.2;

        double newThickness = (LARGEST_THICKNESS - SMALLEST_THICKNESS) * Math.exp(-zoomLevel) + SMALLEST_THICKNESS;

        for (Shape wire : this.highlightedWires) {
            wire.setStrokeWidth(newThickness);
        }
    }
}
