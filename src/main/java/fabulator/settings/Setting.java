package fabulator.settings;

import fabulator.language.Text;
import fabulator.ui.builder.LabelBuilder;
import fabulator.util.LayoutUtils;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public class Setting extends HBox {

    private Text name;
    private Node adjustor;

    public Setting(Text name, Node adjustor) {
        this.name = name;
        this.adjustor = adjustor;

        this.build();
    }

    private void build() {
        this.setSpacing(4);

        Label nameLabel = new LabelBuilder()
                .setFontSize(14)
                .setText(this.name)
                .setTextColor(Color.WHITE)
                .build();

        this.getChildren().addAll(
                nameLabel,
                LayoutUtils.hSpacer(),
                this.adjustor
        );
    }
}
