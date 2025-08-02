package fabulator.ui.icon;


import lombok.Getter;

@Getter
public enum CssIcon {
    CHIP("chip-icon"),
    EDIT_DESIGN("edit-design-icon"),
    SETTINGS("settings-icon"),
    ERASE("erase-icon"),
    CLEAR_SELECTION("clear-selection-icon");

    private String id;

    CssIcon(String id) {
        this.id = id;
    }
}

