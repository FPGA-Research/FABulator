package fabulator.util;

import java.io.File;
import java.io.IOException;

public class CompileUtils {

    private static final String COMPILE_TO_JSON = String.join(" ", new String[]{
            "yosys",
            "-p",
            "synth_fabulous",
            "-top top_wrapper",
            "-json %s",
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
    }
}
