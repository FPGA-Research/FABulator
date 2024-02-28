package fabulator.util;

import fabulator.FABulator;
import fabulator.async.FileChangedManager;
import fabulator.geometry.FabricGeometry;
import fabulator.lookup.BitstreamConfiguration;
import fabulator.parse.FasmParser;
import fabulator.parse.GeometryParser;
import fabulator.ui.fabric.Fabric;
import fabulator.ui.window.LoadingWindow;
import javafx.application.Platform;
import javafx.stage.FileChooser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A central util class for actions concerning the handling of a file.
 */
public class FileUtils {

    private static final String GEOM_FILE_EXTENSION = ".csv";
    private static final List<String> HDL_EXTENSIONS = List.of(".v", ".vhd", ".vhdl");

    private static boolean isValidGeomFile(File file) {
        boolean valid = (file != null) && (file.getName().endsWith(GEOM_FILE_EXTENSION));
        return valid;
    }

    private static boolean isValidHdlFile(File file) {
        boolean valid = false;

        if (file != null) {
            String fileName = file.getName();

            for (String extension : HDL_EXTENSIONS) {
                if (fileName.endsWith(extension)) {
                    valid = true;
                    break;
                }
            }
        }
        return valid;
    }

    /**
     * Opens a dialog for choosing a file to open.
     */
    public static void openFile() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(
                FABulator.getApplication().getStage()
        );
        openAsync(file);
    }

    /**
     * Reads a geometry file specified by a given
     * name and parses the contained fabric
     * asynchronously.
     * Therefore, Platform.runLater() is used
     * by this function to synchronize with the UI
     * Thread.
     * This function will start parsing the new
     * fabric after the previously displayed fabric
     * is deleted (or rather, references are dropped)
     * in order to make sure memory can be reused.
     *
     * @param file the name of the file to open
     */
    public static void openAsync(File file) {
        if (isValidGeomFile(file)) {
            Logger logger = LogManager.getLogger();
            logger.info("Asynchronously opening fabric file " + file.getName());

            LoadingWindow.getInstance().show();

            FABulator.getApplication()
                    .getMainView()
                    .dropReferences();

            new Thread(() -> {
                FileChangedManager.getInstance().setFile(file);

                GeometryParser parser = new GeometryParser(file);
                FabricGeometry geometry = parser.getGeometry();
                Fabric fabric = new Fabric(geometry);

                Platform.runLater(() -> {
                    FABulator.getApplication()
                            .getMainView()
                            .setNewFabric(fabric);
                    Platform.runLater(() -> LoadingWindow.getInstance().hide());
                });
            }).start();
        }
    }

    /**
     * Opens a fabric file synchronously.
     *
     * @param file the file to open
     */
    public static void openSync(File file) {
        if (isValidGeomFile(file)) {
            Logger logger = LogManager.getLogger();
            logger.info("Synchronously opening fabric file " + file.getName());

            FABulator.getApplication()
                    .getMainView()
                    .dropReferences();

            FileChangedManager.getInstance().setFile(file);

            GeometryParser parser = new GeometryParser(file);
            FabricGeometry geometry = parser.getGeometry();
            Fabric fabric = new Fabric(geometry);

            Platform.runLater(() -> {
                FABulator.getApplication()
                        .getMainView()
                        .setNewFabric(fabric);
            });
        }
    }

    /**
     * Opens a dialog for choosing an HDL file to open.
     */
    public static void openHdl() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(FABulator.getApplication().getStage());

        if (isValidHdlFile(file)) {
            Logger logger = LogManager.getLogger();
            logger.info("Opening HDL file " + file.getName());

            String fileName = file.getAbsolutePath();
            openHdlFile(fileName);
        }
    }

    public static void openHdlFile(String fileName) {
        Logger logger = LogManager.getLogger();
        logger.info("Opening HDL file " + fileName);

        try (FileReader fileReader = new FileReader(fileName)) {
            BufferedReader reader = new BufferedReader(fileReader);

            List<String> lines = reader.lines().collect(Collectors.toList());
            FABulator.getApplication()
                    .getMainView()
                    .openHdl(lines);

        } catch (IOException exception) {
            logger.error("HDL file " + fileName + " not found.");
        }
    }

    public static void openFasm() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(FABulator.getApplication().getStage());

        if (file != null) {
            FasmParser fasmParser = new FasmParser(file.getAbsolutePath());
            BitstreamConfiguration bitstreamConfiguration = fasmParser.getConfig();
            FABulator.getApplication()
                    .getMainView()
                    .displayBitstreamConfig(bitstreamConfiguration);
        }
    }
}