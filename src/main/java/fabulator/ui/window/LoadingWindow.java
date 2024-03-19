package fabulator.ui.window;

import fabulator.language.Text;
import fabulator.ui.builder.LabelBuilder;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import javafx.stage.StageStyle;

import static javafx.stage.Modality.APPLICATION_MODAL;

public class LoadingWindow extends FABulatorWindow {

    private static final int INIT_WIDTH = 320;
    private static final int INIT_HEIGHT = 160;
    private static final Text TITLE = Text.LOADING_FABRIC;

    private static LoadingWindow instance;

    private LoadingWindow() {
        super(
                INIT_WIDTH,
                INIT_HEIGHT,
                TITLE,
                APPLICATION_MODAL
        );
        instance = this;

        this.setResizable(false);
        this.initStyle(StageStyle.TRANSPARENT);
        this.buildSelf();
    }

    private void buildSelf() {
        ProgressIndicator spinner = new ProgressIndicator();
        spinner.setProgress(-1);

        Label loadingLabel = new LabelBuilder()
                .setText(Text.LOADING_FABRIC)
                .build();

        VBox root = new VBox(
                loadingLabel,
                spinner
        );
        root.setAlignment(Pos.CENTER);
        root.setSpacing(20);
        root.getStyleClass().add("loading-window");   // TODO: Add to StyleClass

        this.setRoot(root);
    }

    public static LoadingWindow getInstance() {
        if (instance == null) {
            new LoadingWindow();
        }
        return instance;
    }
}
