package fabulator.ui.builder;

import javafx.beans.property.ObjectProperty;
import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;
import javafx.util.Builder;

public class ColorPickerBuilder implements Builder<ColorPicker> {

    private final ColorPicker colorPicker;

    public ColorPickerBuilder() {
        this.colorPicker = new ColorPicker();
    }

    public ColorPickerBuilder bindBidirectional(ObjectProperty<Color> colorProperty) {
        this.colorPicker.valueProperty().bindBidirectional(colorProperty);
        return this;
    }

    @Override
    public ColorPicker build() {
        return colorPicker;
    }
}
