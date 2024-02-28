package fabulator.ui.style;

import lombok.Getter;

@Getter
public enum StyleClass {
    FABRIC_PANE("fabric-pane"),
    FABRIC_VIEW("fabric-view"),
    CONTENT_INFO_VIEW("content-info-view"),
    SETTINGS_VIEW("settings-view"),
    SETTING_SECTION("setting-section"),
    BOTTOM_MENU("bottom-menu"),
    SEARCH_FEEDBACK("search-feedback"),
    PAGE_MENU("page-menu"),
    WIZARD("wizard"),
    WORLD_VIEW("world-view"),
    ELEMENT_VIEW("element-view"),
    NETLIST_VIEW("netlist-view"),
    TITLE("title");

    private String name;

    StyleClass(String name) {
        this.name = name;
    }
}
