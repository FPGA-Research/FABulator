package fabulator.ui.view;

import fabulator.memory.ReferenceHolder;
import fabulator.ui.fabric.Bel;
import fabulator.ui.fabric.Fabric;
import fabulator.ui.fabric.Tile;
import fabulator.ui.style.StyleClass;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class ElementView extends TabPane implements ReferenceHolder {

    static class FabricElement extends Label {
    }

    private ContentInfoView parent;

    private Fabric fabric;
    private List<Tab> columnTabs;

    private List<List<TreeItem<FabricElement>>> tileItems;


    public ElementView(ContentInfoView parent) {
        this.getStyleClass().add(StyleClass.ELEMENT_VIEW.getName());
        this.parent = parent;

        this.initialize();
        this.setup();
    }

    private void initialize() {
        if (this.fabric == null) return;

        this.columnTabs = new ArrayList<>();
        this.tileItems = new ArrayList<>();

        int columns = this.fabric.getGeometry().getNumberOfColumns();
        int rows = this.fabric.getGeometry().getNumberOfRows();

        for (int x = 0; x < columns; x++) {

            Tab columnTab = new Tab();
            columnTab.setText("X" + x);
            columnTab.setClosable(false);

            VBox rowBox = new VBox();
            ScrollPane scrollPane = new ScrollPane(rowBox);
            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(true);

            FabricElement rootElement = new FabricElement();
            TreeItem<FabricElement> root = new TreeItem<>(rootElement);
            root.setExpanded(true);
            TreeView<FabricElement> tree = new TreeView<>();
            tree.setRoot(root);
            tree.setPrefHeight(1080);       // less dirty solution?
            rowBox.getChildren().add(tree);

            List<TreeItem<FabricElement>> itemColumn = new ArrayList<>();

            for (int y = 0; y < rows; y++) {
                FabricElement tileElement = new FabricElement();
                tileElement.setText(
                        this.fabric.getGeometry()
                                .getTileNames()
                                .get(y).get(x)
                );
                TreeItem<FabricElement> tileItem = new TreeItem<>(tileElement);
                root.getChildren().add(tileItem);
                itemColumn.add(tileItem);
            }
            columnTab.setContent(scrollPane);
            this.tileItems.add(itemColumn);
            this.columnTabs.add(columnTab);
        }
        this.getTabs().clear();
        this.getTabs().addAll(this.columnTabs);
    }

    private void setup() {
        if (this.fabric == null) return;

        int columns = this.fabric.getGeometry().getNumberOfColumns();
        int rows = this.fabric.getGeometry().getNumberOfRows();


        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < columns; y++) {
                Tile tile = this.fabric.getTile(x, y);
                this.setupTile(tile, this.tileItems.get(y).get(x));
            }
        }
    }

    private void setupTile(Tile tile, TreeItem<FabricElement> tileItem) {
        if (tile == null) return;

        FabricElement smElement = new FabricElement();
        smElement.setText(
                tile.getSwitchMatrix().getGeometry().getName()
        );
        TreeItem<FabricElement> switchMatrixItem = new TreeItem<>();
        switchMatrixItem.setValue(smElement);
        tileItem.getChildren().add(switchMatrixItem);

        smElement.setOnMouseClicked(event -> {
            this.parent.navigateTo(tile.getSwitchMatrix());
        });

        for (Bel bel : tile.getBels()) {
            FabricElement belElement = new FabricElement();
            belElement.setText(
                    bel.getGeometry().getName()
            );
            TreeItem<FabricElement> belItem = new TreeItem<>();
            belItem.setValue(belElement);
            tileItem.getChildren().add(belItem);

            belElement.setOnMouseClicked(event -> {
                this.parent.navigateTo(bel);
            });
        }
    }

    @Override
    public void dropReferences() {
        this.getTabs().clear();
        this.columnTabs = null;
        this.tileItems = null;
        this.fabric = null;
    }

    public void setNewFabric(Fabric fabric) {
        this.fabric = fabric;

        this.initialize();
        this.setup();
    }
}