package fabulator.ui.window;

import fabulator.language.Text;
import fabulator.ui.view.CompilerSetupView;

import static javafx.stage.Modality.APPLICATION_MODAL;

public class EditSetupWindow extends FABulatorWindow {

    private static final int INIT_WIDTH = 480;
    private static final int INIT_HEIGHT = 320;
    private static final Text TITLE = Text.EDIT_COMPILER_SETUP;

    private static EditSetupWindow instance;

    private EditSetupWindow() {
        super(
                INIT_WIDTH,
                INIT_HEIGHT,
                TITLE,
                APPLICATION_MODAL
        );
        instance = this;

        this.buildSelf();
    }

    private void buildSelf() {
        this.setRoot(
                new CompilerSetupView()
        );
    }

    public static EditSetupWindow getInstance() {
        if (instance == null) {
            new EditSetupWindow();
        }
        return instance;
    }
}
