package fabulator.ui.style;

import lombok.Getter;

@Getter
public enum UiColor {
    GREEN("green-node"),
    DARK_GREEN("dark-green-node"),
    BLUE("blue-node"),
    DARK_BLUE("dark-blue-node"),
    PURPLE("purple-node"),
    DARK_PURPLE("dark-purple-node"),
    PINK("pink-node"),
    DARK_PINK("dark-pink-node"),
    SECONDARY("secondary-node"),
    TRANSPARENT("transparent-node");

    private String colorClass;

    UiColor(String colorClass) {
        this.colorClass = colorClass;
    }
}
