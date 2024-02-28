package fabulator.lookup;

import fabulator.geometry.DiscreteLocation;
import javafx.util.Pair;
import lombok.Getter;

import java.util.LinkedList;
import java.util.List;

@Getter
public class Net {

    private String name;
    private List<Pair<DiscreteLocation, BitstreamConfiguration.ConnectedPorts>> entries;

    public Net(String name) {
        this.name = name;
        this.entries = new LinkedList<>();
    }

    public void add(DiscreteLocation loc, BitstreamConfiguration.ConnectedPorts ports) {
        Pair<DiscreteLocation, BitstreamConfiguration.ConnectedPorts> pair;
        pair = new Pair<>(loc, ports);
        this.entries.add(pair);
    }

    public boolean isEmpty() {
        return entries.isEmpty();
    }
}
