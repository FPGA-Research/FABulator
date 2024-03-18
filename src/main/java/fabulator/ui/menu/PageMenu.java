package fabulator.ui.menu;

import fabulator.language.Text;
import fabulator.settings.SettingsView;
import fabulator.ui.builder.ButtonBuilder;
import fabulator.ui.icon.CssIcon;
import fabulator.ui.style.StyleClass;
import fabulator.ui.style.UiColor;
import fabulator.util.LayoutUtils;
import javafx.event.Event;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PageMenu extends HBox {

    @Getter
    @Setter
    @AllArgsConstructor
    private static class PageNodes {
        private Button pageButton;
        private Region page;
        private int index;
    }

    private List<PageNodes> pageNodesList;
    private PageNodes focusedPageNodes;

    private TabPane pageBox;
    private VBox highlights;


    public PageMenu() {
        this.pageNodesList = new ArrayList<>();
        this.getStyleClass().add("page-menu");
    }

    private void registerEventFilters() {
        this.pageNodesList.stream()
                .map(node -> node.pageButton)
                .filter(Objects::nonNull)
                .forEach(button -> button.addEventFilter(KeyEvent.ANY, Event::consume));
    }

    public void addPage(Button pageButton, Region page) {
        int index = this.pageNodesList.size();
        PageNodes nodes = new PageNodes(pageButton, page, index);
        this.pageNodesList.add(nodes);
    }

    public void build() {
        this.highlights = new VBox();
        this.highlights.getStyleClass().add("highlights");
        this.highlights.setPrefWidth(12);

        int numberCells = this.pageNodesList.size() + 2;
        int spacerIndex = numberCells - 2;
        this.buildHighlights(numberCells, spacerIndex);

        VBox buttons = new VBox();
        buttons.getStyleClass().add("buttons");

        for (PageNodes nodes : this.pageNodesList) {
            nodes.getPageButton().setOnAction(event -> this.changeTo(nodes));
        }

        buttons.getChildren().addAll(
                this.pageNodesList.stream()
                        .map(PageNodes::getPageButton)
                        .toList()
        );

        Region settingsPage = new SettingsView();
        PageNodes settingNodes = new PageNodes(null, settingsPage, numberCells - 1);
        Button settingsButton = new ButtonBuilder()
                .setIcon(CssIcon.SETTINGS)
                .setColor(UiColor.SECONDARY)
                .setTooltip(Text.SETTINGS_PAGE)
                .setOnAction(event -> this.changeTo(settingNodes))
                .build();
        buttons.getChildren().addAll(LayoutUtils.vSpacer(), settingsButton);

        PageNodes dummyNodes = new PageNodes(null, null, numberCells - 2);
        this.pageNodesList.add(dummyNodes);
        this.pageNodesList.add(settingNodes);

        this.pageBox = new TabPane();
        this.pageBox.getStyleClass().add(StyleClass.WIZARD.getName());
        this.pageBox.getTabs().addAll(
                this.pageNodesList.stream()
                        .map(PageNodes::getPage)
                        .map(page -> new Tab(null, page))
                        .toList()
        );
        HBox.setHgrow(this.pageBox, Priority.ALWAYS);

        this.getChildren().addAll(
                this.highlights,
                buttons,
                this.pageBox
        );

        this.registerEventFilters();
    }

    private void buildHighlights(int numberCells, int spacerIndex) {
        for (int i = 0; i < numberCells; i++) {
            if (i == spacerIndex) {
                Region highlightSpacer = new Region();
                VBox.setVgrow(highlightSpacer, Priority.ALWAYS);
                this.highlights.getChildren().add(highlightSpacer);
                continue;
            }

            Region highlight = new Region();
            highlight.setPrefHeight(20);
            highlight.getStyleClass().add("highlight");
            highlight.setId("highlight-off");
            this.highlights.getChildren().add(highlight);
        }
    }

    public void changeTo(int index) {
        this.changeTo(this.pageNodesList.get(index));
    }

    private void changeTo(PageNodes nodes) {
        if (nodes != this.focusedPageNodes) {

            if (this.focusedPageNodes != null) {
                this.highlights.getChildren()
                        .get(this.focusedPageNodes.getIndex())
                        .setId("highlight-off");
            }

            this.pageBox.getSelectionModel().select(
                    nodes.getIndex()
            );
            this.focusedPageNodes = nodes;

            this.highlights.getChildren()
                    .get(this.focusedPageNodes.getIndex())
                    .setId("highlight-on");
        }
    }
}
