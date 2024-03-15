package fabulator.ui.view;

import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// TODO: Add support for python-like comments
public class CodeMatcher {

    private static final String NAME_CAPTURING_GROUP = "(?<%s>%s)";
    private static final String OR = "|";
    private static final String NOT = "^";
    private static final String BOUNDARY = "\\b";
    private static final String QUOTE = "\"";
    private static final String BACKSLASH = "\\\\";
    private static final String NEWLINE = "\n";
    private static final String LINEBREAK = "\\R";
    private static final String VERTICAL_WHITESPACE = "\\v";
    private static final String HORIZONTAL_WHITESPACE = "\\h";

    private Code code;
    private Pattern codePattern;

    private List<String> nameCapturingGroupNames = new ArrayList<>();

    public CodeMatcher(Code code) {
        this.code = code;
        this.buildPatterns();
    }

    private void buildPatterns() {
        String keywordRegex = BOUNDARY
                + "("
                + String.join(OR, this.code.getKeywords())
                + ")"
                + BOUNDARY;

        String parenRegex = "\\(" + OR + "\\)";
        String braceRegex = "\\{" + OR + "\\}";
        String bracketRegex = "\\[" + OR + "\\]";

        String semicolonRegex = ";";

        String stringRegex = QUOTE
                + "("
                + "["
                + NOT
                + BACKSLASH
                + QUOTE
                + "]"
                + OR
                + BACKSLASH
                + ".)*"
                + QUOTE;

        String simpleCommentRegex = "//" + "[" + NOT + NEWLINE + "]*";
        String multilineCommentRegex = "/\\*"
                + "(."
                + OR
                + LINEBREAK
                + ")*?"
                + "\\*/"
                + OR
                + "/\\*"
                + "["
                + NOT
                + VERTICAL_WHITESPACE
                + "]*"
                + OR
                + NOT
                + HORIZONTAL_WHITESPACE
                + "*\\*(["
                + NOT
                + VERTICAL_WHITESPACE
                + "]*|/)";

        this.codePattern = Pattern.compile(
                String.join(
                        OR,
                        new String[]{
                                nameCapturingGroup("keyword", keywordRegex),
                                nameCapturingGroup("paren", parenRegex),
                                nameCapturingGroup("brace", braceRegex),
                                nameCapturingGroup("bracket", bracketRegex),
                                nameCapturingGroup("semicolon", semicolonRegex),
                                nameCapturingGroup("string", stringRegex),
                                nameCapturingGroup("comment", simpleCommentRegex),
                                nameCapturingGroup("multicomment", multilineCommentRegex)
                        }
                )
        );
    }

    public StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = this.codePattern.matcher(text);
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();

        int matcherEndPos = 0;

        while (matcher.find()) {
            String style = null;
            for (String groupName : this.nameCapturingGroupNames) {
                if (matcher.group(groupName) != null) {
                    style = groupName;
                    break;
                }
            }
            assert style != null;
            spansBuilder.add(List.of("code"), matcher.start() - matcherEndPos);
            spansBuilder.add(List.of(style), matcher.end() - matcher.start());

            matcherEndPos = matcher.end();
        }
        spansBuilder.add(List.of("code"), text.length() - matcherEndPos);

        return spansBuilder.create();
    }

    private String nameCapturingGroup(String name, String regex) {
        this.nameCapturingGroupNames.add(name);
        return String.format(NAME_CAPTURING_GROUP, name, regex);
    }
}