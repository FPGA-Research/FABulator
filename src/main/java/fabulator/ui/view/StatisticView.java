package fabulator.ui.view;

import fabulator.object.Statistics;
import fabulator.object.StatisticsCategory;
import fabulator.object.StatisticsSection;
import fabulator.ui.builder.LabelBuilder;
import fabulator.ui.builder.TitledPaneBuilder;
import fabulator.ui.style.StyleClass;
import fabulator.util.LayoutUtils;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class StatisticView extends ScrollPane implements View {

    private Statistics statistics;
    private List<Node> categoryNodes = new ArrayList<>();

    public StatisticView() {
        this.setFitToWidth(true);
        this.getStyleClass().add(StyleClass.STATISTIC_VIEW.getName());
        this.init();
    }

    @Override
    public void buildParts() {
        this.categoryNodes.clear();

        if (this.statistics != null) {
            for (StatisticsCategory category : this.statistics.getCategories()) {
                Node categoryNode = this.buildCategory(category);
                this.categoryNodes.add(categoryNode);
            }
        }
    }

    private Node buildCategory(StatisticsCategory category) {
        switch (category.getType()) {
            case SIMPLE -> {
                return this.buildSimpleCategory(category);
            }
            case COMPLEX, default -> {
                return this.buildComplexCategory(category);
            }
        }
    }

    private Node buildSimpleCategory(StatisticsCategory category) {
        HBox content = new HBox();

        content.getChildren().addAll(
                new LabelBuilder()
                        .setFontSize(16)
                        .setText(category.getName())
                        .build(),
                LayoutUtils.hSpacer(),
                new LabelBuilder()
                        .setFontSize(16)
                        .setText(category.getValue())
                        .build()
        );

        return content;
    }

    private Node buildComplexCategory(StatisticsCategory category) {
        VBox sectionBox = new VBox();
        sectionBox.setSpacing(4);
        sectionBox.setPadding(
                new Insets(0, 0, 0, 24)
        );

        for (StatisticsSection section : category.getSections()) {
            Node sectionNode = this.buildSection(section);
            sectionBox.getChildren().add(sectionNode);
        }

        VBox content = new VBox(
                new HBox(
                        new LabelBuilder()
                                .setFontSize(16)
                                .setText(category.getName())
                                .build(),
                        LayoutUtils.hSpacer(),
                        new LabelBuilder()
                                .setFontSize(16)
                                .setText(category.getValue())
                                .build()
                ),
                sectionBox
        );

        return content;
    }

    public Node buildSection(StatisticsSection section) {
        VBox sectionBox = new VBox();
        section.getEntries().forEach(entry -> {
            HBox entryBox = new HBox(
                    new LabelBuilder()
                            .setText(entry.getKey())
                            .build(),
                    LayoutUtils.hSpacer(),
                    new LabelBuilder()
                            .setText(entry.getValue())
                            .build()
            );
            sectionBox.getChildren().add(entryBox);
        });

        TitledPane sectionPane = new TitledPaneBuilder()
                .setKeyValue(section.getName(), section.getValue())
                .setContent(sectionBox)
                .build();

        Accordion sectionAccordion = new Accordion(
                sectionPane
        );
        return sectionAccordion;
    }

    @Override
    public void buildWhole() {
        VBox contentBox = new VBox();
        contentBox.getChildren().addAll(
                this.categoryNodes
        );
        contentBox.setSpacing(8);
        this.setContent(contentBox);
    }

    public void show(Statistics statistics) {
        this.statistics = statistics;
        this.init();
    }
}
