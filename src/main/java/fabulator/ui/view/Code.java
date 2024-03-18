package fabulator.ui.view;

import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Getter
public enum Code {
    DEFAULT("/keywords/default.txt"),
    VERILOG("/keywords/verilog.txt", ".v"),
    VHDL("/keywords/vhdl.txt", ".vhd", ".vhdl"),
    FASM("/keywords/fasm.txt", ".fasm");

    private String[] keywords;
    private String[] fileExtensions;

    Code(String keywordFileName, String... fileExtensions) {
        this.loadKeywords(keywordFileName);
        this.fileExtensions = fileExtensions;
    }

    private void loadKeywords(String keywordFileName) {
        try (InputStream inStream = getClass().getResourceAsStream(keywordFileName)) {
            assert inStream != null;

            String fileContent = new String(
                    inStream.readAllBytes()
            );
            this.keywords = fileContent.split(",");

        } catch (IOException e) {
            Logger logger = LogManager.getLogger();
            logger.fatal("Could not load keyword resources");

            System.exit(1);
        }
    }

    public static Code of(File file) {
        Code codeOfFile = Code.DEFAULT;
        String fileName = file.getName();

        for (Code code : Code.values()) {
            for (String extension : code.fileExtensions) {
                if (fileName.endsWith(extension)) {
                    codeOfFile = code;
                    break;
                }
            }
        }
        return codeOfFile;
    }
}
