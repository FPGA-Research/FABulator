package fabulator.ui.window;

import fabulator.FABulator;
import fabulator.language.Text;
import fabulator.ui.icon.ImageIcon;
import fabulator.ui.style.Style;
import javafx.beans.property.StringProperty;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class FABulatorWindow extends Stage {

    protected Scene scene;
    protected Pane root;

    public FABulatorWindow(int width, int height, Text title, Modality modality) {
        this(width, height, title);
        this.initModality(modality);
    }

    public FABulatorWindow(int width, int height, Text title) {
        this.setTitle(title);
        this.setWidth(width);
        this.setHeight(height);
        this.setIcon();
        this.initScene();
        this.initListeners();
        this.setStyle();
    }

    private void setTitle(Text title) {
        StringProperty titleProperty = title.stringProperty();
        this.titleProperty().bind(titleProperty);
    }

    private void setIcon() {
        Image iconImage = ImageIcon.FABULOUS.getImage();
        this.getIcons().add(iconImage);
    }

    private void setStyle() {
        this.scene.setFill(Color.TRANSPARENT);
        this.scene.getStylesheets().addAll(
                Style.DARK.getStyleSheet(),
                Style.DARK.getIconSheet(),
                Style.DARK.getColorSheet()
        );
    }

    private void initScene() {
        this.root = new Pane();
        this.scene = new Scene(
                this.root,
                this.getWidth(),
                this.getHeight()
        );
        this.setScene(this.scene);
    }

    private void initListeners() {
        FABulator.getApplication().addClosedRequestListener(this::close);
    }

    public void setRoot(Pane root) {
        this.root = root;
        this.scene.setRoot(root);
    }
}
