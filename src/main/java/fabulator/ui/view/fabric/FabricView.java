package fabulator.ui.view.fabric;

import fabulator.language.Text;
import fabulator.ui.fabric.Fabric;
import fabulator.ui.fabric.element.ElementType;
import fabulator.ui.fabric.element.FabricElement;
import fabulator.geometry.Location;
import fabulator.lookup.BitstreamConfiguration;
import fabulator.lookup.Net;
import fabulator.ui.style.StyleClass;
import fabulator.memory.ReferenceHolder;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public class FabricView extends VBox implements ReferenceHolder {

    private FabricPane fabricPane;
    private Group contentGroup;
    private TabPane tabPane;

    private Fabric fabric;


    public FabricView() {
        this.getStyleClass().add(StyleClass.FABRIC_VIEW.getName());

        this.initialize();
        this.setup();
    }

    private void initialize() {
        this.contentGroup = new Group();

        VBox wrapperBox = new VBox();
        wrapperBox.getChildren().add(this.contentGroup);

        this.fabricPane = new FabricPane(wrapperBox);
        this.fabricPane.setBackground(new Background(new BackgroundFill(
                Color.BLACK, null, null
        )));

        String tabName = Text.EMPTY.stringProperty().get();
        if (this.fabric != null) {
            this.fabricPane.setFabric(this.fabric);
            tabName = this.fabric.getGeometry().getName();
        }

        Tab wrapperTab = new Tab(tabName, this.fabricPane);
        wrapperTab.setClosable(false);
        this.tabPane = new TabPane(wrapperTab);
        VBox.setVgrow(this.tabPane, Priority.ALWAYS);
    }

    private void setup() {
        this.getChildren().clear();
        if (this.fabric != null) {
            this.contentGroup.getChildren().add(this.fabric);
            Platform.runLater(() -> {
                this.fabricPane.updateWorldView();
                this.fabricPane.updateLod();
            });
        }
        this.getChildren().add(this.tabPane);
    }

    public void navigateTo(FabricElement element) {
        this.navigateToGlobal(
                element.getGlobalLocation(),
                element.getViewableZoom()
        );
    }

    public void navigateToGlobal(Location location, double viewableZoom) {
        this.fabricPane.navigateToGlobal(
                location.getX(),
                location.getY(),
                viewableZoom
        );
    }

    @Override
    public void dropReferences() {
        Tab wrapperTab = this.tabPane.getTabs().get(0);
        wrapperTab.setText(Text.EMPTY.stringProperty().get());

        this.fabricPane = new FabricPane(new VBox());
        this.fabricPane.setBackground(new Background(new BackgroundFill(
                Color.BLACK, null, null
        )));
        wrapperTab.setContent(this.fabricPane);

        this.fabricPane = null;
        this.contentGroup = null;
        this.fabric = null;
    }

    public void setNewFabric(Fabric fabric) {
        this.fabric = fabric;

        this.initialize();
        this.setup();
    }

    public void displayBitstreamConfig(BitstreamConfiguration config) {
        if (this.fabric != null) {
            this.fabric.displayBitstreamConfig(config);
        }
    }

    public void clearNets() {
        if (this.fabric != null) {
            this.fabric.clearNets();
        }
    }

    public Location displayNet(Net net) {
        Location averageLocation = new Location();

        if (this.fabric != null) {
            averageLocation = this.fabric.displayNet(net);
        }
        return averageLocation;
    }

    public List<FabricElement> search(ElementType type, Pattern regex) {
        List<FabricElement> result = new LinkedList<>();
        if (this.fabric != null) {
            this.fabric.filterAndAdd(result, type, regex);
        }
        return result;
    }

    public void zoomIn() {
        this.fabricPane.zoomIn();
    }

    public void zoomOut() {
        this.fabricPane.zoomOut();
    }
}
