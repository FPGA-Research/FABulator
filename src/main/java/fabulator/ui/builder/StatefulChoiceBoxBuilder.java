package fabulator.ui.builder;

import fabulator.ui.menu.Stateful;
import fabulator.ui.menu.StatefulChoiceBox;
import javafx.beans.value.ChangeListener;
import javafx.util.Builder;

public class StatefulChoiceBoxBuilder<T extends Stateful> implements Builder<StatefulChoiceBox<T>> {

    private StatefulChoiceBox<T> choiceBox;

    public StatefulChoiceBoxBuilder() {
        this.choiceBox = new StatefulChoiceBox<>();
    }

    public StatefulChoiceBoxBuilder<T> fillWith(T... items) {
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
