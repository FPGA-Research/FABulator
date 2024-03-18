package fabulator.ui.menu;

import fabulator.object.Location;
import fabulator.lookup.BitstreamConfiguration;
import fabulator.lookup.Net;
import fabulator.memory.ReferenceHolder;
import fabulator.settings.Config;
import fabulator.ui.fabric.Fabric;
import fabulator.ui.fabric.element.ElementType;
import fabulator.ui.fabric.element.FabricElement;
import fabulator.ui.view.ContentInfoView;
import fabulator.ui.view.fabric.FabricView;
import fabulator.ui.window.AutoOpenDialog;
import fabulator.util.FileUtils;
import fabulator.util.StringUtils;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.scene.control.SplitPane;
import lombok.Getter;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Pattern;

@Getter
public class FabricMenu extends SplitPane implements ReferenceHolder {

    private FabricView fabricView;
    private ContentInfoView infoView;

    public FabricMenu() {
        this.initialize();
        this.setup();
    }

    private void initialize() {
        this.fabricView = new FabricView();
        this.infoView = new ContentInfoView(this);
    }

    private void setup() {
        String menuPosition = Config.getInstance().getMenuPosition().get();

        if (menuPosition.equals("Right")) {
            this.getItems().addAll(
                    this.fabricView,
                    this.infoView
            );

        } else {
            this.getItems().addAll(
                    this.infoView,
                    this.fabricView
            );
        }
        SplitPane.setResizableWithParent(this.infoView, false);

        Platform.runLater(this::maybeOpenLastFabric);
    }

    private void maybeOpenLastFabric() {
        Config config = Config.getInstance();
        boolean suggestAutoOpen = config.getSuggestAutoOpen().get();
        String lastFile = Config.getInstance().getOpenedFabricFileName().get();

        if (!StringUtils.valid(lastFile)) return;

        Path path;
        try {
            path = Paths.get(lastFile);
        } catch (InvalidPathException exception) {
            Logger logger = LogManager.getLogger();
            logger.log(Level.ERROR, "Cannot open last fabric due to invalid path", exception);
            return;
        }

        if (suggestAutoOpen) {
            AutoOpenDialog.getInstance().suggest(path);

        } else {
            boolean autoOpen = config.getAutoOpen().get();
            if (autoOpen) {
                FileUtils.openFabricAsync(path.toFile());
            }
        }
    }

    @Override
    public void dropReferences() {
        this.fabricView.dropReferences();
        this.infoView.dropReferences();
    }

    public void setNewFabric(Fabric fabric) {
        this.infoView.setNewFabric(fabric);
        this.fabricView.setNewFabric(fabric);
    }

    public void displayBitstreamConfig(BitstreamConfiguration config) {
        this.fabricView.displayBitstreamConfig(config);
        this.infoView.displayBitstreamConfig(config);
    }

    public void displayNets(List<Net> nets) {
        this.fabricView.clearNets();

        Location averageLocation = new Location();

        for (Net net : nets) {
            Location netLocation = this.fabricView.displayNet(net);
            averageLocation.add(netLocation);
        }
        averageLocation.scaleInverse(nets.size());

        this.fabricView.navigateToGlobal(averageLocation, 1.25);
    }

    public void openHdl(List<String> hdl) {
        this.infoView.openHdl(hdl);
    }

    public List<FabricElement> searchView(ElementType type, Pattern regex) {
        return this.fabricView.search(type, regex);
    }

    public void zoomIn() {
        this.fabricView.zoomIn();
    }

    public void zoomOut() {
        this.fabricView.zoomOut();
    }

    public void updateWorldView(Bounds viewPortBounds) {
        this.infoView.updateWorldView(viewPortBounds);
    }

    public void navigateTo(FabricElement element) {
        this.fabricView.navigateTo(element);
    }
}
