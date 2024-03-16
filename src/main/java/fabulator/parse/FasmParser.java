package fabulator.parse;

import fabulator.object.DiscreteLocation;
import fabulator.lookup.BitstreamConfiguration;
import fabulator.util.StringUtils;
import lombok.Getter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Parses a fasm file. Displaying information from
 * fasm files is an experimental feature.
 */
@Getter
public class FasmParser {

    private String fileName;
    private String currentNetName;
    private BitstreamConfiguration config;

    public FasmParser(String fileName) {
        this.fileName = fileName;
        this.config = new BitstreamConfiguration();

        try {
            this.parse();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private void parse() throws IOException {
        try (FileReader reader = new FileReader(this.fileName)) {
            BufferedReader bufferedReader = new BufferedReader(reader);

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                this.processLine(line);
            }
            bufferedReader.close();
        }
    }


    private void processLine(String line) {
        if (line.startsWith("#")) {
            if (line.contains("net")) {
                String[] split = line.split("'");
                this.currentNetName = split[1];
                this.config.addNet(this.currentNetName);
            }
            return;
        }

        String[] args = line.split("\\.");
        if (args.length != 3 || args[2].startsWith("INIT")) return;

        String locArg = args[0];
        DiscreteLocation tileLoc = StringUtils.discreteLocFrom(locArg);

        this.config.addEntry(this.currentNetName, tileLoc, args[1], args[2]);
    }
}
