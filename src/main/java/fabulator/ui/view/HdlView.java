package fabulator.ui.view;

import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Shows HDL code.
 *
 * @deprecated will be replaced by {@link CodeView} soon
 */
@Deprecated
public class HdlView extends CodeArea {

    //TODO: put into external file
    private static final String[] KEYWORDS = new String[]{
            "always",
            "end",
            "ifnone",
            "or",
            "rpmos",
            "tranif1",
            "and",
            "endcase",
            "initial",
            "output",
            "rtran",
            "tri",
            "assign",
            "endmodule",
            "inout",
            "parameter",
            "rtranif0",
            "tri0",
            "begin",
            "endfunction",
            "input",
            "pmos",
            "rtranif1",
            "tri1",
            "buf",
            "endprimitive",
            "integer",
            "posedge",
            "scalared",
            "triand",
            "bufif0",
            "endspecify",
            "join",
            "primitive",
            "small",
            "trior",
            "bufif1",
            "endtable",
            "large",
            "pull0",
            "specify",
            "trireg",
            "case",
            "endtask",
            "macromodule",
            "pull1",
            "specparam",
            "vectored",
            "casex",
            "event",
            "medium",
            "pullup",
            "strong0",
            "wait",
            "casez",
            "for",
            "module",
            "pulldown",
            "strong1",
            "wand",
            "cmos",
            "force",
            "nand",
            "rcmos",
            "supply0",
            "weak0",
            "deassign",
            "forever",
            "negedge",
            "real",
            "supply1",
            "weak1",
            "default",
            "for",
            "nmos",
            "realtime",
            "table",
            "while",
            "defparam",
            "function",
            "nor",
            "reg",
            "task",
            "wire",
            "disable",
            "highz0",
            "not",
            "release",
            "time",
            "wor",
            "edge",
            "highz1",
            "notif0",
            "repeat",
            "tran",
            "xnor",
            "else",
            "if",
            "notif1",
            "rnmos",
            "tranif0",
            "xor"
    };

    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private static final String PAREN_PATTERN = "\\(|\\)";
    private static final String BRACE_PATTERN = "\\{|\\}";
    private static final String BRACKET_PATTERN = "\\[|\\]";
    private static final String SEMICOLON_PATTERN = "\\;";
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    private static final String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/"
            + "|" + "/\\*[^\\v]*" + "|" + "^\\h*\\*([^\\v]*|/)";

    private static final Pattern PATTERN = Pattern.compile(
            "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
                    + "|(?<PAREN>" + PAREN_PATTERN + ")"
                    + "|(?<BRACE>" + BRACE_PATTERN + ")"
                    + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
                    + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
                    + "|(?<STRING>" + STRING_PATTERN + ")"
                    + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
    );

    public HdlView() {
        URL url = getClass().getResource("/style/style-text-editor.css");
        assert url != null;
        String styleSheet = url.toExternalForm();
        this.getStylesheets().add(styleSheet);

        this.setEditable(false);
        this.setBackground(new Background(
                new BackgroundFill(Color.BLACK, null, null)
        ));
        this.setParagraphGraphicFactory(LineNumberFactory.get(this));
    }

    public void openHdl(List<String> hdl) {
        String hdlString = String.join("\n ", hdl);

        this.clear();
        this.appendText(" ");
        this.appendText(hdlString);
        this.setStyleSpans(0, computeHighlighting(this.getText()));
    }

    private StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();

        while (matcher.find()) {
            String styleClass =
                    matcher.group("KEYWORD") != null ? "keyword" :
                            matcher.group("PAREN") != null ? "paren" :
                                    matcher.group("BRACE") != null ? "brace" :
                                            matcher.group("BRACKET") != null ? "bracket" :
                                                    matcher.group("SEMICOLON") != null ? "semicolon" :
                                                            matcher.group("STRING") != null ? "string" :
                                                                    matcher.group("COMMENT") != null ? "comment" :
                                                                            null;
            assert styleClass != null;
            spansBuilder.add(Collections.singleton("code"), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.singleton("code"), text.length() - lastKwEnd);
        return spansBuilder.create();
    }
}
