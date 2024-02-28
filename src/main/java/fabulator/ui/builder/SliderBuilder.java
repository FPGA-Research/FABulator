package fabulator.ui.builder;

import javafx.beans.property.DoubleProperty;
import javafx.scene.control.Slider;
import javafx.util.Builder;

public class SliderBuilder implements Builder<Slider> {

    private final Slider slider;

    public SliderBuilder() {
        this.slider = new Slider();
    }

    public SliderBuilder bindBidirectional(DoubleProperty property) {
        this.slider.valueProperty().bindBidirectional(property);
        return this;
    }

    public SliderBuilder setMin(double minValue) {
        this.slider.setMin(minValue);
        return this;
    }

    public SliderBuilder setMax(double maxValue) {
        this.slider.setMax(maxValue);
        return this;
    }

    @Override
    public Slider build() {
        return this.slider;
    }
}
