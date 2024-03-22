package fabulator.ui.view;

import fabulator.language.Text;
import fabulator.ui.builder.ButtonBuilder;
import fabulator.ui.builder.LabelBuilder;
import fabulator.ui.builder.RectangleBuilder;
import fabulator.ui.builder.TextFieldBuilder;
import fabulator.ui.style.StyleClass;
import fabulator.util.LayoutUtils;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import lombok.Getter;
import lombok.Setter;

import java.util.function.Consumer;

public class CompilerSetupView extends VBox implements View {

    @Getter
    @Setter
    public static class Data {
        private boolean apply;
        private String topModule;

        public Data() {
            this.apply = false;
        }
    }

    private Label titleLabel;
    private Rectangle titleSpacer;
    private Label topModuleLabel;
    private TextField topModuleField;
    private HBox topModuleSection;
    private Node includeSelection;
    private Button applyButton;
    private Button cancelButton;
    private HBox bottomButtons;
    private Consumer<Data> readyConsumer;

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

        this.titleSpacer = new RectangleBuilder()
                .setDims(24, 4)
                .setFill(Color.WHITE)
                .build();

        this.topModuleLabel = new LabelBuilder()
                .setText(Text.TOP_MODULE_NAME)
                .build();

        this.topModuleField = new TextFieldBuilder()
                .setText("top_wrapper")                 // TODO
                .build();

        this.includeSelection = this.buildIncludeSelection();

        this.topModuleSection = new HBox(
                this.topModuleLabel,
                LayoutUtils.hSpacer(),
                this.topModuleField
        );

        this.applyButton = new ButtonBuilder()
                .setId("yesButton")
                .setText(Text.APPLY)
                .setTooltip(Text.APPLY)
                .setOnAction(this::apply)
                .build();

        this.cancelButton = new ButtonBuilder()
                .setId("noButton")
                .setText(Text.CANCEL)
                .setTooltip(Text.CANCEL)
                .setOnAction(this::cancel)
                .build();

        this.bottomButtons = new HBox(
                LayoutUtils.hSpacer(),
                this.applyButton,
                this.cancelButton
        );
        this.bottomButtons.setSpacing(4);
    }

    private Node buildIncludeSelection() {
        VBox wrapper = new VBox();
        ToggleGroup group = new ToggleGroup();

        Label includeLabel = new LabelBuilder()
                .setText("Include Files")
                .build();

        RadioButton allInFolder = new RadioButton("All in Folder");
        allInFolder.setSelected(true);
        allInFolder.setToggleGroup(group);

        RadioButton userDefined = new RadioButton("User defined");
        userDefined.setSelected(false);
        userDefined.setToggleGroup(group);

        HBox includeBox = new HBox(
                includeLabel,
                LayoutUtils.hSpacer(),
                allInFolder,
                userDefined
        );
        includeBox.setSpacing(4);
        return includeBox;
    }

    private void apply(ActionEvent event) {
        Data data = new Data();
        data.setApply(true);
        data.setTopModule(this.topModuleField.getText());

        this.readyConsumer.accept(data);
    }

    private void cancel(ActionEvent event) {
        Data data = new Data();

        this.readyConsumer.accept(data);
    }

    public void setOnReady(Consumer<Data> readyConsumer) {
        this.readyConsumer = readyConsumer;
    }


    @Override
    public void buildWhole() {
        this.getChildren().addAll(
                this.titleLabel,
                this.titleSpacer,
                this.topModuleSection,
                this.includeSelection,
                LayoutUtils.vSpacer(),
                this.bottomButtons
        );
    }
}
