package fabulator.ui.window;

import fabulator.ui.builder.StageBuilder;
import fabulator.ui.icon.ImageIcon;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class EditSetupWindow {

    private static EditSetupWindow instance;

    private Stage stage;

    private EditSetupWindow() {
        instance = this;

        this.buildSelf();
    }

    private void buildSelf() {
        this.stage = new StageBuilder()
                .initModality(Modality.APPLICATION_MODAL)
                .setIcon(ImageIcon.FABULOUS)
                .build();

        this.stage.setTitle("Edit Compiler Setup");
        this.stage.setWidth(480);
        this.stage.setHeight(320);

        StackPane root = new StackPane(
                new Label("Work In Progress")
        );
        Scene scene = new Scene(root, 480, 320);
        this.stage.setScene(scene);
    }

    public void show() {
        this.stage.show();
    }

    public static EditSetupWindow getInstance() {
        if (instance == null) {
            new EditSetupWindow();
        }
        return instance;
    }
}
