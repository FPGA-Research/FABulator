package fabulator.ui.builder;

import fabulator.language.Text;
import fabulator.ui.icon.CssIcon;
import fabulator.ui.icon.ImageIcon;
import fabulator.ui.style.UiColor;
import javafx.beans.value.ObservableBooleanValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.util.Builder;
import org.kordamp.ikonli.javafx.FontIcon;

public class ButtonBuilder implements Builder<Button> {

    private final Button button;

    public ButtonBuilder() {
        this.button = new Button();
    }

    public ButtonBuilder setIcon(CssIcon icon) {
        this.button.setGraphic(new FontIcon());
        this.button.setId(icon.getId());
        return this;
    }

    public ButtonBuilder setIcon(ImageIcon icon) {
        this.button.setGraphic(new ImageView(icon.getImage()));
        return this;
    }

    public ButtonBuilder setColor(UiColor color) {
        this.button.getStyleClass().add(color.getColorClass());
        return this;
    }

    public ButtonBuilder setId(String id) {
        this.button.setId(id);
        return this;
    }

    public ButtonBuilder setTooltip(Text text) {
        Tooltip tooltip = new Tooltip();
        tooltip.textProperty().bind(text.stringProperty());
        this.button.setTooltip(tooltip);
        return this;
    }

    public ButtonBuilder setText(Text text) {
        this.button.textProperty().bind(text.stringProperty());
        return this;
    }

    public ButtonBuilder setString(String string) {
        this.button.setText(string);
        return this;
    }

    public ButtonBuilder setOnAction(EventHandler<ActionEvent> eventHandler) {
        this.button.setOnAction(eventHandler);
        return this;
    }

    public ButtonBuilder bindDisable(ObservableBooleanValue binding) {
        this.button.disableProperty().bind(binding);
        return this;
    }

    @Override
    public Button build() {
        return this.button;
    }
}
