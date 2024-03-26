package fabulator.ui.view;

import fabulator.ui.builder.LabelBuilder;
import fabulator.ui.icon.CssIcon;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.HBox;
import lombok.Getter;

import java.io.File;
import java.nio.file.Files;
import java.util.function.Consumer;

/**
 * A View for displaying information about a {@link File}
 * object like its name or whether it is a file or directory.
 */
public class FileInfoView extends HBox implements View {

    @Getter
    private TreeItem<FileInfoView> wrapper;

    @Getter
    private File file;
    private Label label;
    private Node graphic;

    /**
     * Constructs a {@link FileInfoView} object. Takes the
     * {@link File} object of which the information shall be
     * displayed and the {@link TreeItem} object in which
     * the constructed {@link FileInfoView} object shall be
     * wrapped. The wrapping is handled by this class.
     *
     * @param file the {@link File} object to display the
     *             information of
     * @param wrapper the {@link TreeItem} object in which
     *                to wrap the constructed object.
     */
    public FileInfoView(File file, TreeItem<FileInfoView> wrapper) {
        this.file = file;
        this.wrapper = wrapper;

        this.init();
        this.wrap();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void buildParts() {
        this.label = new LabelBuilder()
                .setText(this.file.getName())
                .build();

        CssIcon cssIcon = Files.isDirectory(this.file.toPath())
                ? CssIcon.DIRECTORY
                : CssIcon.FILE;

        this.graphic = new LabelBuilder()
                .setIcon(cssIcon)
                .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void buildWhole() {
        this.getChildren().addAll(
                this.graphic,
                this.label
        );
    }

    /**
     * Wraps this object in the {@link TreeItem} object
     * passed in the constructor.
     */
    private void wrap() {
        this.wrapper.setValue(this);
    }

    /**
     * Sets a {@link Consumer} to call whenever this object
     * is clicked and represents a regular file rather than
     * a directory.
     *
     * @param handler the {@link Consumer} handling the
     *                click
     */
    public void setOnFileClicked(Consumer<FileInfoView> handler) {
        this.setOnMouseClicked(event -> {
            if (Files.isRegularFile(this.file.toPath())) {
                handler.accept(this);
            }
        });
    }

    /**
     * Determines whether the name of the {@link File}
     * object represented by this object matches a given
     * filterString. This is done by checking if the
     * filterString is contained in the name of the file.
     *
     * @param filterString the {@link String} to check for
     * @return true if the filterString matches - false
     * otherwise
     */
    public boolean matches(String filterString) {
        return this.toString().contains(filterString)
                && Files.isRegularFile(this.file.toPath());
    }

    @Override
    public String toString() {
        return this.file.getName();
    }
}
