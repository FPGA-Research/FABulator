package fabulator.lookup;

import fabulator.logging.LogManager;
import fabulator.logging.Logger;
import fabulator.ui.fabric.port.AbstractPort;
import fabulator.ui.fabric.port.JumpPort;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A class managing connections within a single switch matrix.
 * Can be used to look up connected ports.
 */
@Getter
@Setter
public class SmConnectivity {

    private List<String> topRowNames;
    private List<String> leftColNames;
    private List<List<Boolean>> connected;

    public SmConnectivity() {
        this.topRowNames = new ArrayList<>();
        this.leftColNames = new ArrayList<>();
        this.connected = new ArrayList<>();
    }

    /**
     * Returns a Map of Strings and Booleans, where the Strings are the
     * connected ports, and the Booleans specify whether the connected port
     * has been found by a column lookup (true) or a row lookup (false).
     * These Boolean flags are used to color connections differently.
     * Depending on whether the given port is a jump port or not, a lookup
     * for both the source and destination name or just the name will need
     * to be performed, as both are merged in jump ports, but not regular
     * ones.
     *
     * @param port the port for which connections are to be looked up
     * @return names of connected ports and their lookup flag
     */
    public Map<String, Boolean> connectedNamesOf(AbstractPort port) {   // TODO: Use IO to lookup just in row or column
        Map<String, Boolean> connected = new HashMap<>();

        if (port instanceof JumpPort) {
            String sourceName = port.getGeometry().getSourceName();
            String destName = port.getGeometry().getDestName();
            for (String hit : this.searchUsingColAsKeys(sourceName)) {
                connected.put(hit, true);
            }
            for (String hit : this.searchUsingRowAsKeys(sourceName)) {
                connected.put(hit, false);
            }
            for (String hit : this.searchUsingColAsKeys(destName)) {
                connected.put(hit, true);
            }
            for (String hit : this.searchUsingRowAsKeys(destName)) {
                connected.put(hit, false);
            }

        } else {
            String portName = port.getName();
            for (String hit : this.searchUsingColAsKeys(portName)) {
                connected.put(hit, true);
            }
            for (String hit : this.searchUsingRowAsKeys(portName)) {
                connected.put(hit, false);
            }
        }
        return connected;
    }

    /**
     * Searches the matrix for connected ports, uses the column
     * names as keys for the lookup, e.g. if we look for ports
     * connected to port p, we look for a column named p, and
     * look up connected ports there.
     *
     * @param name the name of a port of the switch matrix
     * @return names of connected ports
     */
    private List<String> searchUsingColAsKeys(String name) {
        int nameIndex = this.leftColNames.indexOf(name);

        if (nameIndex == -1) {
            Logger logger = LogManager.getLogger();
            logger.info("Key " + name + " not found");

            return List.of();
        }

        List<Boolean> row = this.connected.get(nameIndex);
        List<Integer> nameIndices = new ArrayList<>();

        for (int x = 0; x < row.size(); x++) {
            if (row.get(x)) {
                nameIndices.add(x);
            }
        }

        List<String> names = nameIndices.stream()
                .map(index -> this.topRowNames.get(index))
                .collect(Collectors.toList());
        return names;
    }

    /**
     * Searches the matrix for connected ports, uses the row
     * names as keys for the lookup, e.g. if we look for ports
     * connected to port p, we look for a row named p, and
     * look up connected ports there.
     *
     * @param name the name of a port of the switch matrix
     * @return names of connected ports
     */
    private List<String> searchUsingRowAsKeys(String name) {
        int nameIndex = this.topRowNames.indexOf(name);

        if (nameIndex == -1) {
            Logger logger = LogManager.getLogger();
            logger.info("Key " + name + " not found");

            return List.of();
        }

        List<Integer> nameIndices = new ArrayList<>();

        for (int y = 0; y < this.connected.size(); y++) {
            if (this.connected.get(y).get(nameIndex)) {
                nameIndices.add(y);
            }
        }

        List<String> names = nameIndices.stream()
                .map(index -> this.leftColNames.get(index))
                .collect(Collectors.toList());
        return names;
    }
}
