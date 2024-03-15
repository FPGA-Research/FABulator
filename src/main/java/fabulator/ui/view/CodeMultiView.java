package fabulator.ui.view;

import fabulator.language.Text;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.HashMap;
import java.util.function.Consumer;

public class CodeMultiView extends StackPane implements View {

    private EmptyHintView emptyHintView;
    private TabPane codeTabs;

    private HashMap<FileInfoView, Tab> tabMap;
    private HashMap<Tab, FileInfoView> infoMap;

    public CodeMultiView() {
        this.init();
        this.listenForSelection();
    }

    @Override
    public void buildParts() {
        this.emptyHintView = new EmptyHintView(
                Text.EXPLORER_HINT_1,
                Text.EXPLORER_HINT_2
        );
        this.codeTabs = new TabPane();
        this.tabMap = new HashMap<>();
        this.infoMap = new HashMap<>();
    }

    @Override
    public void buildWhole() {
        this.getChildren().addAll(
                this.emptyHintView,
                this.codeTabs
        );
    }

    private void listenForSelection() {
        ChangeListener<Tab> listener = (obs, old, now) -> {
            // TODO: enable/disable syntax highlighting computation to save cpu resources
        };

        this.codeTabs.getSelectionModel()
                .selectedItemProperty()
                .addListener(listener);
    }

    public void openFileOf(FileInfoView fileInfo) throws IOException {
        if (this.tabMap.containsKey(fileInfo)) {
            this.openTabOf(fileInfo);
        } else {
            this.createNewTabFor(fileInfo);
        }
    }

    private void openTabOf(FileInfoView fileInfo) {
        assert this.tabMap.containsKey(fileInfo);

        this.codeTabs.getSelectionModel().select(
                this.tabMap.get(fileInfo)
        );
    }

    private void createNewTabFor(FileInfoView fileInfo) throws IOException {
        ScrollableCodeView codeView = new ScrollableCodeView();
        codeView.open(fileInfo.getFile());

        Tab tab = new Tab(fileInfo.getFile().getName(), codeView);
        tab.setOnClosed(event -> {
            this.tabMap.remove(fileInfo);
            this.infoMap.remove(tab);
        });

        codeView.getComputeHighlightingProperty().bind(
                tab.selectedProperty()
        );

        this.tabMap.put(fileInfo, tab);
        this.infoMap.put(tab, fileInfo);

        this.codeTabs.getTabs().add(tab);
        this.codeTabs.getSelectionModel().select(tab);
    }

    public void setOnSelectionChanged(Consumer<FileInfoView> handler) {
        this.codeTabs.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, old, now) -> {
                    FileInfoView infoViewOfTab = this.infoMap.get(now);
                    handler.accept(infoViewOfTab);
                });
    }
}
