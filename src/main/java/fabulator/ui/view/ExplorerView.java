package fabulator.ui.view;

import fabulator.language.Text;
import fabulator.logging.LogManager;
import fabulator.logging.Logger;
import fabulator.ui.builder.ButtonBuilder;
import fabulator.ui.builder.LabelBuilder;
import fabulator.ui.builder.TextFieldBuilder;
import fabulator.ui.icon.CssIcon;
import fabulator.ui.style.StyleClass;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class ExplorerView extends VBox implements View {

    private ExplorerMenu topMenu;
    private VBox explorerAndSearchPane;
    private ListView<FileInfoView> searchResult;
    private TreeView<FileInfoView> fileStructure;

    private Consumer<FileInfoView> fileClickedHandler = event -> {};
    private Consumer<FileInfoView> itemClickedHandler = event -> {};

    public ExplorerView() {
        this.init();
    }

    @Override
    public void buildParts() {
        this.getStyleClass().add(StyleClass.EXPLORER_VIEW.getName());

        this.topMenu = new ExplorerMenu();
        this.topMenu.setOnSearch(this::search);
        this.topMenu.setOnExpandAll(this::expandAll);
        this.topMenu.setOnCollapseAll(this::collapseAll);

        this.explorerAndSearchPane = new VBox();
        this.searchResult = new ListView<>();
        this.fileStructure = new TreeView<>();

        VBox.setVgrow(this.explorerAndSearchPane, Priority.ALWAYS);
        VBox.setVgrow(this.searchResult, Priority.ALWAYS);
        VBox.setVgrow(this.fileStructure, Priority.ALWAYS);
    }

    @Override
    public void buildWhole() {
        this.setVisibleView(this.fileStructure);

        this.getChildren().addAll(
                this.topMenu,
                this.explorerAndSearchPane
        );
    }

    public void setOnFileClicked(Consumer<FileInfoView> eventHandler) {
        this.fileClickedHandler = eventHandler;
    }

    public void setOnFilesChanged(Consumer<FileInfoView> eventHandler) {
        this.itemClickedHandler = eventHandler;
    }

    public void open(File file) throws IOException {
        TreeItem<FileInfoView> root = new TreeItem<>();
        root.setValue(
                new FileInfoView(file, root)
        );

        this.fileStructure.setRoot(root);
        this.openRecursively(file, root);
    }

    private void openRecursively(File file, TreeItem<FileInfoView> root) throws IOException {
        if (file.isDirectory()) {
            try (Stream<Path> pathStream = Files.list(file.toPath())) {
                pathStream.forEach(path -> {

                    TreeItem<FileInfoView> item = new TreeItem<>();
                    FileInfoView infoView = new FileInfoView(path.toFile(), item);
                    infoView.setOnClicked(fileInfo -> {
                        this.fileClickedHandler.accept(fileInfo);
                        this.itemClickedHandler.accept(fileInfo);
                    });
                    item.setValue(infoView);

                    root.getChildren().add(item);

                    try {
                        this.openRecursively(path.toFile(), item);
                    } catch (IOException e) {
                        Logger logger = LogManager.getLogger();
                        logger.error("Could not open file " + path);
                    }
                });
            }
        }
    }

    public void expandAll(ActionEvent event) {
        this.expandRecursively(this.fileStructure.getRoot());
    }

    private void expandRecursively(TreeItem<FileInfoView> item) {
        if (item != null) {
            item.setExpanded(true);

            for (TreeItem<FileInfoView> child : item.getChildren()) {
                expandRecursively(child);
            }
        }
    }

    public void collapseAll(ActionEvent event) {
        this.collapseRecursively(this.fileStructure.getRoot());
    }

    private void collapseRecursively(TreeItem<FileInfoView> item) {
        if (item != null) {
            item.setExpanded(false);

            for (TreeItem<FileInfoView> child : item.getChildren()) {
                collapseRecursively(child);
            }
        }
    }

    public void search(String filterString) {
        this.setSearchEnabled(!filterString.isEmpty());
        this.searchResult.getItems().clear();
        this.filterRecursively(filterString, this.fileStructure.getRoot());
    }

    private void setSearchEnabled(boolean enabled) {
        if (enabled) {
            this.setVisibleView(this.searchResult);
        } else {
            this.setVisibleView(this.fileStructure);
        }
    }

    private void setVisibleView(Parent view) {
        this.explorerAndSearchPane.getChildren().clear();
        this.explorerAndSearchPane.getChildren().add(view);
    }

    private void filterRecursively(String filterString, TreeItem<FileInfoView> item) {
        if (item != null) {
            FileInfoView infoView = item.getValue();
            if (infoView.matches(filterString)) {
                this.searchResult.getItems().add(infoView);
            }

            for (TreeItem<FileInfoView> child : item.getChildren()) {
                filterRecursively(filterString, child);
            }
        }
    }
}

class ExplorerMenu extends HBox implements View {

    private Node graphic;
    private TextField searchField;
    private Button expandAllButton;
    private Button collapseAllButton;

    private Consumer<String> searchHandler;
    private Consumer<ActionEvent> expandAllHandler;
    private Consumer<ActionEvent> collapseAllHandler;

    public ExplorerMenu() {
        this.getStyleClass().add(StyleClass.EXPLORER_MENU.getName());
        this.init();
    }

    @Override
    public void buildParts() {
        this.graphic = new LabelBuilder()
                .setIcon(CssIcon.SEARCH)
                .build();

        this.searchField = new TextFieldBuilder()
                .setPrompt(Text.SEARCH)
                .setOnChanged(this::search)
                .build();

        this.expandAllButton = new ButtonBuilder()
                .setIcon(CssIcon.EXPAND_ALL)
                .setTooltip(Text.EXPAND_ALL)
                .setOnAction(this::expandAll)
                .build();

        this.collapseAllButton = new ButtonBuilder()
                .setIcon(CssIcon.COLLAPSE_ALL)
                .setTooltip(Text.COLLAPSE_ALL)
                .setOnAction(this::collapseAll)
                .build();
    }

    @Override
    public void buildWhole() {
        Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        this.getChildren().addAll(
                this.graphic,
                this.searchField,
                spacer,
                this.expandAllButton,
                this.collapseAllButton
        );
    }

    private void search(String text) {
        this.searchHandler.accept(text);
    }

    private void expandAll(ActionEvent event) {
        this.expandAllHandler.accept(event);
    }

    private void collapseAll(ActionEvent event) {
        this.collapseAllHandler.accept(event);
    }

    public void setOnSearch(Consumer<String> eventHandler) {
        this.searchHandler = eventHandler;
    }

    public void setOnExpandAll(Consumer<ActionEvent> eventHandler) {
        this.expandAllHandler = eventHandler;
    }

    public void setOnCollapseAll(Consumer<ActionEvent> eventHandler) {
        this.collapseAllHandler = eventHandler;
    }
}
