package fabulator.ui.icon;


import lombok.Getter;

@Getter
public enum CssIcon {
    CHIP("chip-icon"),
    EDIT_ARCH("edit-arch-icon"),
    SETTINGS("settings-icon");

    private String id;

    CssIcon(String id) {
        this.id = id;
    }
}

