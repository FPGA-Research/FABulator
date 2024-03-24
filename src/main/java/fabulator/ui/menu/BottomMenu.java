package fabulator.ui.menu;

import fabulator.language.Text;
import fabulator.memory.ReferenceHolder;
import fabulator.ui.builder.ButtonBuilder;
import fabulator.ui.builder.StatefulChoiceBoxBuilder;
import fabulator.ui.fabric.element.ElementType;
import fabulator.ui.fabric.element.FabricElement;
import fabulator.ui.style.StyleClass;
import fabulator.util.LayoutUtils;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Pattern;

public class BottomMenu extends VBox implements ReferenceHolder {

    private FabricMenu fabricMenu;
    private List<FabricElement> found;
    private ListIterator<FabricElement> iterator;

    private HBox searchWrapper;
    private TextField searchField;
    private Button nextButton;
    private ChoiceBox<ElementType> typeBox;
    private Label resultText;
    private Label elementLabel;

    private boolean search = false;

    public BottomMenu(FabricMenu fabricMenu) {
        this.getStyleClass().add(StyleClass.BOTTOM_MENU.getName());
        this.fabricMenu = fabricMenu;

        this.initialize();
        this.setup();
    }

    private void initialize() {
        this.searchWrapper = new HBox();
        this.searchWrapper.setSpacing(4);

        this.searchField = new TextField();
        this.searchField.promptTextProperty().bind(Text.SEARCH.stringProperty());
        this.searchField.focusedProperty().addListener((obs, old, now) -> {
            this.search = now;
        });

        this.searchField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                if (this.search) {
                    this.search();
                    this.search = false;
                }
                this.goToNext();
            }
        });

        this.searchField.textProperty().addListener((obs, old, now) -> {
            this.search = true;
        });

        this.nextButton = new ButtonBuilder()
                .setOnAction(event -> this.goToNext())
                .setText(Text.NEXT)
                .build();

        ChangeListener<ElementType> valChangedListener = (obs, old, now) -> {
            this.search();
            this.goToNext();
        };

        this.typeBox = new StatefulChoiceBoxBuilder<ElementType>()
                .fillWith(List.of(ElementType.values()))
                .setValue(ElementType.ANY)
                .setOnValueChanged(valChangedListener)
                .build();

        this.resultText = new Label();
        this.resultText.getStyleClass().add(StyleClass.SEARCH_FEEDBACK.getName());

        this.elementLabel = new Label();
        this.elementLabel.getStyleClass().add(StyleClass.SEARCH_FEEDBACK.getName());
    }

    private void setup() {
        this.searchWrapper.getChildren().addAll(
                this.searchField,
                this.nextButton,
                this.typeBox,
                this.resultText,
                LayoutUtils.hSpacer(),
                this.elementLabel
        );
        this.getChildren().add(this.searchWrapper);
    }

    @Override
    public void dropReferences() {
        this.found = new LinkedList<>();
        this.iterator = null;
        this.resultText.setText("");
        this.elementLabel.setText("");
    }

    private void search() {
        String regex = this.searchField.getText();
        if (!regex.isEmpty()) {
            Pattern pattern = Pattern.compile(regex);
            ElementType type = this.typeBox.getValue();

            this.found = this.fabricMenu.searchView(type, pattern);
            this.iterator = this.found.listIterator();
        }
    }

    private void goToNext() {
        if (this.iterator != null && this.iterator.hasNext()) {
            int index = this.iterator.nextIndex() + 1;
            String feedback = index + " " + Text.OF.stringProperty().get() + " " + this.found.size();
            FabricElement element = this.iterator.next();
            this.resultText.setText(feedback);
            this.elementLabel.setText(
                    element.getType().toString() + ": " + element.getName()
            );
            this.fabricMenu.navigateTo(element);

        } else {
            this.resultText.setText(Text.NO_MORE_OCCURRENCES.stringProperty().get());
            this.elementLabel.setText("");
        }
    }
}
