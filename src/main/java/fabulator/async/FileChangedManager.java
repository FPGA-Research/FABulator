package fabulator.async;

import fabulator.settings.Config;
import fabulator.ui.window.AutoRefreshDialog;
import fabulator.util.FileUtils;
import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * A class asynchronously checking for changes made to the
 * geometry file that is being parsed and represented in the gui.
 * The purpose of this class is to check for changes of the
 * geometry file and trigger a notification for the user, with
 * an option to reload the file.
 */
public class FileChangedManager {

    private static FileChangedManager instance;
    private ScheduledExecutorService scheduler;

    private String currentFileName;
    private FileTime lastModified;

    private FileChangedManager() {
        instance = this;
        this.startListener();
    }

    private void startListener() {
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.scheduler.scheduleAtFixedRate(
                this::checkFileChanged,
                2,
                2,
                TimeUnit.SECONDS
        );
    }

    public void stopScheduler() {
        if (this.scheduler != null) {
            this.scheduler.shutdown();
        }
    }

    private void checkFileChanged() {
        if (this.currentFileName == null || this.lastModified == null) return;
        Path path = Paths.get(this.currentFileName);

        FileTime fileTime;
        try {
            fileTime = Files.getLastModifiedTime(path);
            if (fileTime.compareTo(this.lastModified) > 0) {
                this.onNewVersionAvailable();
                this.lastModified = fileTime;
            }

        } catch (IOException exception) {
            Logger logger = LogManager.getLogger();
            logger.error("Could not get last modified time of " + path);
        }
    }

    private void onNewVersionAvailable() {
        Path path = Paths.get(this.currentFileName);
        Config config = Config.getInstance();
        boolean askForAutoOpen = config.getSuggestAutoReload().get();

        if (askForAutoOpen) {
            Platform.runLater(() -> AutoRefreshDialog.getInstance().suggest(path));
        } else {
            boolean autoOpen = config.getAutoReload().get();

            if (autoOpen) {
                Platform.runLater(() -> FileUtils.openFabricAsync(path.toFile()));
            }
        }
    }

    public void setFile(File file) {
        try {
            this.currentFileName = file.getCanonicalPath();
            Config.getInstance().getOpenedFabricFileName().set(this.currentFileName);

            Path path = Paths.get(this.currentFileName);
            this.lastModified = Files.getLastModifiedTime(path);

        } catch (IOException exception) {
            Logger logger = LogManager.getLogger();
            logger.error("Could not get last modified time of " + file.getName());
        }
    }

    public static FileChangedManager getInstance() {
        if (instance == null) {
            new FileChangedManager();
        }
        return instance;
    }
}
