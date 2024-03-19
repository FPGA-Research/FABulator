package fabulator.logging;

import java.util.ResourceBundle;

public class Logger implements System.Logger {

    private System.Logger systemLogger;

    public Logger(String name) {
        this.systemLogger = System.getLogger(name);
    }

    @Override
    public String getName() {
        return this.systemLogger.getName();
    }

    @Override
    public boolean isLoggable(Level level) {
        return this.systemLogger.isLoggable(level);
    }

    @Override
    public void log(Level level, ResourceBundle bundle, String msg, Throwable thrown) {
        this.systemLogger.log(level, bundle, msg, thrown);
    }

    @Override
    public void log(Level level, ResourceBundle bundle, String format, Object... params) {
        this.systemLogger.log(level, bundle, format, params);
    }

    public void info(String msg) {
        this.systemLogger.log(Level.INFO, msg);
    }

    public void warn(String msg) {
        this.systemLogger.log(Level.WARNING, msg);
    }

    public void error(String msg) {
        this.systemLogger.log(Level.ERROR, msg);
    }
}
