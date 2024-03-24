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

/**
 * A View for exploring a directory and its files and
 * subdirectories.
 */
public class ExplorerView extends VBox implements View {

    private ExplorerMenu topMenu;
    private VBox explorerAndSearchPane;
    private ListView<FileInfoView> searchResult;
    private TreeView<FileInfoView> fileStructure;

    private Consumer<FileInfoView> fileClickedHandler = event -> {};
    private Consumer<FileInfoView> itemClickedHandler = event -> {};

    /**
     * Constructs an {@link ExplorerView} object.
     */
    public ExplorerView() {
        this.init();
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void buildWhole() {
        this.setVisibleView(this.fileStructure);

        this.getChildren().addAll(
                this.topMenu,
                this.explorerAndSearchPane
        );
    }

    /**
     * Sets a {@link Consumer} that can be used to handle
     * clicks on a {@link FileInfoView} object, which
     * represents a file in the displayed directory.
     *
     * @param eventHandler the {@link Consumer} handling the
     *                     click
     */
    public void setOnFileClicked(Consumer<FileInfoView> eventHandler) {
        this.fileClickedHandler = eventHandler;
    }

    /**
     * Opens a directory in the {@link ExplorerView}.
     *
     * @param file the given directory
     * @throws IOException If reading the contents of the
     * directory throws an {@link IOException}
     */
    public void open(File file) throws IOException {
        TreeItem<FileInfoView> root = new TreeItem<>();
        root.setValue(
                new FileInfoView(file, root)
        );

        this.fileStructure.setRoot(root);
        this.openRecursively(file, root);
    }

    /**
     * Opens a file or directory recursively in the
     * {@link ExplorerView}, i.e. if the given {@link File}
     * object is a directory, then this method will be
     * called for all files and directories in that
     * directory. Used by the
     * {@link #open(File) open} method of
     * {@link ExplorerView}.
     *
     * @param file the {@link} File object to open
     *             recursively
     * @param root the corresponding {@link TreeItem}
     * @throws IOException If reading the contents throws an
     * {@link IOException}
     */
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

    /**
     * Fully expands the directory structure in the
     * {@link ExplorerView}.
     *
     * @param event the {@link ActionEvent} causing the
     *              expansion
     */
    public void expandAll(ActionEvent event) {
        this.expandRecursively(this.fileStructure.getRoot());
    }

    /**
     * Expands the directory structure in the
     * {@link ExplorerView} recursively. Used by the
     * {@link #expandAll(ActionEvent) expandAll} method of
     * {@link ExplorerView}.
     *
     * @param item the {@link TreeItem} of the
     *             {@link ExplorerView} to expand
     *             recursively
     */
    private void expandRecursively(TreeItem<FileInfoView> item) {
        if (item != null) {
            item.setExpanded(true);

            for (TreeItem<FileInfoView> child : item.getChildren()) {
                expandRecursively(child);
            }
        }
    }

    /**
     * Fully collapses the directory structure in the
     * {@link ExplorerView}.
     *
     * @param event the {@link ActionEvent} causing the
     *              collapse
     */
    public void collapseAll(ActionEvent event) {
        this.collapseRecursively(this.fileStructure.getRoot());
    }

    /**
     * Collapses the directory structure in the
     * {@link ExplorerView} recursively. Used by the
     * {@link #collapseAll(ActionEvent) collapseAll} method
     * of {@link ExplorerView}.
     *
     * @param item the {@link TreeItem} of the
     *             {@link ExplorerView} to collapse
     *             recursively
     */
    private void collapseRecursively(TreeItem<FileInfoView> item) {
        if (item != null) {
            item.setExpanded(false);

            for (TreeItem<FileInfoView> child : item.getChildren()) {
                collapseRecursively(child);
            }
        }
    }

    /**
     * Searches for a name in the directory and filters the
     * displayed items.
     *
     * @param filterString the name to search and filter for
     */
    public void search(String filterString) {
        this.setSearchEnabled(!filterString.isEmpty());
        this.searchResult.getItems().clear();
        this.filterRecursively(filterString, this.fileStructure.getRoot());
    }

    /**
     * Enables or disables the search functionality. If
     * disabled, a normal tree-like structure is shown to
     * display the opened directory. If enabled, a list of
     * all items in the directory that match the current
     * search are shown.
     *
     * @param enabled whether to enable the search
     */
    private void setSearchEnabled(boolean enabled) {
        if (enabled) {
            this.setVisibleView(this.searchResult);
        } else {
            this.setVisibleView(this.fileStructure);
        }
    }

    /**
     * Sets which of two views of the opened directory
     * should be shown: The regular tree-like view; or the
     * list-like view for searching. Used by the
     * {@link #setSearchEnabled(boolean) setSearchEnabled}
     * method of {@link ExplorerView}.
     *
     * @param view the view to make visible
     */
    private void setVisibleView(Parent view) {
        this.explorerAndSearchPane.getChildren().clear();
        this.explorerAndSearchPane.getChildren().add(view);
    }

    /**
     * Searches for a name in the directory and filters the
     * displayed items recursively. Used by the
     * {@link #search(String) search} method of
     * {@link ExplorerView}.
     *
     * @param filterString the name to search and filter for
     * @param item the item representing a file or directory
     *             to search and filter recursively
     */
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

/**
 * A view accompanying {@link ExplorerView} which offers
 * a search bar and buttons for options related to the
 * {@link ExplorerView}.
 */
class ExplorerMenu extends HBox implements View {

    private Node graphic;
    private TextField searchField;
    private Button expandAllButton;
    private Button collapseAllButton;

    private Consumer<String> searchHandler;
    private Consumer<ActionEvent> expandAllHandler;
    private Consumer<ActionEvent> collapseAllHandler;

    /**
     * Constructs an {@link ExplorerMenu} object.
     */
    public ExplorerMenu() {
        this.getStyleClass().add(StyleClass.EXPLORER_MENU.getName());
        this.init();
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
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

    /**
     * Searches for a given text in the affiliated
     * {@link ExplorerView}.
     *
     * @param text the text to search for
     */
    private void search(String text) {
        this.searchHandler.accept(text);
    }

    /**
     * Triggers the expansion of the affiliated
     * {@link ExplorerView}.
     *
     * @param event the {@link ActionEvent} causing the
     *              expansion
     */
    private void expandAll(ActionEvent event) {
        this.expandAllHandler.accept(event);
    }

    /**
     * Triggers the collapse of the affiliated
     * {@link ExplorerView}.
     *
     * @param event the {@link ActionEvent} causing the
     *              collapse
     */
    private void collapseAll(ActionEvent event) {
        this.collapseAllHandler.accept(event);
    }

    /**
     * Sets a {@link Consumer} to call whenever the text
     * of the search bar changes.
     *
     * @param eventHandler the {@link Consumer} handling the
     *                     changes
     */
    public void setOnSearch(Consumer<String> eventHandler) {
        this.searchHandler = eventHandler;
    }

    /**
     * Sets a {@link Consumer} to call whenever expansion of
     * the affiliated {@link ExplorerView} is demanded.
     *
     * @param eventHandler the {@link Consumer} handling the
     *                     expansion demand.
     */
    public void setOnExpandAll(Consumer<ActionEvent> eventHandler) {
        this.expandAllHandler = eventHandler;
    }

    /**
     * Sets a {@link Consumer} to call whenever collapse of
     * the affiliated {@link ExplorerView} is demanded.
     *
     * @param eventHandler the {@link Consumer} handling the
     *                     collapse demand.
     */
    public void setOnCollapseAll(Consumer<ActionEvent> eventHandler) {
        this.collapseAllHandler = eventHandler;
    }
}
