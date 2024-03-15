package fabulator.ui.view;

import javafx.beans.property.BooleanProperty;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;

import java.io.File;
import java.io.IOException;

public class ScrollableCodeView extends VBox implements View {

    private VirtualizedScrollPane<CodeArea> scrollPane;
    private CodeView codeView;

    public ScrollableCodeView() {
        this.init();
    }

    @Override
    public void buildParts() {
        this.codeView = new CodeView();
        this.scrollPane = new VirtualizedScrollPane<>(this.codeView);
        this.scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        VBox.setVgrow(this.scrollPane, Priority.ALWAYS);
    }

    @Override
    public void buildWhole() {
        this.getChildren().add(this.scrollPane);
    }

    public void open(File file) throws IOException {
        this.codeView.open(file);
    }

    public BooleanProperty getComputeHighlightingProperty() {
        return this.codeView.getComputeHighlightingProperty();
    }
}
