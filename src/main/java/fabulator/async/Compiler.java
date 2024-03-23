package fabulator.async;

import fabulator.FABulator;
import fabulator.logging.LogManager;
import fabulator.logging.Logger;
import fabulator.util.FileUtils;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Getter
public class Compiler {

    private static Compiler instance;

    private static final String[] YOSYS_CMD_ARGS = new String[]{
            "yosys",
            "-p",
            "synth_fabulous -top %s -json %s"
    };
    private static final String[] NPNR_CMD_ARGS = new String[]{
            "nextpnr-generic",
            "--uarch",
            "fabulous",
            "--json",
            "%s",
            "-o",
            "fasm=%s"
    };

    @Setter
    private String topModuleName;

    @Setter
    private File outputDirectory;

    private BooleanProperty compilingProperty = new SimpleBooleanProperty();
    private ScheduledExecutorService compileService = new ScheduledThreadPoolExecutor(1);
    private Process compileProcess;

    private Logger logger = LogManager.getLogger();

    private Compiler() {
        instance = this;

        this.registerCloseListener();
    }

    private void registerCloseListener() {
        FABulator.getApplication().addClosedListener(this::stopCompilation);
    }

    public void compile(List<File> includeFiles) {
        boolean currentlyCompiling = this.compilingProperty.get();
        assert !currentlyCompiling;
        assert this.outputDirectory != null;
        assert this.topModuleName != null;

        this.prepareCompilation();
        this.doCompilation(includeFiles);
    }

    private void prepareCompilation() {
        this.compilingProperty.set(true);
    }

    public void stopCompilation() {
        this.compileService.shutdown();

        if (this.compileProcess != null) {
            this.compileProcess.destroy();
        }
        this.compilingProperty.set(false);
    }

    private void doCompilation(List<File> includeFiles) {
        Path jsonFilePath = Paths.get(this.outputDirectory.getAbsolutePath(), "out.json");
        Path fasmFilePath = Paths.get(this.outputDirectory.getAbsolutePath(), "out.fasm");
        List<String> includeFileNames = includeFiles.stream()
                .map(File::getAbsolutePath)
                .toList();

        compileToJson(
                includeFileNames,
                jsonFilePath,
                fasmFilePath
        );
    }

    private void compileToJson(
            List<String> includeFileNames,
            Path jsonFilePath,
            Path fasmFilePath) {

        List<String> yosysCmdFormatted = List.of(
                YOSYS_CMD_ARGS[0],
                YOSYS_CMD_ARGS[1],
                String.format(
                        YOSYS_CMD_ARGS[2],
                        this.topModuleName,
                        jsonFilePath
                )
        );

        ProcessBuilder cmdBuilder = buildCommand(
                yosysCmdFormatted,
                includeFileNames
        );

        Runnable compilationCompleted = () -> this.jsonCompilationCompleted(
                jsonFilePath, fasmFilePath
        );
        this.runAndCheck(cmdBuilder, compilationCompleted);
    }

    private void jsonCompilationCompleted(Path jsonFilePath, Path fasmFilePath) {
        if (Files.exists(jsonFilePath)) {
            this.logger.info("Compilation to JSON completed");

            this.compileService.shutdown();
            this.compileProcess.destroy();

            this.compileToFasm(jsonFilePath, fasmFilePath);
        }
    }

    private void compileToFasm(Path jsonFilePath, Path fasmFilePath) {
        List<String> npnrCmdFormatted = List.of(
                NPNR_CMD_ARGS[0],
                NPNR_CMD_ARGS[1],
                NPNR_CMD_ARGS[2],
                NPNR_CMD_ARGS[3],
                String.format(
                        NPNR_CMD_ARGS[4],
                        jsonFilePath
                ),
                NPNR_CMD_ARGS[5],
                String.format(
                        NPNR_CMD_ARGS[6],
                        fasmFilePath
                )
        );

        ProcessBuilder cmdBuilder = buildCommand(
                npnrCmdFormatted, List.of()
        );

        Runnable compilationCompleted = () -> this.fasmCompilationCompleted(
                fasmFilePath
        );
        this.runAndCheck(cmdBuilder, compilationCompleted);
    }

    private void fasmCompilationCompleted(Path fasmFilePath) {
        if (Files.exists(fasmFilePath)) {
            this.logger.info("Compilation to FASM completed");

            this.stopCompilation();

            Platform.runLater(() -> FileUtils.openFasmFile(fasmFilePath.toFile()));
        }
    }

    private ProcessBuilder buildCommand(List<String> formattedCmd, List<String> additionalArgs) {
        List<String> allArgs = Stream.concat(
                formattedCmd.stream(),
                additionalArgs.stream()
        ).toList();

        ProcessBuilder processBuilder = new ProcessBuilder(allArgs);
        processBuilder.inheritIO();

        return processBuilder;
    }

    private void runAndCheck(ProcessBuilder processBuilder, Runnable runnable) {
        try {
            this.compileProcess = processBuilder.start();
            this.compileService.scheduleAtFixedRate(
                    runnable,
                    100,
                    500,
                    TimeUnit.MILLISECONDS
            );

        } catch (IOException e) {
            this.logger.error("Failed to run process: " + e.getMessage());
            this.stopCompilation();
        }
    }

    public static Compiler getInstance() {
        if (instance == null) {
            new Compiler();
        }
        return instance;
    }
}
