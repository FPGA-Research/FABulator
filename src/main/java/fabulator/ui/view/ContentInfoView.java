package fabulator.ui.view;

import fabulator.language.Text;
import fabulator.ui.builder.TabBuilder;
import fabulator.ui.fabric.Fabric;
import fabulator.ui.fabric.element.FabricElement;
import fabulator.lookup.BitstreamConfiguration;
import fabulator.lookup.Net;
import fabulator.memory.ReferenceHolder;
import fabulator.ui.menu.FabricMenu;
import fabulator.ui.style.StyleClass;
import javafx.geometry.Bounds;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.util.List;

public class ContentInfoView extends TabPane implements ReferenceHolder {

    private FabricMenu parent;

    private Tab worldViewTab;
    private Tab elementViewTab;
    private Tab hdlViewTab;
    private Tab netListTab;

    private WorldView worldView;
    private ElementView elementView;
    private HdlView hdlView;
    private NetListView netListView;

    public ContentInfoView(FabricMenu parent) {
        this.getStyleClass().add(StyleClass.CONTENT_INFO_VIEW.getName());
        this.parent = parent;

        this.initialize();
        this.setup();
    }

    private void initialize() {
        this.worldView = new WorldView(this);
        this.elementView = new ElementView(this);
        this.hdlView = new HdlView();
        this.netListView = new NetListView(this);

        this.worldViewTab = new TabBuilder()
                .setText(Text.WORLD_VIEW)
                .setContent(this.worldView)
                .build();

        this.elementViewTab = new TabBuilder()
                .setText(Text.ELEMENT_VIEW)
                .setContent(this.elementView)
                .build();

        this.hdlViewTab = new TabBuilder()
                .setText(Text.HDL_VIEW)
                .setContent(this.hdlView)
                .build();

        this.netListTab = new TabBuilder()
                .setText(Text.NETLIST_VIEW)
                .setContent(this.netListView)
                .build();
    }

    private void setup() {
        this.getTabs().addAll(
                this.worldViewTab,
                this.elementViewTab,
                this.hdlViewTab,
                this.netListTab
        );
        for (Tab tab : this.getTabs()) tab.setClosable(false);
    }

    public void navigateTo(FabricElement element) {
        this.parent.navigateTo(element);
    }

    public void setNewFabric(Fabric fabric) {
        this.elementView.setNewFabric(fabric);
        this.worldView.setNewFabric(fabric);
    }

    public void displayBitstreamConfig(BitstreamConfiguration config) {
        this.netListView.displayBitstreamConfig(config);
    }

    public void displayNets(List<Net> nets) {
        this.parent.displayNets(nets);
    }

    public void openHdl(List<String> hdl) {
        this.hdlView.openHdl(hdl);
    }

    public void updateWorldView(Bounds viewPortBounds) {
        this.worldView.updateIndicator(viewPortBounds);
    }

    @Override
    public void dropReferences() {
        this.worldView.dropReferences();
        this.elementView.dropReferences();
        this.netListView.dropReferences();
        // TODO: drop references in hdlView too
    }
}
