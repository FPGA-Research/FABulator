package fabulator.settings;

import fabulator.language.Language;
import fabulator.util.StringUtils;
import javafx.beans.property.*;
import javafx.scene.paint.Color;
import lombok.Getter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

@Getter
public class Config {       // TODO: A lot of boilerplate code, improve
    private static Config instance;

    private Properties properties;

    private StringProperty openedFabricFileName;
    private BooleanProperty genWireTooltips;
    private DoubleProperty zoomIntensity;
    private StringProperty menuPosition;
    private ObjectProperty<Language> language;
    private BooleanProperty suggestAutoOpen;
    private BooleanProperty autoOpen;
    private BooleanProperty suggestAutoReload;
    private BooleanProperty autoReload;
    private BooleanProperty colorCodeTiles;

    private ObjectProperty<Color> regularPortColor;
    private ObjectProperty<Color> jumpPortColor;
    private ObjectProperty<Color> belPortColor;

    private Config() {
        instance = this;

        this.initialize();
    }

    private void initialize() {
        try {
            this.tryCreateProps();

            URL defaultUrl = getClass().getResource("/default.properties");
            assert defaultUrl != null;
            FileInputStream defaultStream = new FileInputStream(defaultUrl.getFile());
            Properties defaultProperties = new Properties();
            defaultProperties.load(defaultStream);
            defaultStream.close();

            String propDir = System.getProperty("user.dir");
            String propLocation = propDir + "/" + "config.properties";
            FileInputStream propStream = new FileInputStream(propLocation);
            this.properties = new Properties(defaultProperties);
            this.properties.load(propStream);
            propStream.close();

            this.loadProps();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void tryCreateProps() throws IOException {
        String propDir = System.getProperty("user.dir");
        String propLocation = propDir + "/" + "config.properties";
        File propFile = new File(propLocation);
        propFile.createNewFile();
    }

    private void loadProps() {
        this.openedFabricFileName = new SimpleStringProperty(
                this.properties.getProperty("openedFabricFileName")
        );
        this.openedFabricFileName.addListener((obs, old, now) -> this.onOpenedFabricFileNameChanged());

        this.genWireTooltips = new SimpleBooleanProperty(
                Boolean.parseBoolean(this.properties.getProperty("genWireTooltips"))
        );
        this.genWireTooltips.addListener((obs, old, now) -> this.onGenWireTooltipsChanged());

        this.zoomIntensity = new SimpleDoubleProperty(
                Double.parseDouble(this.properties.getProperty("zoomIntensity"))
        );
        this.zoomIntensity.addListener((obs, old, now) -> this.onZoomIntensityChanged());

        this.menuPosition = new SimpleStringProperty(
                this.properties.getProperty("menuPosition")
        );
        this.menuPosition.addListener((obs, old, now) -> this.onMenuPositionChanged());

        this.language = new SimpleObjectProperty<>(
                Language.valueOf(this.properties.getProperty("language"))
        );
        this.language.addListener((obs, old, now) -> {
            this.onLanguageChanged();
        });

        this.suggestAutoOpen = new SimpleBooleanProperty(
                Boolean.parseBoolean(this.properties.getProperty("suggestAutoOpen"))
        );
        this.suggestAutoOpen.addListener((obs, old, now) -> this.onSuggestAutoOpenChanged());

        this.autoOpen = new SimpleBooleanProperty(
                Boolean.parseBoolean(this.properties.getProperty("autoOpen"))
        );
        this.autoOpen.addListener((obs, old, now) -> this.onAutoOpenChanged());

        this.suggestAutoReload = new SimpleBooleanProperty(
                Boolean.parseBoolean(this.properties.getProperty("suggestAutoReload"))
        );
        this.suggestAutoReload.addListener((obs, old, now) -> this.onSuggestAutoReloadChanged());

        this.autoReload = new SimpleBooleanProperty(
                Boolean.parseBoolean(this.properties.getProperty("autoReload"))
        );
        this.autoReload.addListener((obs, old, now) -> this.onAutoReloadChanged());

        this.colorCodeTiles = new SimpleBooleanProperty(
                Boolean.parseBoolean(this.properties.getProperty("colorCodeTiles"))
        );
        this.colorCodeTiles.addListener((obs, old, now) -> this.onColorCodeTilesChanged());

        this.regularPortColor = new SimpleObjectProperty<>(
                Color.web(this.properties.getProperty("regularPortColor"))
        );
        this.regularPortColor.addListener((obs, old, now) -> this.onRegularPortColorChanged());

        this.jumpPortColor = new SimpleObjectProperty<>(
                Color.web(this.properties.getProperty("jumpPortColor"))
        );
        this.jumpPortColor.addListener((obs, old, now) -> this.onJumpPortColorChanged());

        this.belPortColor = new SimpleObjectProperty<>(
                Color.web(this.properties.getProperty("belPortColor"))
        );
        this.belPortColor.addListener((obs, old, now) -> this.onBelPortColorChanged());
    }

    private void save() {
        String propDir = System.getProperty("user.dir");
        String propLocation = propDir + "/" + "config.properties";

        try (FileWriter writer = new FileWriter(propLocation)) {
            this.properties.store(writer, "FABulous UI properties");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setOpenedFabricFileName(String fileName) {
        this.openedFabricFileName.set(fileName);
    }

    private void onOpenedFabricFileNameChanged() {
        this.properties.setProperty("openedFabricFileName", this.openedFabricFileName.get());
        this.save();
    }

    private void onGenWireTooltipsChanged() {
        this.properties.setProperty("genWireTooltips", String.valueOf(this.genWireTooltips.get()));
        this.save();
    }

    private void onZoomIntensityChanged() {
        this.properties.setProperty("zoomIntensity", String.valueOf(this.zoomIntensity.get()));
        this.save();
    }

    private void onMenuPositionChanged() {
        this.properties.setProperty("menuPosition", this.menuPosition.get());
        this.save();
    }

    private void onLanguageChanged() {
        this.properties.setProperty("language", this.language.get().toString());
        this.save();
    }

    private void onSuggestAutoOpenChanged() {
        this.properties.setProperty("suggestAutoOpen", String.valueOf(this.suggestAutoOpen.get()));
        this.save();
    }

    private void onAutoOpenChanged() {
        this.properties.setProperty("autoOpen", String.valueOf(this.autoOpen.get()));
        this.save();
    }

    public void setSuggestAutoReload(Boolean suggest) {
        this.suggestAutoReload.set(suggest);
    }

    private void onSuggestAutoReloadChanged() {
        this.properties.setProperty("suggestAutoReload", String.valueOf(this.suggestAutoReload.get()));
        this.save();
    }

    public void setAutoReload(Boolean autoReload) {
        this.autoReload.set(autoReload);
    }

    private void onAutoReloadChanged() {
        this.properties.setProperty("autoReload", this.autoReload.toString());
        this.save();
    }

    private void onColorCodeTilesChanged() {
        this.properties.setProperty("colorCodeTiles", String.valueOf(this.colorCodeTiles.get()));
        this.save();
    }

    private void onRegularPortColorChanged() {
        String colorHexString = StringUtils.toHexString(this.regularPortColor.get());
        this.properties.setProperty("regularPortColor", colorHexString);
        this.save();
    }

    private void onJumpPortColorChanged() {
        String colorHexString = StringUtils.toHexString(this.jumpPortColor.get());
        this.properties.setProperty("jumpPortColor", colorHexString);
        this.save();
    }

    private void onBelPortColorChanged() {
        String colorHexString = StringUtils.toHexString(this.belPortColor.get());
        this.properties.setProperty("belPortColor", colorHexString);
        this.save();
    }

    public void restoreDefaults() throws IOException, RuntimeException {    // TODO: Rework, Implement in Settings Page
        String workingDir = System.getProperty("user.dir");
        String propLocation = workingDir + "/" + "config.properties";
        File propFile = new File(propLocation);

        if (propFile.delete()) {
            this.initialize();
        } else {
            throw new RuntimeException("Could not restore defaults.");
        }
    }

    public static Config getInstance() {
        if (instance == null) {
            new Config();
        }
        return instance;
    }
}
