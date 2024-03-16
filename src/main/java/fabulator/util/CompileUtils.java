package fabulator.util;

import javafx.application.Platform;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CompileUtils {

    private static final String COMPILE_TO_JSON = String.join(" ", new String[]{
            "yosys",
            "-p",
            "\"synth_fabulous",
            "-top top_wrapper",
            "-json %s\"",
            "%s",
            "%s"
    });

    private static final String COMPILE_TO_FASM = String.join(" ", new String[]{
            "nextpnr-generic",
            "--uarch fabulous",
            "--json %s",
            "-o fasm=%s"
    });

    public static void compile(File file) throws IOException {
        // TODO
        // THIS IS WORK IN PROGRESS

        File parentDir = file.getParentFile();
        String fileName  = file.getName().replaceFirst("[.][^.]+$", "");
        String jsonName = fileName + ".json";
        String topWrapperName = "top_wrapper.v";

        Path jsonFilePath = Paths.get(parentDir.getAbsolutePath(), jsonName);
        Path topWrapperPath = Paths.get(parentDir.getAbsolutePath(), topWrapperName);

        String compileToJsonCmd = String.format(
                COMPILE_TO_JSON,
                jsonFilePath,
                file.toPath(),
                topWrapperPath
        );

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

        ProcessBuilder processBuilder = new ProcessBuilder(compileToJsonCmd);
        Process process = processBuilder.start();

        System.out.println(compileToJsonCmd);
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

        String fileName  = jsonFilePath.toFile()
                .getName()
                .replaceFirst("[.][^.]+$", "");

        File parentDir = jsonFilePath.toFile().getParentFile();
        String fasmFileName = fileName + ".fasm";
        Path fasmFilePath = Paths.get(parentDir.getAbsolutePath(), fasmFileName);

        String compileToFasmCmd = String.format(
                COMPILE_TO_FASM,
                jsonFilePath,
                fasmFilePath
        );

        ScheduledExecutorService service = new ScheduledThreadPoolExecutor(1);
        service.scheduleAtFixedRate(
                () -> checkForFasm(service, fasmFilePath),
                100,
                100,
                TimeUnit.MILLISECONDS
        );

        System.out.println(compileToFasmCmd);
        ProcessBuilder processBuilder = new ProcessBuilder(compileToFasmCmd);
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
