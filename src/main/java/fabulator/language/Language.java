package fabulator.language;

import lombok.Getter;

@Getter
public enum Language {
    ENGLISH("English", "/language/english.txt"),
    GERMAN("Deutsch", "/language/german.txt");

    private String displayName;
    private String fileName;

    Language(String displayName, String fileName) {
        this.displayName = displayName;
        this.fileName = fileName;
    }
}
