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
    TITLE("title"),
    EXPLORER_VIEW("explorer-view"),
    EXPLORER_MENU("explorer-menu"),
    EMPTY_HINT_VIEW("empty-hint-view"),
    EDIT_DESIGN_MENU("edit-design-menu"),
    CODE_VIEW("code-view"),
    COMPILER_SETUP_VIEW("compiler-setup-view"),
    BREAD_CRUMB_BAR("bread-crumb-bar");

    private String name;

    StyleClass(String name) {
        this.name = name;
    }
}
