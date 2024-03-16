package fabulator.ui.view;

import fabulator.language.Text;
import fabulator.ui.builder.ButtonBuilder;
import fabulator.ui.icon.CssIcon;
import fabulator.ui.style.StyleClass;
import fabulator.ui.window.EditSetupWindow;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.MalformedInputException;

public class EditDesignView extends VBox {

    private EditDesignMenu topMenu;
    private SplitPane editPane;

    private ExplorerView explorerView;
    private CodeMultiView codeView;
    private Console console;        // TODO

    public EditDesignView() {
        Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        this.topMenu = new EditDesignMenu();
        this.explorerView = new ExplorerView();
        this.codeView = new CodeMultiView();
        this.console = new Console();

        this.explorerView.setOnFileClicked(fileInfoView -> {
            File file = fileInfoView.getFile();
            try {
                this.codeView.openFileOf(fileInfoView);

            } catch (MalformedInputException exception) {
                Logger logger = LogManager.getLogger();
                logger.error("Unreadable format for file " + file);

            } catch (IOException e) {
                Logger logger = LogManager.getLogger();
                logger.error("Cannot open file " + file);
            }
        });
        this.explorerView.setOnFilesChanged(fileInfoView -> {
            this.topMenu.setBreadCrumb(fileInfoView);
        });
        this.codeView.setOnSelectionChanged(fileInfoView -> {
            this.topMenu.setBreadCrumb(fileInfoView);
        });

        this.editPane = new SplitPane();
        this.editPane.getItems().addAll(
                this.explorerView,
                this.codeView
        );

        VBox.setVgrow(this.editPane, Priority.ALWAYS);
        SplitPane.setResizableWithParent(this.explorerView, false);

        this.getChildren().addAll(
                this.topMenu,
                this.editPane//,
                //this.console      // TODO
        );
    }

    public void openFolder(File folder) {
        try {
            this.explorerView.open(folder);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

class EditDesignMenu extends HBox implements View {

    private BreadCrumbBar<FileInfoView> currentFileBar;
    private MenuButton compilerSetupButton;
    private Button compileButton;

    public EditDesignMenu() {
        this.getStyleClass().add(StyleClass.EDIT_DESIGN_MENU.getName());
        this.init();
    }

    @Override
    public void buildParts() {
        this.currentFileBar = new BreadCrumbBar<>();

        this.compilerSetupButton = new MenuButton("Compiler Setup");

        MenuItem editSetup = new MenuItem("Edit Setup");
        editSetup.setOnAction(this::editSetup);

        this.compilerSetupButton.getItems().add(editSetup);

        this.compileButton = new ButtonBuilder()
                .setTooltip(Text.COMPILE)
                .setIcon(CssIcon.COMPILE)
                .build();
    }

    @Override
    public void buildWhole() {
        Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        this.getChildren().addAll(
                this.currentFileBar,
                spacer,
                this.compilerSetupButton,
                this.compileButton
        );
    }

    private void editSetup(ActionEvent event) {
        EditSetupWindow.getInstance().show();
    }

    public void setBreadCrumb(FileInfoView infoView) {
        TreeItem<FileInfoView> item = null;

        if (infoView != null) {
            item = infoView.getWrapper();
        }
        this.currentFileBar.setSelected(item);
    }
}