package fabulator.ui.builder;

import fabulator.language.Text;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.util.Builder;

import java.util.function.Consumer;

public class TextFieldBuilder implements Builder<TextField> {

    private TextField textField;

    public TextFieldBuilder() {
        this.textField = new TextField();
    }

    public TextFieldBuilder setPrompt(Text text) {
        this.textField.promptTextProperty().bind(text.stringProperty());
        return this;
    }

    public TextFieldBuilder setOnAction(EventHandler<ActionEvent> eventHandler) {
        this.textField.setOnAction(eventHandler);
        return this;
    }

    public TextFieldBuilder setOnChanged(Consumer<String> handler) {
        this.textField.textProperty().addListener((obs, old, now) -> {
            handler.accept(now);
        });
        return this;
    }

    @Override
    public TextField build() {
        return this.textField;
    }
}
