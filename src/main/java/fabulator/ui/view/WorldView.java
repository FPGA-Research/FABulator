package fabulator.ui.view;

import fabulator.ui.fabric.Fabric;
import fabulator.ui.style.StyleClass;
import fabulator.ui.builder.LabelBuilder;
import fabulator.ui.builder.RectangleBuilder;
import fabulator.ui.fabric.Tile;
import fabulator.geometry.FabricGeometry;
import fabulator.geometry.Location;
import fabulator.geometry.TileGeometry;
import fabulator.memory.ReferenceHolder;
import fabulator.util.TileColorUtils;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WorldView extends StackPane implements ReferenceHolder {

    private static final double FAB_WIDTH_RATIO = 0.8;
    private static final double FAB_HEIGHT_RATIO = 0.8;

    private ContentInfoView parent;
    private Fabric fabric;

    private double scale;
    private VBox miniFabric;

    @Getter
    @Setter
    private static class ScaleContextShape {
        private Rectangle shape;
        private double originalX;
        private double originalY;
        private double originalWidth;
        private double originalHeight;
        private double originalStroke;

        public ScaleContextShape(Rectangle shape) {
            this.shape = shape;
            this.originalX = shape.getTranslateX();
            this.originalY = shape.getTranslateY();
            this.originalWidth = shape.getWidth();
            this.originalHeight = shape.getHeight();
            this.originalStroke = shape.getStrokeWidth();
        }
    }

    @Getter
    @Setter
    private static class ScaledLabel {
        private Label label;
        private double originalFontSize;
        private double originalX;
        private double originalY;
        private double originalWidth;
        private double originalHeight;
        private int sizeFactor;

        public ScaledLabel(Label label) {
            this.label = label;
            this.originalFontSize = label.getFont().getSize();
            this.originalX = label.getTranslateX();
            this.originalY = label.getTranslateY();
            this.originalWidth = label.getPrefWidth();
            this.originalHeight = label.getPrefHeight();
            this.sizeFactor = 1;
        }
    }

    private ScaleContextShape positionIndicator;
    private List<ScaleContextShape> scaledShapes;
    private List<ScaledLabel> scaledLabels;

    public WorldView(ContentInfoView parent) {
        this.getStyleClass().add(StyleClass.WORLD_VIEW.getName());

        this.parent = parent;
        this.scaledShapes = new ArrayList<>();
        this.scaledLabels = new ArrayList<>();

        this.initialize();
        this.setup();
    }

    private void initialize() {
        this.miniFabric = new VBox();

        Rectangle indicatorRect = new RectangleBuilder()
                .setArcDims(4, 4)
                .setFill(Color.TRANSPARENT)
                .setStroke(Color.WHITE, 32)
                .setMouseTransparent(true)
                .build();

        this.positionIndicator = new ScaleContextShape(indicatorRect);

        this.parent.widthProperty().addListener((obs, old, now) -> this.updateScale());
        this.parent.heightProperty().addListener((obs, old, now) -> this.updateScale());
    }

    private void setup() {
        this.scaledShapes.clear();
        if (this.fabric == null) return;

        FabricGeometry geometry = this.fabric.getGeometry();
        List<List<Location>> tileLocations = geometry.getTileLocations();
        List<List<String>> tileNames = geometry.getTileNames();
        Map<String, TileGeometry> tileGeomMap = geometry.getTileGeomMap();

        Group miniTileGroup = new Group();

        int averageTileWidth = geometry.getWidth() / geometry.getNumberOfColumns();
        int averageTileHeight = geometry.getHeight() / geometry.getNumberOfRows();
        double fontSize = Math.min(averageTileWidth, averageTileHeight) / 2.5;

        for (int x = 0; x < tileLocations.size(); x++) {
            List<Location> locRow = tileLocations.get(x);

            for (int y = 0; y < locRow.size(); y++) {
                Location tileLoc = tileLocations.get(x).get(y);
                if (tileLoc == null) continue;

                String tileName = tileNames.get(x).get(y);
                TileGeometry tileGeom = tileGeomMap.get(tileName);
                Tile tile = this.fabric.getTile(x, y);
                double tileStroke = 32;

                EventHandler<MouseEvent> clickHandler = event -> {
                    this.parent.navigateTo(tile);
                };

                Rectangle tileRect = new RectangleBuilder()
                        .setDims(tileGeom.getWidth(), tileGeom.getHeight())
                        .setTranslateX(tileLoc.getX())
                        .setTranslateY(tileLoc.getY())
                        .setFill(TileColorUtils.colorOfTile(tileGeom.getName()))
                        .setOpacity(0.3)
                        .setStroke(Color.BLACK, tileStroke)
                        .setOnMouseClicked(clickHandler)
                        .build();

                miniTileGroup.getChildren().add(tileRect);
                ScaleContextShape contextShape = new ScaleContextShape(tileRect);
                this.scaledShapes.add(contextShape);

                if (x == 0) {
                    double offsetY = tileGeom.getHeight() * 3;

                    Label label = new LabelBuilder()
                            .setText(String.format("%d", y))
                            .setFontSize(fontSize)
                            .setTranslateX(tileLoc.getX())
                            .setTranslateY(tileLoc.getY() - offsetY)
                            .setPrefWidth(tileGeom.getWidth())
                            .setPrefHeight(tileGeom.getHeight() * 2.75)
                            .setAlignment(Pos.CENTER)
                            .setTextColor(Color.WHITE)
                            .build();
                    miniTileGroup.getChildren().add(label);
                    ScaledLabel scaledLabel = new ScaledLabel(label);
                    this.scaledLabels.add(scaledLabel);
                }
                if (y == 0) {
                    double offsetX = tileGeom.getWidth() * 3;

                    Label label = new LabelBuilder()
                            .setText(String.format("%d", x))
                            .setFontSize(fontSize)
                            .setTranslateX(tileLoc.getX() - offsetX)
                            .setTranslateY(tileLoc.getY())
                            .setPrefWidth(tileGeom.getWidth() * 2.75)
                            .setPrefHeight(tileGeom.getHeight())
                            .setAlignment(Pos.CENTER)
                            .setTextColor(Color.WHITE)
                            .build();
                    miniTileGroup.getChildren().add(label);
                    ScaledLabel scaledLabel = new ScaledLabel(label);
                    this.scaledLabels.add(scaledLabel);
                }
            }
        }
        miniTileGroup.getChildren().add(this.positionIndicator.getShape());
        this.scaledShapes.add(this.positionIndicator);
        this.positionIndicator.getShape().toFront();

        this.miniFabric.getChildren().add(miniTileGroup);
        this.miniFabric.setAlignment(Pos.CENTER);
        this.getChildren().add(this.miniFabric);
    }

    public void setNewFabric(Fabric fabric) {
        this.fabric = fabric;
        this.setup();
        this.updateScale();
    }

    private void updateScale() {
        if (this.fabric == null) return;

        double paneWidth = this.parent.getWidth();
        double paneHeight = this.parent.getHeight();

        double maxWidth = paneWidth * FAB_WIDTH_RATIO;
        double maxHeight = paneHeight * FAB_HEIGHT_RATIO;

        FabricGeometry geometry = this.fabric.getGeometry();
        double widthScale = maxWidth / geometry.getWidth();
        double heightScale = maxHeight / geometry.getHeight();
        this.scale = Math.min(widthScale, heightScale);

        for (ScaleContextShape contextShape : this.scaledShapes) {
            Rectangle shape = contextShape.getShape();
            shape.setTranslateX(contextShape.getOriginalX() * this.scale);
            shape.setTranslateY(contextShape.getOriginalY() * this.scale);
            shape.setWidth(contextShape.getOriginalWidth() * this.scale);
            shape.setHeight(contextShape.getOriginalHeight() * this.scale);
            shape.setStrokeWidth(contextShape.getOriginalStroke() * this.scale);
        }

        for (ScaledLabel scaledLabel : this.scaledLabels) {
            Font labelFont = new Font(
                    scaledLabel.originalFontSize
                            * scaledLabel.sizeFactor
                            * this.scale
            );
            Label label = scaledLabel.getLabel();
            label.setTranslateX(scaledLabel.getOriginalX() * this.scale);
            label.setTranslateY(scaledLabel.getOriginalY() * this.scale);
            label.setPrefWidth(scaledLabel.getOriginalWidth() * this.scale);
            label.setPrefHeight(scaledLabel.getOriginalHeight() * this.scale);
            label.setFont(labelFont);
        }
    }

    public void updateIndicator(Bounds viewPortBounds) {
        double minX = viewPortBounds.getMinX();
        double minY = viewPortBounds.getMinY();
        double width = viewPortBounds.getWidth();
        double height = viewPortBounds.getHeight();

        this.positionIndicator.setOriginalX(minX);
        this.positionIndicator.setOriginalY(minY);
        this.positionIndicator.setOriginalWidth(width);
        this.positionIndicator.setOriginalHeight(height);
        this.positionIndicator.getShape().setTranslateX(minX * this.scale);
        this.positionIndicator.getShape().setTranslateY(minY * this.scale);
        this.positionIndicator.getShape().setWidth(width * this.scale);
        this.positionIndicator.getShape().setHeight(height * this.scale);

        FabricGeometry geometry = this.fabric.getGeometry();
        boolean tooWide = (width > geometry.getWidth());
        boolean tooTall = (height > geometry.getHeight());
        boolean tooBig = (tooWide || tooTall);
        this.positionIndicator.getShape().setVisible(!tooBig);

        double indicatorCenterX = viewPortBounds.getCenterX();
        double indicatorCenterY = viewPortBounds.getCenterY();

        for (ScaledLabel scaledLabel : this.scaledLabels) {
            double labelX = scaledLabel.getOriginalX();
            double labelY = scaledLabel.getOriginalY();
            double labelWidth = scaledLabel.getOriginalWidth();
            double labelHeight = scaledLabel.getOriginalHeight();

            boolean xValid = (0 <= indicatorCenterX) && (indicatorCenterX <= geometry.getWidth());
            boolean yValid = (0 <= indicatorCenterY) && (indicatorCenterY <= geometry.getHeight());
            boolean inXSpan = xValid && (labelX < indicatorCenterX) && (indicatorCenterX <= labelX + labelWidth);
            boolean inYSpan = yValid && (labelY < indicatorCenterY) && (indicatorCenterY <= labelY + labelHeight);

            if (inXSpan || inYSpan) {
                scaledLabel.setSizeFactor(2);
            } else {
                scaledLabel.setSizeFactor(1);
            }
        }
        for (ScaledLabel scaledLabel : this.scaledLabels) {
            Font labelFont = new Font(
                    scaledLabel.originalFontSize
                            * scaledLabel.sizeFactor
                            * this.scale
            );
            scaledLabel.getLabel().setFont(labelFont);
        }
    }

    @Override
    public void dropReferences() {
        this.fabric = null;
        this.scaledShapes.clear();
        this.scaledLabels.clear();
        this.miniFabric.getChildren().clear();
        this.getChildren().clear();
    }
}
