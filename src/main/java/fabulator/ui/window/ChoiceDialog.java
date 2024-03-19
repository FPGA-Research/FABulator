package fabulator.ui.window;

import fabulator.language.Text;
import fabulator.ui.builder.ButtonBuilder;
import fabulator.ui.builder.LabelBuilder;
import fabulator.util.LayoutUtils;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.Setter;

public class ChoiceDialog extends FABulatorWindow {

    private Text[] dialogText;
    private Button yesButton;
    private Button noButton;

    @Setter
    private Runnable yesRunnable = () -> {};

    @Setter
    private Runnable noRunnable = () -> {};

    public ChoiceDialog(int width, int height, Text title, Text... dialogText) {
        super(width, height, title);

        this.dialogText = dialogText;

        this.initialize();
        this.setup();
    }

    private void initialize() {
        this.yesButton = new ButtonBuilder()
                .setId("yesButton")
                .setText(Text.YES)
                .setTooltip(Text.YES)
                .setOnAction(this::yesClicked)
                .build();

        this.noButton = new ButtonBuilder()
                .setId("noButton")
                .setText(Text.CANCEL)
                .setTooltip(Text.CANCEL)
                .setOnAction(this::noClicked)
                .build();
    }

    private void setup() {
        VBox root = new VBox();
        root.getStyleClass().add("choice-dialog");

        ObservableList<Node> rootChildren = root.getChildren();

        for (Text text : this.dialogText) {
            Label textLabel = new LabelBuilder()
                    .setText(text)
                    .setWrapText(true)
                    .build();
            rootChildren.add(textLabel);
        }

        rootChildren.addAll(
                LayoutUtils.vSpacer(),
                new HBox(
                        LayoutUtils.hSpacer(),
                        this.yesButton,
                        this.noButton
                )
        );

        this.setRoot(root);
    }

    private void yesClicked(ActionEvent event) {
        this.close();
        this.yesRunnable.run();
    }

    private void noClicked(ActionEvent event) {
        this.close();
        this.noRunnable.run();
    }
}
