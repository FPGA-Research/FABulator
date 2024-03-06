package fabulator.ui.view;

import fabulator.FABulator;
import fabulator.language.Text;
import fabulator.lookup.BitstreamConfiguration;
import fabulator.memory.ReferenceHolder;
import fabulator.ui.builder.ButtonBuilder;
import fabulator.ui.fabric.Fabric;
import fabulator.ui.icon.CssIcon;
import fabulator.ui.menu.BottomMenu;
import fabulator.ui.menu.FabricMenu;
import fabulator.ui.menu.PageMenu;
import fabulator.ui.menu.TopMenu;
import fabulator.ui.style.UiColor;
import javafx.geometry.Bounds;
import javafx.scene.control.Button;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;

public class MainView extends VBox implements ReferenceHolder {

    private TopMenu topMenu;
    private PageMenu pageMenu;

    private FabricMenu fabricMenu;
    private BottomMenu bottomMenu;

    public MainView() {
        this.initialize();
        this.setup();
    }

    private void initialize() {
        this.topMenu = new TopMenu();
        this.pageMenu = new PageMenu();
        this.pageMenu.prefWidthProperty().bind(
                FABulator.getApplication()
                        .getStage()
                        .widthProperty()
        );
        VBox.setVgrow(this.pageMenu, Priority.ALWAYS);

        this.fabricMenu = new FabricMenu();
        this.bottomMenu = new BottomMenu(this.fabricMenu);
    }

    private void setup() {
        VBox fabricPage = new VBox(this.fabricMenu, this.bottomMenu);
        Button fabricPageButton = new ButtonBuilder()
                .setIcon(CssIcon.CHIP)
                .setColor(UiColor.GREEN)
                .setTooltip(Text.FABRIC_PAGE)
                .build();
        this.pageMenu.addPage(fabricPageButton, fabricPage);

        this.pageMenu.build();
        this.pageMenu.changeTo(0);

        this.getChildren().addAll(
                this.topMenu,
                this.pageMenu
        );
    }

    @Override
    public void dropReferences() {
        this.fabricMenu.dropReferences();
        this.bottomMenu.dropReferences();
    }

    public void setNewFabric(Fabric fabric) {
        this.fabricMenu.setNewFabric(fabric);
    }

    public void displayBitstreamConfig(BitstreamConfiguration config) {
        this.fabricMenu.displayBitstreamConfig(config);
    }

    public void openHdl(List<String> hdl) {
        this.fabricMenu.openHdl(hdl);
    }

    public void zoomIn() {
        this.fabricMenu.zoomIn();
    }

    public void zoomOut() {
        this.fabricMenu.zoomOut();
    }

    public void updateWorldView(Bounds viewPortBounds) {
        this.fabricMenu.updateWorldView(viewPortBounds);
    }
}