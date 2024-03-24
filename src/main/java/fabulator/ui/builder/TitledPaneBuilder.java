package fabulator.ui.builder;

import fabulator.language.Text;
import fabulator.util.LayoutUtils;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.util.Builder;

public class TitledPaneBuilder implements Builder<TitledPane> {

    private final TitledPane titledPane;

    public TitledPaneBuilder() {
        this.titledPane = new TitledPane();
    }

    public TitledPaneBuilder setTitle(Text title) {
        this.titledPane.textProperty().bind(title.stringProperty());
        return this;
    }

    public TitledPaneBuilder setValue(String value) {
        HBox valueBox = new HBox(
                LayoutUtils.hSpacer(),
                new LabelBuilder()
                        .setText(value)
                        .build()
        );
        this.titledPane.setGraphic(valueBox);
        this.titledPane.setContentDisplay(ContentDisplay.RIGHT);
        return this;
    }

    public TitledPaneBuilder setKeyValue(Text key, String value) {
        this.titledPane.setAlignment(Pos.CENTER);

        HBox contentPane = new HBox(
                new LabelBuilder()
                        .setText(key)
                        .build(),
                LayoutUtils.hSpacer(),
                new LabelBuilder()
                        .setText(value)
                        .build()
        );
        contentPane.setAlignment(Pos.CENTER);
        contentPane.getStyleClass().add("key-value-title");
        contentPane.minWidthProperty().bind(this.titledPane.widthProperty());

        this.titledPane.setGraphic(contentPane);
        return this;
    }

    public TitledPaneBuilder setContent(Node content) {
        this.titledPane.setContent(content);
        return this;
    }

    @Override
    public TitledPane build() {
        return this.titledPane;
    }
}
