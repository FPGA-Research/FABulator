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

public class FileInfoView extends HBox implements View {

    @Getter
    private TreeItem<FileInfoView> wrapper;

    @Getter
    private File file;
    private Label label;
    private Node graphic;

    public FileInfoView(File file, TreeItem<FileInfoView> wrapper) {
        this.file = file;
        this.wrapper = wrapper;
        this.init();
    }

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

    @Override
    public void buildWhole() {
        this.getChildren().addAll(
                this.graphic,
                this.label
        );
    }

    public void setOnClicked(Consumer<FileInfoView> handler) {
        this.setOnMouseClicked(event -> {
            if (Files.isRegularFile(this.file.toPath())) {
                handler.accept(this);
            }
        });
    }

    public boolean matches(String filterString) {
        return this.file.getName().contains(filterString)
                && Files.isRegularFile(this.file.toPath());
    }

    @Override
    public String toString() {
        return this.file.getName();
    }
}
