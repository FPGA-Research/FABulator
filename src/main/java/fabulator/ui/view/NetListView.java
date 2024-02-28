package fabulator.ui.view;

import fabulator.language.Text;
import fabulator.lookup.BitstreamConfiguration;
import fabulator.lookup.Net;
import fabulator.ui.builder.LabelBuilder;
import fabulator.ui.style.StyleClass;
import javafx.geometry.Insets;
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
    private TextField searchField;
    private HBox spacer;
    private Label hideLabel;
    private CheckBox hideEmptyNets;
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
        this.topBox = new HBox();
        this.topBox.setPadding(
                new Insets(4)
        );
        this.topBox.setSpacing(4);

        this.searchField = new TextField();
        this.searchField.promptTextProperty().bind(Text.SEARCH.stringProperty());
        this.searchField.textProperty().addListener((obs, old, now) -> this.filter());

        this.spacer = new HBox();
        HBox.setHgrow(this.spacer, Priority.ALWAYS);

        this.hideLabel = new LabelBuilder()
                .setText(Text.HIDE_EMPTY_NETS)
                .build();

        this.hideLabel.setPadding(
                new Insets(4)
        );

        this.hideEmptyNets = new CheckBox();
        this.hideEmptyNets.setSelected(true);
        this.hideEmptyNets.setPadding(
                new Insets(4)
        );
        this.hideEmptyNets.selectedProperty().addListener((obs, old, now) -> this.filter());

        this.listView = new ListView<>();
        this.listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        this.listView.prefHeightProperty().bind(
                this.heightProperty().subtract(this.topBox.heightProperty())
        );
    }

    private void setup() {
        this.topBox.getChildren().addAll(
                this.searchField,
                this.spacer,
                this.hideLabel,
                this.hideEmptyNets
        );

        this.getChildren().addAll(
                this.topBox,
                this.listView
        );
    }

    private void filter() {
        this.listView.getItems().clear();
        boolean hideEmpty = this.hideEmptyNets.isSelected();

        if (this.config != null) {
            HashMap<String, Net> netMap = this.config.getNetMap();

            for (Map.Entry<String, Net> entry : netMap.entrySet()) {
                String netName = entry.getKey();
                Net net = entry.getValue();

                boolean passesFilter = netName.contains(this.searchField.getText());

                if (!(hideEmpty && net.isEmpty()) && passesFilter) {
                    Label netLabel = new Label(netName);
                    netLabel.setOnMouseClicked(event -> {
                        this.displaySelectedNets();
                    });

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

    public void dropReferences() {
        this.config = null;
        this.listView.getItems().clear();
    }
}
