package fabulator.ui.view;

import fabulator.geometry.FabricGeometry;
import fabulator.geometry.TileGeometry;
import fabulator.memory.ReferenceHolder;
import fabulator.object.DiscreteLocation;
import fabulator.object.Location;
import fabulator.ui.builder.RectangleBuilder;
import fabulator.ui.fabric.Fabric;
import fabulator.ui.style.StyleClass;
import fabulator.util.TileColorUtils;
import javafx.beans.binding.Bindings;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class WorldView extends VBox implements ReferenceHolder {

    private ContentInfoView parent;
    private MiniFabric miniFabric;

    @Getter
    private class MiniFabric extends Group {
        private Fabric fabric;
        private List<List<Rectangle>> miniTiles = new ArrayList<>();
        private Rectangle viewPortIndicator;
        private Rectangle backgroundRect;

        private Date lastNavigation = new Date();

        public MiniFabric(Fabric fabric) {
            this.fabric = fabric;
            this.build();
        }

        private void build() {
            FabricGeometry fabricGeometry = this.fabric.getGeometry();
            List<List<String>> tileNames = fabricGeometry.getTileNames();
            List<List<Location>> tileLocations = fabricGeometry.getTileLocations();
            Map<String, TileGeometry> tileGeomMap = fabricGeometry.getTileGeomMap();

            this.backgroundRect = new RectangleBuilder()
                    .setDims(fabricGeometry.getWidth(), fabricGeometry.getHeight())
                    .setFill(Color.TRANSPARENT)
                    .build();
            this.getChildren().add(this.backgroundRect);

            for (int y = 0; y < fabricGeometry.getNumberOfRows(); y++) {
                List<Rectangle> currentRow = new ArrayList<>();
                this.miniTiles.add(currentRow);
                for (int x = 0; x < fabricGeometry.getNumberOfColumns(); x++) {
                    String tileName = tileNames.get(y).get(x);
                    TileGeometry tileGeom = tileGeomMap.get(tileName);

                    if (tileGeom == null) {
                        currentRow.add(null);
                        continue;
                    }

                    Location tileLoc = tileLocations.get(y).get(x);

                    Tooltip miniTileTooltip = new Tooltip();
                    miniTileTooltip.setText(
                            String.format(
                                    "%s\tX%sY%s",
                                    tileName,
                                    x,
                                    y
                            )
                    );

                    Rectangle miniTile = new RectangleBuilder()
                            .setDims(tileGeom.getWidth(), tileGeom.getHeight())
                            .setTranslateX(tileLoc.getX())
                            .setTranslateY(tileLoc.getY())
                            .setFill(TileColorUtils.colorOfTile(tileGeom.getName()))
                            .setOpacity(0.4)
                            .setStroke(Color.BLACK, 2)
                            .addTooltip(miniTileTooltip)
                            .build();

                    miniTile.setOnMouseEntered(event -> miniTile.setOpacity(0.5));
                    miniTile.setOnMouseExited(event -> miniTile.setOpacity(0.4));
                    DiscreteLocation tileCoords = new DiscreteLocation(x, y);

                    miniTile.setOnMouseClicked(event -> {
                        parent.navigateTo(this.fabric.getTile(tileCoords.getX(), tileCoords.getY()));
                        miniTile.requestFocus();
                    });

                    miniTile.setOnKeyPressed(event -> {
                        Date now = new Date();
                        if (now.getTime() - this.lastNavigation.getTime() > 50) {
                            this.lastNavigation = now;
                        } else {
                            return;
                        }

                        int xOffset = 0;
                        int yOffset = 0;

                        switch (event.getCode()) {
                            case UP -> yOffset = -1;
                            case DOWN -> yOffset = 1;
                            case LEFT -> xOffset = -1;
                            case RIGHT -> xOffset = 1;
                            default -> {
                            }
                        }
                        if (xOffset != 0 || yOffset != 0) {
                            int nextX = tileCoords.getX() + xOffset;
                            int nextY = tileCoords.getY() + yOffset;

                            Rectangle nextMiniTile = null;

                            if (nextX >= 0 && nextX < fabricGeometry.getNumberOfColumns()
                                    && nextY >= 0 && nextY < fabricGeometry.getNumberOfRows()) {
                                nextMiniTile = this.miniTiles.get(nextY).get(nextX);
                            }
                            if (nextMiniTile != null) {
                                parent.navigateTo(this.fabric.getTile(nextX, nextY));
                                nextMiniTile.requestFocus();
                            }
                        }
                    });

                    this.getChildren().add(miniTile);
                    currentRow.add(miniTile);
                }
            }

            this.viewPortIndicator = new RectangleBuilder()
                    .setArcDims(2, 2)
                    .setFill(Color.TRANSPARENT)
                    .setStroke(Color.WHITE, 16)
                    .setMouseTransparent(true)
                    .build();
            this.getChildren().add(this.viewPortIndicator);
        }

        public void updateViewPortIndicator(Bounds viewPortBounds) {
            double minX = viewPortBounds.getMinX();
            double minY = viewPortBounds.getMinY();
            double width = viewPortBounds.getWidth();
            double height = viewPortBounds.getHeight();

            this.viewPortIndicator.setTranslateX(minX);
            this.viewPortIndicator.setTranslateY(minY);
            this.viewPortIndicator.setWidth(width);
            this.viewPortIndicator.setHeight(height);

            Bounds fabricBounds = new BoundingBox(
                    0,
                    0,
                    this.fabric.getGeometry().getWidth(),
                    this.fabric.getGeometry().getHeight()
            );

            double maxDiffX = Math.max(
                    Math.max(fabricBounds.getMinX() - viewPortBounds.getMinX(), 0),
                    Math.max(viewPortBounds.getMaxX() - fabricBounds.getMaxX(), 0)
            );
            this.backgroundRect.setWidth(
                    this.fabric.getGeometry().getWidth() + 2 * maxDiffX
            );
            this.backgroundRect.setTranslateX(-maxDiffX);

            double maxDiffY = Math.max(
                    Math.max(fabricBounds.getMinY() - viewPortBounds.getMinY(), 0),
                    Math.max(viewPortBounds.getMaxY() - fabricBounds.getMaxY(), 0)
            );
            this.backgroundRect.setHeight(
                    this.fabric.getGeometry().getHeight() + 2 * maxDiffY
            );
            this.backgroundRect.setTranslateY(-maxDiffY);

            Bounds indicatorBounds = new BoundingBox(minX, minY, width, height);

            if (indicatorBounds.contains(fabricBounds)) {
                this.getChildren().remove(this.viewPortIndicator);
                this.backgroundRect.setTranslateX(0);
                this.backgroundRect.setTranslateY(0);
                this.backgroundRect.setWidth(this.fabric.getGeometry().getWidth());
                this.backgroundRect.setHeight(this.fabric.getGeometry().getHeight());
            } else {
                if (!this.getChildren().contains(this.viewPortIndicator)) {
                    this.getChildren().add(this.viewPortIndicator);
                }
            }
        }
    }

    public WorldView(ContentInfoView parent) {
        this.parent = parent;
        this.getStyleClass().add(StyleClass.WORLD_VIEW.getName());
    }

    private void update() {
        this.getChildren().clear();
        this.setAlignment(Pos.CENTER);

        ScrollPane wrapperPane = new ScrollPane(this.miniFabric);
        wrapperPane.getContent().scaleXProperty().bind(
                Bindings.min(
                        wrapperPane.widthProperty().divide(
                                this.miniFabric.getBackgroundRect().widthProperty().multiply(1.5)
                        ),
                        wrapperPane.heightProperty().divide(
                                this.miniFabric.getBackgroundRect().heightProperty().multiply(1.5)
                        )
                )
        );
        wrapperPane.getContent().scaleYProperty().bind(
                wrapperPane.getContent().scaleXProperty()
        );
        wrapperPane.setHvalue(0.5);
        wrapperPane.setVvalue(0.5);
        wrapperPane.hvalueProperty().addListener(event -> wrapperPane.setHvalue(0.5));
        wrapperPane.vvalueProperty().addListener(event -> wrapperPane.setVvalue(0.5));
        wrapperPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        wrapperPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        this.getChildren().add(wrapperPane);
    }

    public void setNewFabric(Fabric fabric) {
        this.miniFabric = new MiniFabric(fabric);
        this.update();
    }

    public void updateIndicator(Bounds viewPortBounds) {
        this.miniFabric.updateViewPortIndicator(viewPortBounds);
    }

    @Override
    public void dropReferences() {
        this.miniFabric = null;
        this.getChildren().clear();
    }
}
