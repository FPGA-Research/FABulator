package fabulator.ui.fabric;

import fabulator.FABulator;
import fabulator.geometry.BelGeometry;
import fabulator.geometry.PortGeometry;
import fabulator.language.Text;
import fabulator.object.Location;
import fabulator.object.Statistics;
import fabulator.object.StatisticsCategory;
import fabulator.object.StatisticsSection;
import fabulator.ui.builder.RectangleBuilder;
import fabulator.ui.fabric.element.ElementType;
import fabulator.ui.fabric.element.FabricElement;
import fabulator.ui.fabric.port.BelPort;
import fabulator.util.FileUtils;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Pair;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * A class representing a single bel of a tile.
 */
@Getter
@Setter
public class Bel extends Group implements FabricElement {

    private Statistics statistics;

    private BelGeometry geometry;
    private Tile tile;

    private Rectangle belRect;

    public Bel(BelGeometry geometry, Tile tile) {
        this.geometry = geometry;
        this.tile = tile;
        this.build();
    }

    private void build() {
        this.setTranslateX(this.geometry.getRelX());
        this.setTranslateY(this.geometry.getRelY());

        this.belRect = new RectangleBuilder()
                .setDims(this.geometry.getWidth(), this.geometry.getHeight())
                .setArcDims(3, 3)
                .setStroke(Color.WHITE, 1)
                .setFill(Color.BLACK)
                .addTooltip(this.geometry.getName())
                .setOnMouseClicked(this::onClicked)
                .build();

        this.getChildren().add(this.belRect);

        for (PortGeometry portGeom : this.geometry.getPortGeometryList()) {
            BelPort belPort = new BelPort(portGeom, this);
            this.getChildren().add(belPort);
        }
    }

    private void onClicked(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            FileUtils.openHdlFile(this.geometry.getSrc());

            FABulator.getApplication()
                    .getMainView()
                    .openStats(this.getStatistics());
        }
    }

    public void filterAndAdd(List<FabricElement> result, ElementType type, Pattern regex) {
        if (type == ElementType.ANY || type == ElementType.PORT) {
            for (Node node : this.getChildren()) {
                if (!(node instanceof BelPort belPort)) continue;

                String portName = belPort.getGeometry().getName();
                Matcher matcher = regex.matcher(portName);

                if (matcher.matches() || portName.contains(regex.pattern())) {
                    result.add(belPort);
                }
            }
        }

        if (type == ElementType.ANY || type == ElementType.BEL) {
            String name = this.geometry.getName();
            Matcher matcher = regex.matcher(name);

            if (matcher.matches() || name.contains(regex.pattern())) {
                result.add(this);
            }
        }
    }

    @Override
    public String getName() {
        return this.geometry.getName();
    }

    @Override
    public ElementType getType() {
        return ElementType.BEL;
    }

    @Override
    public Location getGlobalLocation() {
        Tile tile = this.getTile();

        double globalX = tile.getTranslateX()
                + this.getTranslateX()
                + this.getBelRect().getWidth() / 2;
        double globalY = tile.getTranslateY()
                + this.getTranslateY()
                + this.getBelRect().getHeight() / 2;

        Location globalLocation = new Location(globalX, globalY);
        return globalLocation;
    }

    @Override
    public double getViewableZoom() {
        return 12;
    }

    @Override
    public Statistics getStatistics() {
        if (this.statistics == null) {
            this.buildStatistics();
        }
        return this.statistics;
    }

    private void buildStatistics() {
        this.statistics = Statistics.of(
                StatisticsCategory.of(
                        Text.NAME,
                        this.geometry.getName()
                ),
                StatisticsCategory.of(
                        Text.AMOUNT_PORTS_TOTAL,
                        this.geometry.getNumberPorts(),
                        StatisticsSection.of(
                                Text.AMOUNT_PORTS_B,
                                this.geometry.getNumberPorts(),
                                entriesOf(this.geometry.getPortGeometryList())
                        )
                )
        );
    }

    private List<Pair<String, String>> entriesOf(List<PortGeometry> geoms) {
        AtomicInteger counter = new AtomicInteger(1);

        return geoms.stream()
                .map(portGeom -> new Pair<>(
                        String.valueOf(counter.getAndIncrement()),
                        portGeom.getName()
                ))
                .collect(Collectors.toList());
    }
}
