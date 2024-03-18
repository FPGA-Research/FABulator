package fabulator.ui.builder;

import fabulator.language.Text;
import javafx.beans.property.BooleanProperty;
import javafx.scene.control.CheckBox;
import javafx.util.Builder;

import java.util.function.Consumer;

public class CheckBoxBuilder implements Builder<CheckBox> {

    private final CheckBox checkBox;

    public CheckBoxBuilder() {
        this.checkBox = new CheckBox();
    }

    public CheckBoxBuilder setSelected(boolean value) {
        this.checkBox.setSelected(value);
        return this;
    }

    public CheckBoxBuilder setText(Text text) {
        this.checkBox.textProperty().bind(text.stringProperty());
        return this;
    }

    public CheckBoxBuilder bindBidirectional(BooleanProperty property) {
        this.checkBox.selectedProperty().bindBidirectional(property);
        return this;
    }

    public CheckBoxBuilder setOnChanged(Consumer<Boolean> handler) {
        this.checkBox.selectedProperty().addListener((obs, old, now) -> {
            handler.accept(now);
        });
        return this;
    }

    @Override
    public CheckBox build() {
        return this.checkBox;
    }
}
