package fabulator.ui.fabric.element;

import fabulator.language.Text;
import fabulator.ui.menu.Stateful;
import javafx.beans.property.StringProperty;
import lombok.Getter;

@Getter
public enum ElementType implements Stateful {
    ANY(Text.ANY),
    PORT(Text.PORT),
    BEL(Text.BEL),
    SWITCH_MATRIX(Text.SWITCH_MATRIX),
    TILE(Text.TILE);

    private final Text text;

    ElementType(Text text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return this.getText().stringProperty().get();
    }

    @Override
    public StringProperty getState() {
        return this.getText().stringProperty();
    }
}
