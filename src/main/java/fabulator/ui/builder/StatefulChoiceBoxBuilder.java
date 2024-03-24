package fabulator.ui.builder;

import fabulator.ui.menu.Stateful;
import fabulator.ui.menu.StatefulChoiceBox;
import javafx.beans.value.ChangeListener;
import javafx.util.Builder;

import java.util.List;

public class StatefulChoiceBoxBuilder<T extends Stateful> implements Builder<StatefulChoiceBox<T>> {

    private final StatefulChoiceBox<T> choiceBox;

    public StatefulChoiceBoxBuilder() {
        this.choiceBox = new StatefulChoiceBox<>();
    }

    public final StatefulChoiceBoxBuilder<T> fillWith(List<T> items) {
        this.choiceBox.fillWith(items);
        return this;
    }

    public StatefulChoiceBoxBuilder<T> setValue(T value) {
        this.choiceBox.setValue(value);
        return this;
    }

    public StatefulChoiceBoxBuilder<T> setOnValueChanged(ChangeListener<T> listener) {
        this.choiceBox.setOnValueChanged(listener);
        return this;
    }

    @Override
    public StatefulChoiceBox<T> build() {
        return choiceBox;
    }
}
