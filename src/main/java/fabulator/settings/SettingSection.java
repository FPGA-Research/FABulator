package fabulator.settings;

import fabulator.FABulator;
import fabulator.ui.builder.LabelBuilder;
import fabulator.ui.builder.RectangleBuilder;
import fabulator.language.Text;
import fabulator.ui.style.StyleClass;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class SettingSection extends VBox {

    private Text title;

    public SettingSection(Text title) {
        this.getStyleClass().add(StyleClass.SETTING_SECTION.getName());

        this.title = title;
        this.initialize();
    }

    private void initialize() {
        this.maxWidthProperty().bind(
                FABulator.getApplication()
                        .getStage()
                        .widthProperty()
                        .divide(2)
        );

        Rectangle topSpacer = new RectangleBuilder()
                .setHeight(1)
                .setFill(Color.WHITE)
                .build();

        topSpacer.widthProperty().bind(
                FABulator.getApplication()
                        .getStage()
                        .widthProperty()
                        .divide(2)
        );

        this.getChildren().add(topSpacer);

        Label sectionLabel = new LabelBuilder()
                .setText(this.title)
                .setFontSize(16)
                .setTextColor(Color.WHITE)
                .build();

        this.getChildren().add(sectionLabel);

        Rectangle titleSpacer = new RectangleBuilder()
                .setDims(24, 3)
                .setFill(Color.WHITE)
                .build();

        this.getChildren().add(titleSpacer);
    }

    public void addSetting(HBox setting) {
        this.getChildren().add(setting);
    }

    public void addSettings(HBox... settings) {
        this.getChildren().addAll(settings);
    }
}
