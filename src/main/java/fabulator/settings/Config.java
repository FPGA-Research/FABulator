package fabulator.settings;

import fabulator.language.Language;
import fabulator.util.StringUtils;
import javafx.beans.property.*;
import javafx.scene.paint.Color;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.URL;
import java.util.Properties;

@Getter
public class Config {

    private static final String PROPERTIES_COMMENT = "FABulous UI properties";
    private static final String DEFAULT_PROPS = "/default.properties";
    private static final String PROP_FILE_NAME = "/config.properties";
    private static final String PROP_LOCATION = System.getProperty("user.dir") + PROP_FILE_NAME;
    private static Config instance;

    private Properties properties;

    private StringProperty openedFabricFileName;
    private StringProperty openedFasmFileName;
    private StringProperty openedHdlFileName;
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
            boolean success = this.tryCreateProps();
            Logger logger = LogManager.getLogger();

            if (success) {
                logger.info("Created new properties file");
            } else {
                logger.info("Found properties file");
            }

            Properties defaultProperties = this.loadDefaultProperties();
            this.properties = this.loadProperties(defaultProperties);
            this.buildPropertyObjects();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Properties loadDefaultProperties() throws IOException {
        Properties defaultProperties = new Properties();

        URL defaultUrl = getClass().getResource(DEFAULT_PROPS);
        assert defaultUrl != null;

        FileInputStream defaultStream = new FileInputStream(defaultUrl.getFile());
        defaultProperties.load(defaultStream);
        defaultStream.close();

        return defaultProperties;
    }

    private Properties loadProperties(Properties defaultProperties) throws IOException {
        Properties properties = new Properties(defaultProperties);

        FileInputStream propStream = new FileInputStream(PROP_LOCATION);
        properties.load(propStream);
        propStream.close();

        return properties;
    }

    private boolean tryCreateProps() throws IOException {
        File propFile = new File(PROP_LOCATION);
        boolean success = propFile.createNewFile();
        return success;
    }

    private void buildPropertyObjects() {
        this.openedFabricFileName   = this.buildStringSetting("openedFabricFileName");
        this.openedFasmFileName     = this.buildStringSetting("openedFasmFileName");
        this.openedHdlFileName      = this.buildStringSetting("openedHdlFileName");
        this.genWireTooltips        = this.buildBooleanSetting("genWireTooltips");
        this.zoomIntensity          = this.buildDoubleSetting("zoomIntensity");
        this.menuPosition           = this.buildStringSetting("menuPosition");
        this.suggestAutoOpen        = this.buildBooleanSetting("suggestAutoOpen");
        this.autoOpen               = this.buildBooleanSetting("autoOpen");
        this.suggestAutoReload      = this.buildBooleanSetting("suggestAutoReload");
        this.autoReload             = this.buildBooleanSetting("autoReload");
        this.colorCodeTiles         = this.buildBooleanSetting("colorCodeTiles");
        this.regularPortColor       = this.buildColorSetting("regularPortColor");
        this.jumpPortColor          = this.buildColorSetting("jumpPortColor");
        this.belPortColor           = this.buildColorSetting("belPortColor");

        this.language = new SimpleObjectProperty<>(
                Language.valueOf(this.properties.getProperty("language"))
        );
        this.language.addListener((obs, old, now) -> {
            this.properties.setProperty("language", this.language.get().toString());
            this.save();
        });
    }

    private void save() {
        try (FileWriter writer = new FileWriter(PROP_LOCATION)) {
            this.properties.store(writer, PROPERTIES_COMMENT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private StringProperty buildStringSetting(String settingName) {
        StringProperty settingProperty = new SimpleStringProperty(
                this.properties.getProperty(settingName)
        );
        settingProperty.addListener((obs, old, now) -> {
            this.properties.setProperty(settingName, now);
            this.save();
        });
        return settingProperty;
    }

    private BooleanProperty buildBooleanSetting(String settingName) {
        BooleanProperty settingProperty = new SimpleBooleanProperty(
                Boolean.parseBoolean(this.properties.getProperty(settingName))
        );
        settingProperty.addListener((obs, old, now) -> {
            this.properties.setProperty(settingName, String.valueOf(now));
            this.save();
        });
        return settingProperty;
    }

    private DoubleProperty buildDoubleSetting(String settingName) {
        DoubleProperty settingProperty = new SimpleDoubleProperty(
                Double.parseDouble(this.properties.getProperty(settingName))
        );
        settingProperty.addListener((obs, old, now) -> {
            this.properties.setProperty(settingName, String.valueOf(now));
            this.save();
        });
        return settingProperty;
    }

    private ObjectProperty<Color> buildColorSetting(String settingName) {
        ObjectProperty<Color> settingProperty = new SimpleObjectProperty<>(
                Color.web(this.properties.getProperty(settingName))
        );
        settingProperty.addListener((obs, old, now) -> {
            String colorHexString = StringUtils.toHexString(now);
            this.properties.setProperty(settingName, colorHexString);
            this.save();
        });
        return settingProperty;
    }

    public static Config getInstance() {
        if (instance == null) {
            new Config();
        }
        return instance;
    }
}
