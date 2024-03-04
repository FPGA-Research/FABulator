package fabulator.ui.builder;

import javafx.beans.property.Property;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Arc;
import javafx.util.Builder;

public class ArcBuilder implements Builder<Arc> {

    private final Arc arc;

    public ArcBuilder() {
        this.arc = new Arc();
    }

    public ArcBuilder setCenterX(double centerX) {
        this.arc.setCenterX(centerX);
        return this;
    }

    public ArcBuilder setCenterY(double centerY) {
        this.arc.setCenterY(centerY);
        return this;
    }

    public ArcBuilder setRadius(double radiusX, double radiusY) {
        this.arc.setRadiusX(radiusX);
        this.arc.setRadiusY(radiusY);
        return this;
    }

    public ArcBuilder setStartAngle(double angle) {
        this.arc.setStartAngle(angle);
        return this;
    }

    public ArcBuilder setLength(double length) {
        this.arc.setLength(length);
        return this;
    }

    public ArcBuilder setFill(Paint paint) {
        this.arc.setFill(paint);
        return this;
    }

    public ArcBuilder setStroke(Paint paint, double width) {
        this.arc.setStroke(paint);
        this.arc.setStrokeWidth(width);
        return this;
    }

    public ArcBuilder setStroke(Property<Color> colorProp, double width) {
        this.arc.strokeProperty().bind(colorProp);
        this.arc.setStrokeWidth(width);
        return this;
    }

    @Override
    public Arc build() {
        return this.arc;
    }
}
