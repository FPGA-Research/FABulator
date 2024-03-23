package fabulator.ui.icon;


import lombok.Getter;

@Getter
public enum CssIcon {
    CHIP("chip-icon"),
    EDIT_DESIGN("edit-design-icon"),
    SETTINGS("settings-icon"),
    DIRECTORY("directory-icon"),
    FILE("file-icon"),
    COMPILE("compile-icon"),
    STOP("stop-icon"),
    UPLOAD("upload-icon"),
    SETUP("setup-icon"),
    SEARCH("search-icon"),
    EXPAND_ALL("expand-icon"),
    COLLAPSE_ALL("collapse-icon"),
    ERASE("erase-icon"),
    CLEAR_SELECTION("clear-selection-icon");

    private String id;

    CssIcon(String id) {
        this.id = id;
    }
}

