package fabulator.language;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public enum Text {
    YES,
    NO,
    LEFT,
    RIGHT,

    FILE,
    EDIT,
    VIEW,

    OPEN,
    OPEN_FOLDER,
    OPEN_FILE,
    OPEN_HDL,
    OPEN_FASM,
    GARBAGE_COLLECT,
    ZOOM_IN,
    ZOOM_OUT,
    FULL_SCREEN,

    FABRIC_PAGE,
    ARCH_PAGE,
    SETTINGS_PAGE,

    SETTINGS,
    LOD,
    ZOOM_INTENSITY,
    GEN_WIRE_TOOLTIPS,
    COLOR_CODE_TILES,
    REG_PORT_COLOR,
    JUMP_PORT_COLOR,
    BEL_PORT_COLOR,
    SM_CONN_IN_COLOR,
    SM_CONN_OUT_COLOR,
    SM_CONN_JUMP_COLOR,
    USER_DES_COL,
    USER_DES_MARKED,
    SIDE_MENU_POS,
    LANGUAGE,
    SUGGEST_OPEN,
    AUTO_OPEN,
    SUGGEST_RELOAD,
    AUTO_RELOAD,
    LOADING_FABRIC,
    REFRESH_FILE,
    MODIFICATION_DETECTED,
    RELOAD_QUESTION,
    DONT_ASK_AGAIN,
    RELOAD,
    CANCEL,
    OPEN_QUESTION,

    PERF_SETTINGS,
    CONTROLS,
    COLOR_SETTINGS,
    UI_SETTINGS,
    MISC_SETTINGS,

    ANY,
    PORT,
    BEL,
    SWITCH_MATRIX,
    TILE,

    SEARCH,
    HIDE_EMPTY_NETS,

    WORLD_VIEW,
    ELEMENT_VIEW,
    HDL_VIEW,
    NETLIST_VIEW,

    EMPTY,
    NEXT,
    NO_MORE_OCCURRENCES,
    OF,

    ERROR,
    CLOSE,
    INVALID_GEOM_FILE,
    INVALID_HDL_FILE,

    THICKNESS,

    COMPILE,
    STOP_COMPILATION,
    COMPILER_SETUP,
    APPLY,
    EDIT_COMPILER_SETUP,
    TOP_MODULE_NAME,
    UPLOAD,
    EXPAND_ALL,
    COLLAPSE_ALL,
    EXPLORER_HINT_1,
    EXPLORER_HINT_2,

    ERASE_FASM,
    CLEAR_SELECTION,

    OUTDATED_VERSION,
    OUTDATED_INFO_1,
    OUTDATED_INFO_2,
    OPEN_ANYWAYS,

    STATISTICS,
    NAME,
    AMOUNT_PORTS_TOTAL,
    AMOUNT_PORTS_N,
    AMOUNT_PORTS_S,
    AMOUNT_PORTS_E,
    AMOUNT_PORTS_W,
    AMOUNT_PORTS_J,
    AMOUNT_PORTS_B;

    private StringProperty stringProperty;

    Text() {
        this.stringProperty = new SimpleStringProperty(
                Dictionary.getInstance().stringOf(this)
        );
    }

    public StringProperty stringProperty() {
        return this.stringProperty;
    }
}
