package fabulator.lookup;

import fabulator.object.DiscreteLocation;
import lombok.Getter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


@Getter
public class BitstreamConfiguration {

    @Getter
    public static class ConnectedPorts {
        private String portA;
        private String portB;

        public ConnectedPorts(String portA, String portB) {
            this.portA = portA;
            this.portB = portB;
        }
    }

    HashMap<DiscreteLocation, List<ConnectedPorts>> connectivityMap;
    HashMap<String, Net> netMap;

    public BitstreamConfiguration() {
        this.connectivityMap = new HashMap<>();
        this.netMap = new HashMap<>();
    }

    public void addNet(String netName) {
        this.netMap.computeIfAbsent(netName, k -> new Net(netName));
    }

    public void addEntry(String netName, DiscreteLocation location, String portA, String portB) {
        this.connectivityMap.computeIfAbsent(location, k -> new LinkedList<>());
        ConnectedPorts ports = new ConnectedPorts(portA, portB);

        this.connectivityMap.get(location).add(ports);
        this.netMap.get(netName).add(location, ports);
    }

    public static BitstreamConfiguration empty() {
        return new BitstreamConfiguration();
    }
}
