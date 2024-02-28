package fabulator.builder;

import fabulator.language.Text;
import fabulator.ui.builder.*;
import fabulator.ui.style.StyleClass;
import fabulator.ui.style.UiColor;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import testutil.TestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BuilderTest {

    @BeforeAll
    public static void startupJavaFX() {
        TestUtils.initializeJavaFX();
    }

    @Test
    @DisplayName("Test ArcBuilder")
    public void testArcBuilder() {
        Arc arc = new ArcBuilder()
                .setCenterX(33)
                .setCenterY(44)
                .setRadius(11, 22)
                .setStartAngle(1.23)
                .setLength(0.345)
                .setFill(Color.TRANSPARENT)
                .setStroke(Color.PINK, 0.1)
                .build();

        assertEquals(33, arc.getCenterX());
        assertEquals(44, arc.getCenterY());
        assertEquals(11, arc.getRadiusX());
        assertEquals(22, arc.getRadiusY());
        assertEquals(1.23, arc.getStartAngle());
        assertEquals(0.345, arc.getLength());
        assertEquals(Color.TRANSPARENT, arc.getFill());
        assertEquals(Color.PINK, arc.getStroke());
        assertEquals(0.1, arc.getStrokeWidth());
    }

    @Test
    @DisplayName("Test ButtonBuilder")
    public void testButtonBuilder() {

        Button button = new ButtonBuilder()
                .setColor(UiColor.BLUE)
                .setTooltip(Text.ZOOM_IN)
                .setText(Text.ZOOM_IN)
                .build();

        assertTrue(button.getStyleClass().contains(UiColor.BLUE.getColorClass()));
        assertEquals(Text.ZOOM_IN.stringProperty().get(), button.getTooltip().textProperty().get());
        assertEquals("Zoom In", button.getText());
    }

    @Test
    @DisplayName("Test LabelBuilder")
    public void testLabelBuilder() {
        Label label = new LabelBuilder()
                .setTextColor(Color.PINK)
                .setText(Text.ZOOM_OUT)
                .setAlignment(Pos.CENTER)
                .setTranslateX(33)
                .setTranslateY(44)
                .setPrefWidth(11)
                .setPrefHeight(22)
                .setStyleClass(StyleClass.TITLE)
                .build();

        assertEquals(Color.PINK, label.getTextFill());
        assertEquals(Text.ZOOM_OUT.stringProperty().get(), label.getText());
        assertEquals(Pos.CENTER, label.getAlignment());
        assertEquals(33, label.getTranslateX());
        assertEquals(44, label.getTranslateY());
        assertEquals(11, label.getPrefWidth());
        assertEquals(22, label.getPrefHeight());
        assertTrue(label.getStyleClass().contains(StyleClass.TITLE.getName()));
    }

    @Test
    @DisplayName("Test LineBuilder")
    public void testLineBuilder() {
        Line line = new LineBuilder()
                .setStart(11, 22)
                .setEnd(33, 44)
                .setStroke(Color.PINK, 1.23)
                .build();

        assertEquals(11, line.getStartX());
        assertEquals(22, line.getStartY());
        assertEquals(33, line.getEndX());
        assertEquals(44, line.getEndY());
        assertEquals(Color.PINK, line.getStroke());
        assertEquals(1.23, line.getStrokeWidth());
    }

    @Test
    @DisplayName("Test MenuBuilder and MenuItemBuilder")
    public void testMenuBuilderAndMenuItemBuilder() {
        MenuItem item1 = new MenuItemBuilder()
                .setText(Text.ZOOM_IN)
                .build();

        MenuItem item2 = new MenuItemBuilder()
                .setText(Text.ZOOM_OUT)
                .build();

        Menu menu = new MenuBuilder()
                .addItem(item1)
                .addItem(item2)
                .setText(Text.VIEW)
                .build();

        assertEquals(Text.ZOOM_IN.stringProperty().get(), item1.getText());
        assertEquals(Text.ZOOM_OUT.stringProperty().get(), item2.getText());
        assertEquals(item1, menu.getItems().get(0));
        assertEquals(item2, menu.getItems().get(1));
        assertEquals(Text.VIEW.stringProperty().get(), menu.getText());
    }

    @Test
    @DisplayName("Test RectangleBuilder")
    public void testRectangleBuilder() {
        Rectangle rectangle = new RectangleBuilder()
                .setDims(22, 44)
                .setFill(Color.PINK)
                .setStroke(Color.LIME, 1.2)
                .setTranslateX(11)
                .setTranslateY(33)
                .setOpacity(0.4)
                .setArcDims(7, 8)
                .build();

        assertEquals(22, rectangle.getWidth());
        assertEquals(44, rectangle.getHeight());
        assertEquals(Color.PINK, rectangle.getFill());
        assertEquals(Color.LIME, rectangle.getStroke());
        assertEquals(1.2, rectangle.getStrokeWidth());
        assertEquals(11, rectangle.getTranslateX());
        assertEquals(33, rectangle.getTranslateY());
        assertEquals(0.4, rectangle.getOpacity());
        assertEquals(7, rectangle.getArcWidth());
        assertEquals(8, rectangle.getArcHeight());
    }

    @Test
    @DisplayName("Test SliderBuilder")
    public void testSliderBuilder() {
        Slider slider = new SliderBuilder()
                .setMin(0.123)
                .setMax(0.456)
                .build();

        assertEquals(0.123, slider.getMin());
        assertEquals(0.456, slider.getMax());
    }
}
