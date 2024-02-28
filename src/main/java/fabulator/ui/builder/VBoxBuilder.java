package fabulator.ui.builder;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.util.Builder;

import java.util.Collection;

public class VBoxBuilder implements Builder<VBox> {

    private final VBox vBox;

    public VBoxBuilder() {
        this.vBox = new VBox();
    }

    public VBoxBuilder setAlignment(Pos pos) {
        this.vBox.setAlignment(pos);
        return this;
    }

    public VBoxBuilder addChild(Node child) {
        this.vBox.getChildren().add(child);
        return this;
    }

    public VBoxBuilder addChildren(Collection<? extends Node> children) {
        this.vBox.getChildren().addAll(children);
        return this;
    }

    @Override
    public VBox build() {
        return vBox;
    }
}
