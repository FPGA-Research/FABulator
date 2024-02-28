package fabulator.ui.window;

import fabulator.language.Text;
import fabulator.ui.builder.ButtonBuilder;
import fabulator.ui.builder.LabelBuilder;
import fabulator.ui.builder.StageBuilder;
import fabulator.ui.icon.ImageIcon;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ErrorMessageDialog {

    private Stage stage;
    private VBox root;

    private Text errorText;

    public ErrorMessageDialog(Text errorText) {
        this.errorText = errorText;

        this.stage = new StageBuilder()
                .setTitle(Text.ERROR)
                .setResizable(false)
                .initModality(Modality.APPLICATION_MODAL)
                .setIcon(ImageIcon.FABULOUS)
                .build();

        this.root = new VBox();
        Scene scene = new Scene(this.root, 320, 120);

        this.root.prefWidthProperty().bind(scene.widthProperty());
        this.root.prefHeightProperty().bind(scene.heightProperty());

        this.stage.setScene(scene);
        this.build();
    }

    private void build() {
        Label messageLabel = new LabelBuilder()
                .setText(this.errorText)
                .build();

        EventHandler<ActionEvent> closeHandler = event -> {
            this.stage.close();
        };

        Button closeButton = new ButtonBuilder()
                .setText(Text.CLOSE)
                .setOnAction(closeHandler)
                .build();

        HBox answerButtons = new HBox(closeButton);
        answerButtons.setAlignment(Pos.CENTER);

        this.root.getChildren().addAll(
                messageLabel,
                closeButton
        );
        this.root.setAlignment(Pos.CENTER);
        this.root.setSpacing(16);
        this.stage.show();
    }
}
