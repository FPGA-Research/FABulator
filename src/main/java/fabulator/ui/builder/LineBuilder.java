package fabulator.ui.builder;

import javafx.beans.property.Property;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.util.Builder;

public class LineBuilder implements Builder<Line> {

    private final Line line;

    public LineBuilder() {
        this.line = new Line();
    }

    public LineBuilder setStart(double startX, double startY) {
        this.line.setStartX(startX);
        this.line.setStartY(startY);
        return this;
    }

    public LineBuilder setEnd(double endX, double endY) {
        this.line.setEndX(endX);
        this.line.setEndY(endY);
        return this;
    }

    public LineBuilder setStroke(Paint paint, double width) {
        this.line.setStroke(paint);
        this.line.setStrokeWidth(width);
        return this;
    }

    public LineBuilder setStroke(Property<Color> colorProp, double width) {
        this.line.strokeProperty().bind(colorProp);
        this.line.setStrokeWidth(width);
        return this;
    }

    public LineBuilder addTooltip(String text) {
        Tooltip tooltip = new Tooltip(text);
        Tooltip.install(this.line, tooltip);
        return this;
    }

    public LineBuilder addTooltipIf(boolean condition, String text) {
        LineBuilder lineBuilder = this;

        if (condition) {
            lineBuilder = this.addTooltip(text);
        }
        return lineBuilder;
    }

    @Override
    public Line build() {
        return this.line;
    }
}
