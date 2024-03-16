package fabulator.ui.icon;

import javafx.scene.image.Image;
import lombok.Getter;


@Getter
public enum ImageIcon {
    FABULOUS("/img/FABulous.png"),
    CHEVRON("/img/chevron.png");

    private String fileName;
    private Image image;

    ImageIcon(String fileName) {
        this.fileName = fileName;
        this.image = ImageLoader.getInstance().load(fileName);
    }
}