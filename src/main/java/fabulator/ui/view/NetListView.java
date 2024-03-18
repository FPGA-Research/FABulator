package fabulator.ui.view;

import fabulator.FABulator;
import fabulator.language.Text;
import fabulator.lookup.BitstreamConfiguration;
import fabulator.lookup.Net;
import fabulator.ui.builder.ButtonBuilder;
import fabulator.ui.builder.CheckBoxBuilder;
import fabulator.ui.builder.TextFieldBuilder;
import fabulator.ui.icon.CssIcon;
import fabulator.ui.style.StyleClass;
import fabulator.util.LayoutUtils;
import javafx.collections.ListChangeListener;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NetListView extends VBox {

    private ContentInfoView parent;

    private HBox topBox;
    private HBox bottomBox;
    private TextField searchField;
    private CheckBox hideEmptyNets;
    private Button eraseFasmButton;
    private ListView<Label> listView;

    private BitstreamConfiguration config;

    public NetListView(ContentInfoView parent) {
        this.getStyleClass().add(StyleClass.NETLIST_VIEW.getName());
        this.parent = parent;
        VBox.setVgrow(this, Priority.ALWAYS);

        this.initialize();
        this.setup();
    }

    private void initialize() {
        this.initializeTopBox();
        this.initializeListView();
        this.initializeBottomBox();
    }

    private void initializeTopBox() {
        this.topBox = new HBox();

        this.searchField = new TextFieldBuilder()
                .setPrompt(Text.SEARCH)
                .setOnChanged(this::filter)
                .build();

        this.hideEmptyNets = new CheckBoxBuilder()
                .setText(Text.HIDE_EMPTY_NETS)
                .setOnChanged(bool -> this.filter())
                .build();
    }

    private void initializeListView() { //TODO: builder
        this.listView = new ListView<>();
        this.listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        ListChangeListener<Label> selectionListener = event -> this.displaySelectedNets();
        this.listView.getSelectionModel().getSelectedItems().addListener(selectionListener);
        this.listView.prefHeightProperty().bind(
                this.heightProperty().subtract(this.topBox.heightProperty())
        );
    }

    private void initializeBottomBox() {
        this.bottomBox = new HBox();

        this.eraseFasmButton = new ButtonBuilder()
                .setIcon(CssIcon.ERASE)
                .setText(Text.ERASE_FASM)
                .setTooltip(Text.ERASE_FASM)
                .setOnAction(event -> this.eraseFasm())
                .build();

    }

    private void setup() {
        this.topBox.getChildren().addAll(
                this.searchField,
                LayoutUtils.hSpacer(),
                this.hideEmptyNets
        );

        this.bottomBox.getChildren().addAll(
                LayoutUtils.hSpacer(),
                this.eraseFasmButton
        );

        this.getChildren().addAll(
                this.topBox,
                this.listView,
                this.bottomBox
        );
    }

    private void filter() {
        String filterString = this.searchField.getText();
        this.filter(filterString);
    }

    private void filter(String filterString) {
        this.listView.getItems().clear();
        boolean hideEmpty = this.hideEmptyNets.isSelected();

        if (this.config != null) {
            HashMap<String, Net> netMap = this.config.getNetMap();

            for (Map.Entry<String, Net> entry : netMap.entrySet()) {
                String netName = entry.getKey();
                Net net = entry.getValue();

                boolean passesFilter = netName.contains(filterString);

                if (!(hideEmpty && net.isEmpty()) && passesFilter) {
                    Label netLabel = new Label(netName);
                    this.listView.getItems().add(netLabel);
                }
            }
        }
    }

    private void displaySelectedNets() {
        HashMap<String, Net> netMap = this.config.getNetMap();

        List<Net> nets = this.listView.getSelectionModel()
                .getSelectedItems()
                .stream()
                .map(label -> netMap.get(label.getText()))
                .collect(Collectors.toList());

        this.parent.displayNets(nets);
    }

    public void displayBitstreamConfig(BitstreamConfiguration config) {
        this.dropReferences();
        this.config = config;
        this.filter();
    }

    public void eraseFasm() {
        FABulator.getApplication()
                .getMainView()
                .displayBitstreamConfig(BitstreamConfiguration.empty());
    }

    public void dropReferences() {
        this.config = null;
        this.listView.getItems().clear();
    }
}
