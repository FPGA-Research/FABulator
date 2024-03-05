package fabulator.settings;

import fabulator.FABulator;
import fabulator.language.Language;
import fabulator.language.Text;
import fabulator.ui.builder.*;
import fabulator.ui.style.StyleClass;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SettingsView extends ScrollPane {

    private Label titleLabel;
    private List<SettingSection> sections;

    private Config config;

    public SettingsView() {
        this.getStyleClass().add(StyleClass.SETTINGS_VIEW.getName());
        this.config = Config.getInstance();

        this.initialize();
        this.setup();
    }

    private void initialize() {
        this.setFitToWidth(true);
        this.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        this.prefHeightProperty().bind(
                FABulator.getApplication().getStage().heightProperty()
        );

        this.sections = new ArrayList<>();

        this.buildTitle();
        this.buildPerfSection();
        this.buildControlsSection();
        this.buildColorSection();
        this.buildUiSection();
        this.buildMiscSection();
    }

    private void buildTitle() {
        this.titleLabel = new LabelBuilder()
                .setStyleClass(StyleClass.TITLE)
                .setText(Text.SETTINGS)
                .setFontSize(40)
                .setTextColor(Color.WHITE)
                .build();

        this.titleLabel.maxWidthProperty().bind(
                FABulator.getApplication()
                        .getStage()
                        .widthProperty()
                        .divide(2)
        );
    }

    private void buildPerfSection() {
        SettingSection perfSection = new SettingSection(Text.PERF_SETTINGS);

        // TODO: Move to more appropriate section
        CheckBox genWireTooltips = new CheckBox();
        genWireTooltips.selectedProperty().bindBidirectional(
                this.config.getGenWireTooltips()
        );
        Setting generateWireTooltips = new Setting(Text.GEN_WIRE_TOOLTIPS, genWireTooltips);
        perfSection.addSetting(generateWireTooltips);

        this.sections.add(perfSection);
    }

    private void buildControlsSection() {
        SettingSection controlsSection = new SettingSection(Text.CONTROLS);

        Slider zoomIntensitySlider = new SliderBuilder()
                .bindBidirectional(this.config.getZoomIntensity())
                .setMin(0.001)
                .setMax(0.01)
                .build();

        Setting zoomIntensity = new Setting(Text.ZOOM_INTENSITY, zoomIntensitySlider);
        controlsSection.addSetting(zoomIntensity);

        this.sections.add(controlsSection);
    }

    private void buildColorSection() {
        CheckBox colorCodeTilesBox = new CheckBox();
        colorCodeTilesBox.selectedProperty().bindBidirectional(
                Config.getInstance().getColorCodeTiles()
        );

        ColorPicker regularPortColorPicker = new ColorPickerBuilder()
                .bindBidirectional(this.config.getRegularPortColor())
                .build();

        ColorPicker jumpPortColorPicker = new ColorPickerBuilder()
                .bindBidirectional(this.config.getJumpPortColor())
                .build();

        ColorPicker belPortColorPicker = new ColorPickerBuilder()
                .bindBidirectional(this.config.getBelPortColor())
                .build();

        ColorPicker smConnInColorPicker = new ColorPickerBuilder()
                .bindBidirectional(this.config.getSmConnInColor())
                .build();

        ColorPicker smConnOutColorPicker = new ColorPickerBuilder()
                .bindBidirectional(this.config.getSmConnOutColor())
                .build();

        ColorPicker smConnJumpColorPicker = new ColorPickerBuilder()
                .bindBidirectional(this.config.getSmConnJumpColor())
                .build();

        ColorPicker userDesignColorPicker = new ColorPickerBuilder()
                .bindBidirectional(this.config.getUserDesignColor())
                .build();

        ColorPicker userDesignMarkedColorPicker = new ColorPickerBuilder()
                .bindBidirectional(this.config.getUserDesignMarkedColor())
                .build();

        SettingSection colorSection = new SettingSection(Text.COLOR_SETTINGS);
        colorSection.addSettings(
                new Setting(Text.COLOR_CODE_TILES, colorCodeTilesBox),
                new Setting(Text.REG_PORT_COLOR, regularPortColorPicker),
                new Setting(Text.JUMP_PORT_COLOR, jumpPortColorPicker),
                new Setting(Text.BEL_PORT_COLOR, belPortColorPicker),
                new Setting(Text.SM_CONN_IN_COLOR, smConnInColorPicker),
                new Setting(Text.SM_CONN_OUT_COLOR, smConnOutColorPicker),
                new Setting(Text.SM_CONN_JUMP_COLOR, smConnJumpColorPicker),
                new Setting(Text.USER_DES_COL, userDesignColorPicker),
                new Setting(Text.USER_DES_MARKED, userDesignMarkedColorPicker)
        );
        this.sections.add(colorSection);
    }

    private void buildUiSection() {
        SettingSection uiSection = new SettingSection(Text.UI_SETTINGS);

        ChoiceBox<String> menuPositionBox = new ChoiceBox<>();
        menuPositionBox.getItems().addAll("Left", "Right");         // tricky TODO: display in right lang
        menuPositionBox.valueProperty().bindBidirectional(
                this.config.getMenuPosition()
        );
        Setting menuPosition = new Setting(Text.SIDE_MENU_POS, menuPositionBox);
        uiSection.addSetting(menuPosition);

        ChoiceBox<String> languageBox = new ChoiceBox<>();
        languageBox.getItems().addAll(
                Arrays.stream(Language.values())
                        .map(Language::getDisplayName)
                        .toList()
        );
        languageBox.setValue(this.config.getLanguage().get().getDisplayName());
        languageBox.valueProperty().addListener((obs, old, now) -> {
            Language chosen = Arrays.stream(Language.values())
                    .filter(lang -> lang.getDisplayName().equals(now))
                    .findAny()
                    .get();

            this.config.getLanguage().set(chosen);
        });

        Setting language = new Setting(Text.LANGUAGE, languageBox);
        uiSection.addSetting(language);

        // TODO: Themes
        this.sections.add(uiSection);
    }

    private void buildMiscSection() {
        SettingSection miscSection = new SettingSection(Text.MISC_SETTINGS);

        CheckBox autoOpen = new CheckBoxBuilder()
                .bindBidirectional(this.config.getAutoOpen())
                .build();

        Setting autoOpenSetting = new Setting(Text.AUTO_OPEN, autoOpen);
        autoOpenSetting.visibleProperty().bind(
                this.config.getSuggestAutoOpen().not()
        );

        CheckBox suggestAutoOpen = new CheckBoxBuilder()
                .bindBidirectional(this.config.getSuggestAutoOpen())
                .build();

        Setting suggestAutoOpenSetting = new Setting(Text.SUGGEST_OPEN, suggestAutoOpen);

        CheckBox autoReload = new CheckBoxBuilder()
                .bindBidirectional(this.config.getAutoReload())
                .build();

        Setting autoReloadSetting = new Setting(Text.AUTO_RELOAD, autoReload);
        autoReloadSetting.visibleProperty().bind(
                this.config.getSuggestAutoReload().not()
        );

        CheckBox suggestAutoReload = new CheckBoxBuilder()
                .bindBidirectional(this.config.getSuggestAutoReload())
                .build();

        Setting suggestAutoReloadSetting = new Setting(Text.SUGGEST_RELOAD, suggestAutoReload);

        miscSection.addSettings(
                suggestAutoOpenSetting,
                autoOpenSetting,
                suggestAutoReloadSetting,
                autoReloadSetting
        );
        this.sections.add(miscSection);
    }

    private void setup() {
        VBox contentBox = new VBoxBuilder()
                .setAlignment(Pos.TOP_CENTER)
                .addChild(this.titleLabel)
                .addChildren(this.sections)
                .build();

        this.setContent(contentBox);
    }
}
