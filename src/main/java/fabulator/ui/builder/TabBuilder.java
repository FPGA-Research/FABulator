package fabulator.ui.builder;

import fabulator.language.Text;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.util.Builder;

public class TabBuilder implements Builder<Tab> {

    private Tab tab;

    public TabBuilder() {
        this.tab = new Tab();
    }

    public TabBuilder setText(Text text) {
        this.tab.textProperty().bind(text.stringProperty());
        return this;
    }

    public TabBuilder setContent(Node content) {
        this.tab.setContent(content);
        return this;
    }

    @Override
    public Tab build() {
        return this.tab;
    }
}
