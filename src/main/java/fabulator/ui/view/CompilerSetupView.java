package fabulator.ui.view;

import fabulator.language.Text;
import fabulator.ui.builder.LabelBuilder;
import fabulator.ui.builder.TextFieldBuilder;
import fabulator.ui.style.StyleClass;
import fabulator.util.LayoutUtils;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class CompilerSetupView extends VBox implements View {

    private Label titleLabel;
    private Label topModuleLabel;
    private TextField topModuleField;
    private HBox topModuleSection;

    public CompilerSetupView() {
        this.getStyleClass().add(StyleClass.COMPILER_SETUP_VIEW.getName());
        this.init();
    }

    // TODO: Reuse settings components once
    //  settings are refactored
    @Override
    public void buildParts() {
        this.titleLabel = new LabelBuilder()
                .setStyleClass(StyleClass.TITLE)
                .setText(Text.COMPILER_SETUP)
                .build();

        this.topModuleLabel = new LabelBuilder()
                .setText(Text.TOP_MODULE_NAME)
                .build();

        this.topModuleField = new TextFieldBuilder()
                .setText("top_wrapper")                 // TODO
                .build();

        this.topModuleSection = new HBox(
                this.topModuleLabel,
                LayoutUtils.hSpacer(),
                this.topModuleField
        );
    }

    @Override
    public void buildWhole() {
        this.getChildren().addAll(
                this.titleLabel,
                this.topModuleSection
        );
    }
}
