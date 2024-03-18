package fabulator.ui.window;

import fabulator.language.Text;
import fabulator.ui.builder.LabelBuilder;
import fabulator.ui.builder.StageBuilder;
import fabulator.ui.icon.ImageIcon;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

@Deprecated
public class OldLoadingWindow {

    private static OldLoadingWindow instance;

    private Stage stage;
    private VBox root;

    private OldLoadingWindow() {
        instance = this;

        this.stage = new StageBuilder()
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
        Label loadingLabel = new LabelBuilder()
                .setText(Text.LOADING_FABRIC)
                .build();

        VBox spacer = new VBox();
        spacer.setPadding(new Insets(8));

        ProgressIndicator spinner = new ProgressIndicator();
        spinner.setProgress(-1);

        this.root.getChildren().addAll(
                loadingLabel,
                spacer,
                spinner
        );
        this.root.setAlignment(Pos.CENTER);
        this.root.setSpacing(4);
    }

    public void show() {
        this.stage.show();
    }

    public void hide() {
        this.stage.hide();
    }

    public static OldLoadingWindow getInstance() {
        if (instance == null) {
            new OldLoadingWindow();
        }
        return instance;
    }
}
