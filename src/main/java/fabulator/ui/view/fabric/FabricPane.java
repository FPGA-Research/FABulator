package fabulator.ui.view.fabric;

import fabulator.FABulator;
import fabulator.ui.fabric.Fabric;
import fabulator.settings.Config;
import fabulator.ui.style.StyleClass;
import fabulator.geometry.Location;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import lombok.Getter;

public class FabricPane extends ScrollPane {

    @Getter
    private double scaleValue = 0.7;
    private boolean disablePosListeners = false;

    private Node target;
    private Node zoomNode;

    private Config config;
    private Fabric fabric;


    public FabricPane(Node target) {
        this.getStyleClass().add(StyleClass.FABRIC_PANE.getName());
        this.setHvalue(0.5);
        this.setVvalue(0.5);

        this.target = target;
        this.zoomNode = new Group(target);
        this.setContent(outerNode(this.zoomNode));

        this.setPannable(true);
        this.setHbarPolicy(ScrollBarPolicy.NEVER);
        this.setVbarPolicy(ScrollBarPolicy.NEVER);
        this.setFitToHeight(true);
        this.setFitToWidth(true);

        this.updateScale();
        this.registerListeners();

        this.config = Config.getInstance();
    }

    private void registerListeners() {
        this.setOnMouseMoved(event -> this.getScene().setCursor(Cursor.DEFAULT));
        this.hvalueProperty().addListener(listener -> {
            this.updateWorldView();
            if (!this.disablePosListeners) this.updateLod();
        });
        this.vvalueProperty().addListener(listener -> {
            this.updateWorldView();
            if (!this.disablePosListeners) this.updateLod();
        });
        this.widthProperty().addListener(listener -> {
            this.updateWorldView();
            this.updateLod();
        });
        this.heightProperty().addListener(listener -> {
            this.updateWorldView();
            this.updateLod();
        });
    }

    private Node outerNode(Node node) {
        Node outerNode = centeredNode(node);
        outerNode.addEventFilter(ScrollEvent.ANY, event -> {
            if (event.isControlDown()) {
                event.consume();
                onZoom(event.getDeltaY(), new Point2D(event.getX(), event.getY()));
            }
        });
        return outerNode;
    }

    private Node centeredNode(Node node) {
        VBox vBox = new VBox(node);
        vBox.setAlignment(Pos.CENTER);
        return vBox;
    }

    private void updateScale() {
        this.target.setScaleX(this.scaleValue);
        this.target.setScaleY(this.scaleValue);
    }

    public void onZoom(double wheelDelta, Point2D mousePoint) {
        this.disablePosListeners = true;
        double zoomIntensity = this.config.getZoomIntensity().get();
        double zoomFactor = Math.exp(wheelDelta * zoomIntensity);

        Bounds innerBounds = this.zoomNode.getLayoutBounds();
        Bounds viewportBounds = getViewportBounds();

        double valX = this.getHvalue() * (innerBounds.getWidth() - viewportBounds.getWidth());
        double valY = this.getVvalue() * (innerBounds.getHeight() - viewportBounds.getHeight());

        this.scaleValue *= zoomFactor;
        this.updateScale();
        this.layout();

        Point2D posInZoomTarget = target.parentToLocal(zoomNode.parentToLocal(mousePoint));

        Point2D adjustment = target.getLocalToParentTransform().deltaTransform(
                posInZoomTarget.multiply(zoomFactor - 1)
        );

        Bounds updatedInnerBounds = zoomNode.getBoundsInLocal();
        this.setHvalue((valX + adjustment.getX()) / (updatedInnerBounds.getWidth() - viewportBounds.getWidth()));
        this.setVvalue((valY + adjustment.getY()) / (updatedInnerBounds.getHeight() - viewportBounds.getHeight()));

        if (this.fabric != null) {
            this.updateLod();
        }
        this.disablePosListeners = false;
    }

