package fabulator.ui.menu;

import javafx.beans.value.ChangeListener;
import javafx.scene.control.ChoiceBox;

import java.util.ArrayList;
import java.util.List;

public class StatefulChoiceBox<T extends Stateful> extends ChoiceBox<T> {

    private ChangeListener<String> stateChangedListener;
    private ChangeListener<T> valueListener;

    public StatefulChoiceBox() {
        this.stateChangedListener = (obs, old, now) -> {
            this.update();
        };
    }

    public void fillWith(List<T> items) {
        this.getItems().clear();
        this.getItems().addAll(items);
        this.listenForStateChanges();
    }

    public void setOnValueChanged(ChangeListener<T> listener) {
        this.valueListener = listener;
        this.valueProperty().addListener(listener);
    }

    private void listenForStateChanges() {
        this.getItems().forEach(
                item -> item.getState().addListener(this.stateChangedListener)
        );
    }

    private void update() {
        List<T> items = new ArrayList<>(this.getItems());
        int selectedIndex = this.getSelectionModel().getSelectedIndex();

        this.disableValueListener();

        this.getItems().clear();
        this.getItems().addAll(items);
        this.getSelectionModel().select(selectedIndex);

        this.enableValueListener();
    }

    private void disableValueListener() {
        this.valueProperty().removeListener(this.valueListener);
    }

    private void enableValueListener() {
        this.valueProperty().addListener(this.valueListener);
    }
}
