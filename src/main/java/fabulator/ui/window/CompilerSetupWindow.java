package fabulator.ui.window;

import fabulator.language.Text;
import fabulator.ui.view.CompilerSetupView;

import java.util.function.Consumer;

import static javafx.stage.Modality.APPLICATION_MODAL;

public class CompilerSetupWindow extends FABulatorWindow {

    private static final int INIT_WIDTH = 480;
    private static final int INIT_HEIGHT = 240;
    private static final Text TITLE = Text.EDIT_COMPILER_SETUP;

    private static CompilerSetupWindow instance;

    private Consumer<CompilerSetupView.Data> readyConsumer;

    private CompilerSetupWindow() {
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
        CompilerSetupView view = new CompilerSetupView();
        view.setOnReady(this::apply);
        this.setRoot(view);
    }

    private void apply(CompilerSetupView.Data data) {
        this.readyConsumer.accept(data);
        this.close();
    }

    public void setOnReady(Consumer<CompilerSetupView.Data> readyConsumer) {
        this.readyConsumer = readyConsumer;
    }

    public static CompilerSetupWindow getInstance() {
        if (instance == null) {
            new CompilerSetupWindow();
        }
        return instance;
    }
}