    public void updateLod() {
        if (this.fabric == null) return;
        double rangeX = this.fabric.getBottomRight().getTranslateX() - this.fabric.getTopLeft().getTranslateX();
        double rangeY = this.fabric.getBottomRight().getTranslateY() - this.fabric.getTopLeft().getTranslateY();

        Location viewPortCenter = new Location(
                rangeX * this.getHvalue() - Fabric.MARKER_PADDING,
                rangeY * this.getVvalue() - Fabric.MARKER_PADDING
        );
        boolean xOk = -1024 < viewPortCenter.getX()
                && viewPortCenter.getX() < this.fabric.getGeometry().getWidth() + 1024;
        boolean yOk = -1024 < viewPortCenter.getY()
                && viewPortCenter.getY() < this.fabric.getGeometry().getHeight() + 1024;

        if (xOk && yOk) {
            Bounds bounds = getViewportBounds();
            double viewPortWidth = bounds.getWidth() / this.scaleValue;
            double viewPortHeight = bounds.getHeight() / this.scaleValue;
            double xBuffer = 256;
            double yBuffer = 256;

            Bounds viewPortBounds = new BoundingBox(
                    viewPortCenter.getX() - viewPortWidth / 2 - xBuffer,
                    viewPortCenter.getY() - viewPortHeight / 2 - yBuffer,
                    viewPortWidth + xBuffer,
                    viewPortHeight + yBuffer
            );
            this.fabric.updateLod(this.scaleValue, viewPortBounds);
        }
    }

    public void updateWorldView() {
        if (this.fabric == null) return;
        double rangeX = this.fabric.getBottomRight().getTranslateX() - this.fabric.getTopLeft().getTranslateX();
        double rangeY = this.fabric.getBottomRight().getTranslateY() - this.fabric.getTopLeft().getTranslateY();

        Location viewPortCenter = new Location(
                rangeX * this.getHvalue() - Fabric.MARKER_PADDING,
                rangeY * this.getVvalue() - Fabric.MARKER_PADDING
        );
        Bounds bounds = getViewportBounds();
        double viewPortWidth = bounds.getWidth() / this.scaleValue;
        double viewPortHeight = bounds.getHeight() / this.scaleValue;

        Bounds viewPortBounds = new BoundingBox(
                viewPortCenter.getX() - viewPortWidth / 2,
                viewPortCenter.getY() - viewPortHeight / 2,
                viewPortWidth,
                viewPortHeight
        );

        FABulator.getApplication()
                .getMainView()
                .updateWorldView(viewPortBounds);
    }

    public void applyScale() {
        this.updateScale();
        this.layout();
    }

    public void navigateToGlobal(double globalX, double globalY, double zoom) {
        double rangeX = this.fabric.getBottomRight().getTranslateX() - this.fabric.getTopLeft().getTranslateX();
        double rangeY = this.fabric.getBottomRight().getTranslateY() - this.fabric.getTopLeft().getTranslateY();

        double differenceX = globalX - this.fabric.getTopLeft().getTranslateX();
        double differenceY = globalY - this.fabric.getTopLeft().getTranslateY();

        double relX = differenceX / rangeX;
        double relY = differenceY / rangeY;

        this.focusOn(relX, relY, zoom);
    }

    public void focusOn(double relX, double relY, double focus) {
        this.setHvalue(relX);
        this.setVvalue(relY);
        this.focus(focus);
    }

    private void focus(double newScale) {
        double initialScale = this.scaleValue;
        double difference = newScale - initialScale;
        double step = difference / 15;

        final double hValue = this.getHvalue();
        final double vValue = this.getVvalue();

        Timeline lodTimeline = new Timeline(
                new KeyFrame(Duration.millis(20), runnnable -> {
                    this.updateLod();
                })
        );
        lodTimeline.setDelay(Duration.millis(20));
        lodTimeline.setCycleCount(1);

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(20), runnable -> {
                    this.scaleValue += step;
                    this.applyScale();
                    this.setHvalue(hValue);
                    this.setVvalue(vValue);

                    this.updateLod();
                })
        );
        timeline.setCycleCount(15);
        timeline.setOnFinished(event -> {
            lodTimeline.play();
        });
        timeline.play();
    }

    public void zoomIn() {
        double initialScale = this.scaleValue;
        this.focus(initialScale * 4);
    }

    public void zoomOut() {
        double initialScale = this.scaleValue;
        this.focus(initialScale / 4);
    }

    public void setFabric(Fabric fabric) {
        this.fabric = fabric;
        this.updateLod();
    }
}
