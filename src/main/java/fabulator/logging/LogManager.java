package fabulator.logging;

import fabulator.FABulator;

public class LogManager {

    private static final Logger DEFAULT_LOGGER = new Logger(FABulator.APP_NAME);

    public static Logger getLogger() {
        return DEFAULT_LOGGER;
    }
}
