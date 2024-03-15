package fabulator.ui.view;

import fabulator.language.Text;
import fabulator.ui.builder.LabelBuilder;
import fabulator.ui.style.StyleClass;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * A simple View showing a hint text.
 * Can be used as a place-holder for
 * uninitialized or empty Views.
 */
public class EmptyHintView extends StackPane implements View {

    private Text[] hints;
    private VBox hintBox;

    public EmptyHintView(Text... hints) {
        this.getStyleClass().add(StyleClass.EMPTY_HINT_VIEW.getName());
        this.hints = hints;
        this.init();
    }

    @Override
    public void buildParts() {
        this.hintBox = new VBox();
        this.hintBox.setAlignment(Pos.CENTER);

        for (Text hint : this.hints) {
            Label hintLabel = new LabelBuilder()
                    .setText(hint)
                    .build();

            this.hintBox.getChildren().add(hintLabel);
        }
    }

    @Override
    public void buildWhole() {
        this.getChildren().addAll(this.hintBox);
        StackPane.setAlignment(this.hintBox, Pos.CENTER);
    }
}
