package fabulator.ui.style;

import lombok.Getter;

import java.net.URL;

@Getter
public enum Style {
    DARK("/style/style-dark.css", "/icon/icon-dark.css", "/style/colors-dark.css");

    private String styleSheet;
    private String iconSheet;
    private String colorSheet;

    Style(String styleFile, String iconFile, String colorFile) {
        URL url = getClass().getResource(styleFile);
        assert url != null;
        this.styleSheet = url.toExternalForm();

        url = getClass().getResource(iconFile);
        assert url != null;
        this.iconSheet = url.toExternalForm();

        url = getClass().getResource(colorFile);
        assert url != null;
        this.colorSheet = url.toExternalForm();
    }
}
