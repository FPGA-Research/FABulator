package fabulator.language;

import fabulator.settings.Config;
import javafx.beans.value.ObservableValue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Dictionary {

    private static Dictionary instance;
    private Map<String, String> textDict;

    private Dictionary() {
        instance = this;

        Config config = Config.getInstance();
        config.getLanguage().addListener(this::onLangChanged);
        Language language = config.getLanguage().get();

        this.loadOrLogError(language);
    }

    private void onLangChanged(ObservableValue<? extends Language> obs, Language old, Language newLang) {
        this.loadOrLogError(newLang);
        this.updateText();
    }

    private void updateText() {
        for (Text text : Text.values()) {
            String newValue = this.stringOf(text);
            text.stringProperty().set(newValue);
        }
    }

    private void loadOrLogError(Language language) {
        try {
            this.textDict = new HashMap<>();
            this.load(language);

        } catch (IOException exception) {
            Logger logger = LogManager.getLogger();
            logger.fatal("Failed to retrieve language file!");
            System.exit(1);
        }
    }

    private void load(Language language) throws IOException {
        String langFileName = language.getFileName();

        try (InputStream inStream = getClass().getResourceAsStream(langFileName)) {
            assert inStream != null;
            byte[] bytes = inStream.readAllBytes();

            String fileContent = new String(bytes, StandardCharsets.UTF_8);
            String[] lines = fileContent.split("\n");

            for (String line : lines) {
                String[] lineSplit = line.split(":", 2);
                assert lineSplit.length == 2;

                String key = lineSplit[0].trim();
                String text = lineSplit[1].trim();

                this.textDict.put(key, text);
            }
        }
    }

    public String stringOf(Text text) {
        String string = this.textDict.get(text.name());
        return string;
    }

    public static Dictionary getInstance() {
        if (instance == null) {
            new Dictionary();
        }
        return instance;
    }
}
