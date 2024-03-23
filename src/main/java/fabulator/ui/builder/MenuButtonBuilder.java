package fabulator.ui.builder;

import fabulator.language.Text;
import fabulator.ui.icon.CssIcon;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.util.Builder;
import org.kordamp.ikonli.javafx.FontIcon;

public class MenuButtonBuilder implements Builder<MenuButton> {

    private MenuButton menuButton;

    public MenuButtonBuilder() {
        this.menuButton = new MenuButton();
    }

    public MenuButtonBuilder setText(Text text) {
        this.menuButton.textProperty().bind(text.stringProperty());
        return this;
    }

    public MenuButtonBuilder setIcon(CssIcon icon) {
        this.menuButton.setGraphic(new FontIcon());
        this.menuButton.setId(icon.getId());
        return this;
    }

    public MenuButtonBuilder addItem(MenuItem item) {
        this.menuButton.getItems().add(item);
        return this;
    }

    @Override
    public MenuButton build() {
        return this.menuButton;
    }
}
