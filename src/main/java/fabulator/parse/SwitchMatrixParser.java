package fabulator.parse;

import fabulator.lookup.SmConnectivity;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A CSV parser used for parsing switch matrix csv files,
 * parsing them into an SmConnectivity object.
 *
 * @see SmConnectivity
 */
@Getter
public class SwitchMatrixParser {

    private String fileName;
    private SmConnectivity connection;

    public SwitchMatrixParser(String fileName) {
        this.fileName = fileName;
        this.connection = new SmConnectivity();

        try {
            this.parse();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private void parse() throws IOException {
        try (FileReader reader = new FileReader(this.fileName)) {
            BufferedReader bufferedReader = new BufferedReader(reader);

            String firstLine = bufferedReader.readLine();
            this.processFirstLine(firstLine);

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                this.processLine(line);
            }
            bufferedReader.close();
        } catch (FileNotFoundException e) {
            Logger logger = LogManager.getLogger();
            logger.error("Switch Matrix file " + this.fileName + " not found");
        } catch (NumberFormatException e) {
            Logger logger = LogManager.getLogger();
            logger.error("Switch Matrix file " + this.fileName + " violates expected format");
        }
    }

    private void processFirstLine(String line) {
        String[] tokens = line.split(",");
        this.connection.setTopRowNames(Arrays.asList(
                Arrays.copyOfRange(tokens, 1, tokens.length)
        ));
    }

    private void processLine(String line) {
        if (line.isEmpty()) return;

        String[] tokens = line.split(",");
        String name = tokens[0];
        this.connection.getLeftColNames().add(name);

        List<Boolean> connectionRow = new ArrayList<>();
        for (int i = 0; i < this.connection.getTopRowNames().size(); i++) {
            connectionRow.add(
                    Integer.parseInt(tokens[i + 1]) == 1
            );
        }
        this.connection.getConnected().add(connectionRow);
    }
}
