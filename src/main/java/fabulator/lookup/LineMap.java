package fabulator.lookup;

import fabulator.ui.fabric.port.AbstractPort;
import fabulator.geometry.DiscreteLocation;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.shape.Line;

import java.util.*;

/**
 * A Class for location to wire lookups, implemented using
 * hashing. Supports expected constant lookup time.
 */
public class LineMap {

    private Map<DiscreteLocation, List<Line>> map;

    public LineMap(int initialCapacity) {
        this.map = new HashMap<>(initialCapacity);
    }

    public void add(Line line) {
        Point2D pointStart = line.getParent().localToParent(line.getStartX(), line.getStartY());
        Point2D pointEnd = line.getParent().localToParent(line.getEndX(), line.getEndY());
        DiscreteLocation locStart = new DiscreteLocation(pointStart);
        DiscreteLocation locEnd = new DiscreteLocation(pointEnd);

        this.map.computeIfAbsent(locStart, k -> new LinkedList<>());
        this.map.computeIfAbsent(locEnd, k -> new LinkedList<>());
        this.map.get(locStart).add(line);
        this.map.get(locEnd).add(line);
    }

    public void addAll(Line... lines) {
        for (Line line : lines) {
            this.add(line);
        }
    }

    public List<Line> adjacentTo(Line line) {
        Point2D pointStart = line.getParent().localToParent(line.getStartX(), line.getStartY());
        Point2D pointEnd = line.getParent().localToParent(line.getEndX(), line.getEndY());
        DiscreteLocation locStart = new DiscreteLocation(pointStart);
        DiscreteLocation locEnd = new DiscreteLocation(pointEnd);

        List<Line> adjacent = new LinkedList<>();

        for (Line inMap : this.map.get(locStart)) {
            if (inMap != line) adjacent.add(inMap);
        }
        for (Line inMap : this.map.get(locEnd)) {
            if (inMap != line) adjacent.add(inMap);
        }
        return adjacent;
    }

    public Optional<Line> anyLineAt(AbstractPort port) {
        Parent matrix = port.getParent();
        Parent tile = matrix.getParent();

        DiscreteLocation loc = new DiscreteLocation(
                (int) (port.getTranslateX() + matrix.getTranslateX() + tile.getTranslateX()),
                (int) (port.getTranslateY() + matrix.getTranslateY() + tile.getTranslateY())
        );
        List<Line> atLocation = this.map.get(loc);

        Optional<Line> lineOptional;
        if (atLocation != null && atLocation.size() > 0) {
            lineOptional = Optional.of(atLocation.get(0));
        } else {
            lineOptional = Optional.empty();
        }
        return lineOptional;
    }

    public Set<Line> allLinesAt(AbstractPort port) {
        Optional<Line> lineOptional = this.anyLineAt(port);

        Set<Line> allLines = Set.of();
        if (lineOptional.isPresent()) {
            allLines =  allLinesAt(lineOptional.get());
        }
        return allLines;
    }

    public Set<Line> allLinesAt(Line line) {
        Set<Line> lines = new HashSet<>();
        Queue<Line> searchNear = new LinkedList<>();
        searchNear.add(line);

        while (!searchNear.isEmpty()) {
            List<Line> connected = this.adjacentTo(searchNear.remove());

            for (Line con : connected) {
                if (!lines.contains(con)) {
                    lines.add(con);
                    searchNear.add(con);
                }
            }
        }
        return lines;
    }
}
