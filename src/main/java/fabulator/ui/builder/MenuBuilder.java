package fabulator.ui.builder;

import fabulator.language.Text;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.util.Builder;

public class MenuBuilder implements Builder<Menu> {

    private final Menu menu;

    public MenuBuilder() {
        this.menu = new Menu();
    }

    public MenuBuilder setText(Text text) {
        this.menu.textProperty().bind(text.stringProperty());
        return this;
    }

    public MenuBuilder addItem(MenuItem item) {
        this.menu.getItems().add(item);
        return this;
    }

    @Override
    public Menu build() {
        return this.menu;
    }
}
