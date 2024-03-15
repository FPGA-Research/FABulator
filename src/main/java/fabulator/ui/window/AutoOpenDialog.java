package fabulator.ui.window;

import fabulator.language.Text;
import fabulator.settings.Config;
import fabulator.ui.builder.ButtonBuilder;
import fabulator.ui.builder.CheckBoxBuilder;
import fabulator.ui.builder.LabelBuilder;
import fabulator.ui.builder.StageBuilder;
import fabulator.ui.icon.ImageIcon;
import fabulator.util.FileUtils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.nio.file.Path;

public class AutoOpenDialog {

    private static AutoOpenDialog instance;

    private Config config;

    private Path path;

    private Stage stage;
    private CheckBox dontAskAgain;

    private AutoOpenDialog() {
        instance = this;
        this.config = Config.getInstance();

        this.stage = new StageBuilder()
                .setTitle(Text.OPEN_FILE)
                .setResizable(false)
                .setIcon(ImageIcon.FABULOUS)
                .build();

        VBox root = new VBox();
        Scene scene = new Scene(root, 400, 120);

        root.prefWidthProperty().bind(scene.widthProperty());
        root.prefHeightProperty().bind(scene.heightProperty());

        this.stage.setScene(scene);

        String lastFileName = Config.getInstance().getOpenedFabricFileName().get();

        Label information = new Label(lastFileName);

        Label openQuestion = new LabelBuilder()
                .setText(Text.OPEN_QUESTION)
                .build();

        this.dontAskAgain = new CheckBoxBuilder()
                .setText(Text.DONT_ASK_AGAIN)
                .build();

        VBox spacer = new VBox();
        spacer.setPadding(new Insets(8));

        EventHandler<ActionEvent> yesHandler = event -> {
            this.checkDontShowAgain(true);
            this.stage.close();
            Platform.runLater(() -> FileUtils.openFabricAsync(this.path.toFile()));
        };

        Button yesButton = new ButtonBuilder()
                .setText(Text.OPEN)
                .setOnAction(yesHandler)
                .build();

        EventHandler<ActionEvent> noHandler = event -> {
            this.checkDontShowAgain(false);
            this.stage.close();
        };

        Button noButton = new ButtonBuilder()
                .setText(Text.CANCEL)
                .setOnAction(noHandler)
                .build();

        HBox answerButtons = new HBox(yesButton, noButton);
        answerButtons.setAlignment(Pos.CENTER);
        answerButtons.setSpacing(8);

        root.getChildren().addAll(
                information,
                openQuestion,
                spacer,
                this.dontAskAgain,
                answerButtons
        );
        root.setAlignment(Pos.CENTER);
        root.setSpacing(4);
    }

    private void checkDontShowAgain(boolean autoOpen) {
        boolean dontAskAgain = this.dontAskAgain.isSelected();

        if (dontAskAgain) {
            this.config.getSuggestAutoOpen().set(false);
            this.config.getAutoOpen().set(autoOpen);
        }
    }

    public void suggest(Path path) {
        this.path = path;
        this.stage.show();
    }

    public static AutoOpenDialog getInstance() {
        if (instance == null) {
            new AutoOpenDialog();
        }
        return instance;
    }
}
