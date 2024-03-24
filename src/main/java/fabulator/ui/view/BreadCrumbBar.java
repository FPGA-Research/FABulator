package fabulator.ui.view;

import fabulator.ui.builder.ButtonBuilder;
import fabulator.ui.icon.ImageIcon;
import fabulator.ui.style.StyleClass;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.HBox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A minimalistic breadcrumb bar, can be used for indicating
 * a position in a tree-like structure, like a file system.
 * For the breadcrumb button, an ImageIcon rather than a
 * CssIcon is used, since the latter takes a bit longer to
 * load, making for a worse user experience when the
 * BreadCrumbBar is updated.
 *
 * @param <T> the type of the bread crumb item
 */
public class BreadCrumbBar<T> extends HBox implements View {

    private TreeItem<T> selected;
    private List<Button> buttons;

    /**
     * Constructs a {@link BreadCrumbBar} object.
     */
    public BreadCrumbBar() {
        this.getStyleClass().add(StyleClass.BREAD_CRUMB_BAR.getName());
        this.init();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void buildParts() {
        this.buttons = new ArrayList<>();
        TreeItem<T> current = this.selected;

        while (current != null) {
            Button button = new ButtonBuilder()
                    .setString(current.getValue().toString())
                    .setIcon(ImageIcon.CHEVRON)
                    .build();

            this.buttons.add(button);
            current = current.getParent();
        }
        Collections.reverse(this.buttons);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void buildWhole() {
        this.getChildren().clear();
        this.getChildren().addAll(
                this.buttons
        );
    }

    /**
     * Selects the specified {@link TreeItem<T>} object and
     * displays it and all its parent objects up to the root
     * object in the BreadCrumbBar from right to left.
     *
     * @param item the {@link TreeItem<T>} object to display
     */
    public void setSelected(TreeItem<T> item) {
        this.selected = item;
        this.init();
    }
}
