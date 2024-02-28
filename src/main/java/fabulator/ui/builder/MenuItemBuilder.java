package fabulator.ui.builder;

import fabulator.language.Text;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.MenuItem;
import javafx.util.Builder;

public class MenuItemBuilder implements Builder<MenuItem> {

    private final MenuItem item;

    public MenuItemBuilder() {
        this.item = new MenuItem();
    }

    public MenuItemBuilder setText(Text text) {
        this.item.textProperty().bind(text.stringProperty());
        return this;
    }

    public MenuItemBuilder setGraphic(Node node) {
        this.item.setGraphic(node);
        return this;
    }

    public MenuItemBuilder setOnAction(EventHandler<ActionEvent> actionEvent) {
        this.item.setOnAction(actionEvent);
        return this;
    }

    @Override
    public MenuItem build() {
        return this.item;
    }
}
