package fabulator.ui.menu;

import fabulator.FABulator;
import fabulator.ui.builder.MenuBuilder;
import fabulator.ui.builder.MenuItemBuilder;
import fabulator.language.Text;
import fabulator.util.FileUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

public class TopMenu extends MenuBar {

    private Menu fileMenu;
    private Menu editMenu;
    private Menu viewMenu;

    public TopMenu() {
        this.initialize();
        this.setup();
    }

    private void initialize() {
        this.initializeFileMenu();
        this.initializeEditMenu();
        this.initializeViewMenu();
    }

    private void initializeFileMenu() {
        MenuItem fileOpen = new MenuItemBuilder()
                .setText(Text.OPEN)
                .setOnAction(action -> FileUtils.openFile())
                .build();

        MenuItem hdlOpen = new MenuItemBuilder()
                .setText(Text.OPEN_HDL)
                .setOnAction(actionEvent -> FileUtils.openHdl())
                .build();

        MenuItem fasmOpen = new MenuItemBuilder()
                .setText(Text.OPEN_FASM)
                .setOnAction(actionEvent -> FileUtils.openFasm())
                .build();

        this.fileMenu = new MenuBuilder()
                .setText(Text.FILE)
                .addItem(fileOpen)
                .addItem(hdlOpen)
                .addItem(fasmOpen)
                .build();
    }

    private void initializeEditMenu() {
        MenuItem collectGarbage = new MenuItemBuilder()
                .setText(Text.GARBAGE_COLLECT)
                .setOnAction(action -> System.gc())
                .build();

        this.editMenu = new MenuBuilder()
                .setText(Text.EDIT)
                .addItem(collectGarbage)
                .build();
    }

    private void initializeViewMenu() {
        EventHandler<ActionEvent> zoomInAction = action -> {
            FABulator.getApplication()
                    .getMainView()
                    .zoomIn();
        };

        MenuItem zoomIn = new MenuItemBuilder()
                .setText(Text.ZOOM_IN)
                .setOnAction(zoomInAction)
                .build();

        EventHandler<ActionEvent> zoomOutAction = action -> {
            FABulator.getApplication()
                    .getMainView()
                    .zoomOut();
        };

        MenuItem zoomOut = new MenuItemBuilder()
                .setText(Text.ZOOM_OUT)
                .setOnAction(zoomOutAction)
                .build();

        EventHandler<ActionEvent> fullScreenAction = action -> {
            Stage appStage = FABulator.getApplication().getStage();

            boolean fullScreen = appStage.isFullScreen();
            appStage.setFullScreen(!fullScreen);
        };

        MenuItem fullScreen = new MenuItemBuilder()
                .setText(Text.FULL_SCREEN)
                .setOnAction(fullScreenAction)
                .build();

        this.viewMenu = new MenuBuilder()
                .setText(Text.VIEW)
                .addItem(zoomIn)
                .addItem(zoomOut)
                .addItem(fullScreen)
                .build();
    }

    private void setup() {
        this.getMenus().addAll(
                this.fileMenu,
                this.editMenu,
                this.viewMenu
        );
    }
}
