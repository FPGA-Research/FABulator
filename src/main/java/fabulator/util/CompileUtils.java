package fabulator.util;

import fabulator.FABulator;
import javafx.application.Platform;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CompileUtils {

    public static void compile(File file, String topModuleName, List<File> includeFiles) throws IOException {
        // TODO
        // THIS IS WORK IN PROGRESS

        File parentDir = file.getParentFile();
        String jsonName = "out.json";
        Path jsonFilePath = Paths.get(parentDir.getAbsolutePath(), jsonName);

        String[] compileToJsonCmd = new String[includeFiles.size() + 3];
        compileToJsonCmd[0] = "yosys";
        compileToJsonCmd[1] = "-p";
        compileToJsonCmd[2] = String.format("synth_fabulous -top %s -json %s", topModuleName, jsonFilePath);

        for (int i = 0; i < includeFiles.size(); i++) {
            File includeFile = includeFiles.get(i);
            compileToJsonCmd[i + 3] = includeFile.getAbsolutePath();
        }

        ScheduledExecutorService service = new ScheduledThreadPoolExecutor(1);
        service.scheduleAtFixedRate(
                () -> {
                    try {
                        checkForJson(service, jsonFilePath);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                },
                100,
                100,
                TimeUnit.MILLISECONDS
        );
        FABulator.getApplication().addClosedListener(service::shutdown);

        ProcessBuilder processBuilder = new ProcessBuilder(compileToJsonCmd);
        processBuilder.inheritIO();
        processBuilder.command().forEach(System.out::println);
        processBuilder.start();
    }

    private static void compileToJson() {

    }

    // TODO: needs timeout
    private static void checkForJson(ExecutorService service, Path jsonFilePath) throws IOException {
        if (Files.exists(jsonFilePath)) {
            System.out.println("json compiled");
            service.shutdown();
            compileToFasm(jsonFilePath);
        }
    }

    private static void compileToFasm(Path jsonFilePath) throws IOException {
        System.out.println("Compiling to fasm");

        File parentDir = jsonFilePath.toFile().getParentFile();
        String fasmFileName = "out.fasm";
        Path fasmFilePath = Paths.get(parentDir.getAbsolutePath(), fasmFileName);

        String[] compileToFasmCmd = new String[7];
        compileToFasmCmd[0] = "nextpnr-generic";
        compileToFasmCmd[1] = "--uarch";
        compileToFasmCmd[2] = "fabulous";
        compileToFasmCmd[3] = "--json";
        compileToFasmCmd[4] = jsonFilePath.toString();
        compileToFasmCmd[5] = "-o";
        compileToFasmCmd[6] = String.format("fasm=%s", fasmFilePath);

        ScheduledExecutorService service = new ScheduledThreadPoolExecutor(1);
        service.scheduleAtFixedRate(
                () -> checkForFasm(service, fasmFilePath),
                100,
                100,
                TimeUnit.MILLISECONDS
        );
        FABulator.getApplication().addClosedListener(service::shutdown);

        ProcessBuilder processBuilder = new ProcessBuilder(compileToFasmCmd);
        processBuilder.inheritIO();
        processBuilder.command().forEach(System.out::println);
        processBuilder.start();
    }

    // TODO: needs timeout
    private static void checkForFasm(ExecutorService service, Path fasmFilePath) {
        if (Files.exists(fasmFilePath)) {
            System.out.println("fasm compiled");
            service.shutdown();
            System.out.println("opening fasm " + fasmFilePath);

            Platform.runLater(() -> FileUtils.openFasmFile(fasmFilePath.toFile()));
        }
    }
}
