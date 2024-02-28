package fabulator.ui.builder;

import javafx.event.EventHandler;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.util.Builder;

public class RectangleBuilder implements Builder<Rectangle> {

    private final Rectangle rectangle;

    public RectangleBuilder() {
        this.rectangle = new Rectangle();
    }

    public RectangleBuilder setTranslateX(double translateX) {
        this.rectangle.setTranslateX(translateX);
        return this;
    }

    public RectangleBuilder setTranslateY(double translateY) {
        this.rectangle.setTranslateY(translateY);
        return this;
    }

    public RectangleBuilder setWidth(double width) {
        this.rectangle.setWidth(width);
        return this;
    }

    public RectangleBuilder setHeight(double height) {
        this.rectangle.setHeight(height);
        return this;
    }

    public RectangleBuilder setDims(double width, double height) {
        this.rectangle.setWidth(width);
        this.rectangle.setHeight(height);
        return this;
    }

    public RectangleBuilder setArcDims(double width, double height) {
        this.rectangle.setArcWidth(width);
        this.rectangle.setArcHeight(height);
        return this;
    }

    public RectangleBuilder setFill(Paint paint) {
        this.rectangle.setFill(paint);
        return this;
    }

    public RectangleBuilder setOpacity(double opacity) {
        this.rectangle.setOpacity(opacity);
        return this;
    }

    public RectangleBuilder setStroke(Paint paint, double width) {
        this.rectangle.setStroke(paint);
        this.rectangle.setStrokeWidth(width);
        return this;
    }

    public RectangleBuilder setMouseTransparent(boolean transparent) {
        this.rectangle.setMouseTransparent(transparent);
        return this;
    }

    public RectangleBuilder setOnMouseClicked(EventHandler<MouseEvent> handler) {
        this.rectangle.setOnMouseClicked(handler);
        return this;
    }

    public RectangleBuilder addTooltip(String text) {
        Tooltip tooltip = new Tooltip(text);
        Tooltip.install(this.rectangle, tooltip);
        return this;
    }

    @Override
    public Rectangle build() {
        return this.rectangle;
    }
}
