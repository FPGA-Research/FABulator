package fabulator.ui.icon;

import javafx.scene.image.Image;

import java.io.InputStream;

public class ImageLoader {

    private static ImageLoader instance;

    private ImageLoader() {
        instance = this;
    }

    public Image load(String name) {
        InputStream stream = getClass().getResourceAsStream(name);
        assert stream != null;
        Image image = new Image(stream);
        return image;
    }

    public static ImageLoader getInstance() {
        if (instance == null) {
            new ImageLoader();
        }
        return instance;
    }

}
