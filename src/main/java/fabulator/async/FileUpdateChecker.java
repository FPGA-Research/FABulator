package fabulator.async;

import fabulator.FABulator;
import fabulator.logging.LogManager;
import fabulator.logging.Logger;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * A class asynchronously checking for changes made to a
 * specified file. Can be used by other classes to reload
 * files that have been modified.
 */
public class FileUpdateChecker {

    private static FileUpdateChecker instance;
    private ScheduledExecutorService scheduler;

    public enum ListenerCategory {
        FABRIC,
        FASM
    }

    @Getter
    @Setter
    @AllArgsConstructor
    private static class Listener {
        private File file;
        private ListenerCategory category;
        private FileTime lastModified;
        private Runnable fileChangedRunnable;

        boolean fileHasChanged() {
            boolean fileHasChanged = false;

            Path path = this.file.toPath();
            FileTime fileTime;
            try {
                fileTime = Files.getLastModifiedTime(path);
                if (fileTime.compareTo(this.lastModified) > 0) {
                    this.lastModified = fileTime;
                    fileHasChanged = true;
                }

            } catch (IOException exception) {
                Logger logger = LogManager.getLogger();
                logger.error("Could not get last modified time of " + path);
            }
            return fileHasChanged;
        }

        void runRunnable() {
            this.fileChangedRunnable.run();
        }
    }

    private List<Listener> listeners = new ArrayList<>();

    private FileUpdateChecker() {
        instance = this;
        this.startScheduler();
    }

    private void startScheduler() {
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.scheduler.scheduleAtFixedRate(
                this::checkForChanges,
                1,
                1,
                TimeUnit.SECONDS
        );
        FABulator.getApplication().addClosedListener(this::stopScheduler);
    }

    private void checkForChanges() {
        this.listeners.stream()
                .filter(Listener::fileHasChanged)
                .forEach(Listener::runRunnable);
    }

    private void stopScheduler() {
        if (this.scheduler != null) {
            this.scheduler.shutdown();
        }
    }

    public void registerListener(File file, ListenerCategory category, Runnable runnable) {
        try {
            Path path = file.toPath();
            FileTime fileTime = Files.getLastModifiedTime(path);

            Listener listener = new Listener(file, category, fileTime, runnable);
            this.listeners.add(listener);

        } catch (IOException exception) {
            Logger logger = LogManager.getLogger();
            logger.error("Could not get last modified time of " + file.toPath());
        }
    }

    public void deregisterListeners(ListenerCategory category) {
        this.listeners.removeIf(listener -> listener.getCategory().equals(category));
    }

    public static FileUpdateChecker getInstance() {
        if (instance == null) {
            new FileUpdateChecker();
        }
        return instance;
    }
}
