package fabulator.ui.builder;

import fabulator.language.Text;
import fabulator.ui.style.StyleClass;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.util.Builder;

public class LabelBuilder implements Builder<Label> {

    private final Label label;

    public LabelBuilder() {
        this.label = new Label();
    }

    public LabelBuilder setStyleClass(StyleClass styleClass) {
        this.label.getStyleClass().add(styleClass.getName());
        return this;
    }

    public LabelBuilder setText(String text) {
        this.label.setText(text);
        return this;
    }

    public LabelBuilder setText(Text text) {
        this.label.textProperty().bind(text.stringProperty());
        return this;
    }

    public LabelBuilder setTranslateX(double translateX) {
        this.label.setTranslateX(translateX);
        return this;
    }

    public LabelBuilder setTranslateY(double translateY) {
        this.label.setTranslateY(translateY);
        return this;
    }

    public LabelBuilder setPrefWidth(double prefWidth) {
        this.label.setPrefWidth(prefWidth);
        return this;
    }

    public LabelBuilder setPrefHeight(double prefHeight) {
        this.label.setPrefHeight(prefHeight);
        return this;
    }

    public LabelBuilder setAlignment(Pos pos) {
        this.label.setAlignment(pos);
        return this;
    }

    public LabelBuilder setTextColor(Paint paint) {
        this.label.setTextFill(paint);
        return this;
    }

    public LabelBuilder setFontSize(double fontSize) {
        Font font = new Font(fontSize);     // TODO: Cache font objects
        this.label.setFont(font);
        return this;
    }

    @Override
    public Label build() {
        return this.label;
    }
}
