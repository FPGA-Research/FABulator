package fabulator.ui.view;

import fabulator.async.Compiler;
import fabulator.language.Text;
import fabulator.logging.LogManager;
import fabulator.logging.Logger;
import fabulator.ui.builder.ButtonBuilder;
import fabulator.ui.builder.MenuButtonBuilder;
import fabulator.ui.builder.MenuItemBuilder;
import fabulator.ui.icon.CssIcon;
import fabulator.ui.style.StyleClass;
import fabulator.ui.window.CompilerSetupWindow;
import fabulator.util.FileUtils;
import fabulator.util.LayoutUtils;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.IOException;
import java.nio.charset.MalformedInputException;
import java.util.List;

public class EditDesignView extends VBox {

    private EditDesignMenu topMenu;
    private SplitPane editPane;

    private ExplorerView explorerView;
    private CodeMultiView codeView;
    private Compiler compiler = Compiler.getInstance();
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
        this.topMenu.setOnCompile(this::compile);

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

    private void compile() {
        File file = this.codeView.getCurrentFile();
        File outputDir = this.codeView.getCurrentFile().getParentFile();

        List<File> includeFiles = FileUtils.allFilesInDirSatisfying(
                file.getParentFile(),
                FileUtils::isValidHdlFile
        );

        this.compiler.setOutputDirectory(outputDir);
        this.compiler.compile(includeFiles);
    }
}

class EditDesignMenu extends HBox implements View {

    private BreadCrumbBar<FileInfoView> currentFileBar;
    private MenuButton compilerSetupButton;
    private MenuItem editSetupItem;
    private Button compileButton;
    private Button uploadButton;

    private Runnable compileHandler = () -> {};

    public EditDesignMenu() {
        this.getStyleClass().add(StyleClass.EDIT_DESIGN_MENU.getName());
        this.init();
    }

    @Override
    public void buildParts() {
        this.currentFileBar = new BreadCrumbBar<>();

        this.editSetupItem = new MenuItemBuilder()
                .setText(Text.EDIT_COMPILER_SETUP)
                .setOnAction(this::editSetup)
                .build();

        this.compilerSetupButton = new MenuButtonBuilder()
                .setIcon(CssIcon.SETUP)
                .setText(Text.COMPILER_SETUP)
                .addItem(this.editSetupItem)
                .build();

        this.compileButton = new ButtonBuilder()
                .setTooltip(Text.COMPILE)
                .setIcon(CssIcon.COMPILE)
                .bindDisable(Compiler.getInstance().getCompilingProperty())
                .setOnAction(this::compile)
                .build();

        this.uploadButton = new ButtonBuilder()
                .setTooltip(Text.UPLOAD)
                .setIcon(CssIcon.UPLOAD)
                .build();
    }

    @Override
    public void buildWhole() {

        this.getChildren().addAll(
                this.currentFileBar,
                LayoutUtils.hSpacer(),
                this.compilerSetupButton,
                this.compileButton,
                this.uploadButton
        );
    }

    private void editSetup(ActionEvent event) {
        CompilerSetupWindow setupWindow = CompilerSetupWindow.getInstance();
        setupWindow.setOnReady(data -> {
            if (data.isApply()) {
                Compiler.getInstance().setTopModuleName(data.getTopModule());
            }
        });
        setupWindow.show();
    }

    private void compile(ActionEvent event) {
        this.compileHandler.run();
    }

    public void setBreadCrumb(FileInfoView infoView) {
        TreeItem<FileInfoView> item = null;

        if (infoView != null) {
            item = infoView.getWrapper();
        }
        this.currentFileBar.setSelected(item);
    }

    public void setOnCompile(Runnable compileHandler) {
        this.compileHandler = compileHandler;
    }
}