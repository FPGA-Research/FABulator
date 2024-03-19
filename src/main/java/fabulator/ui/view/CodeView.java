package fabulator.ui.view;

import fabulator.FABulator;
import fabulator.ui.style.StyleClass;
import fabulator.util.FileUtils;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import lombok.Getter;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

// TODO: make Code of CodeMatcher editable by user
public class CodeView extends CodeArea {

    private static final String NEW_LINE = "\n";

    @Getter
    private BooleanProperty computeHighlightingProperty = new SimpleBooleanProperty();
    private CodeMatcher codeMatcher = new CodeMatcher(Code.DEFAULT);

    private ScheduledExecutorService scheduler;

    public CodeView() {
        this.setEditable(false);
        this.getStyleClass().add(StyleClass.CODE_VIEW.getName());
        this.setParagraphGraphicFactory(
                LineNumberFactory.get(this)
        );

        this.startHighlighter();
    }

    private void startHighlighter() {
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.scheduler.scheduleAtFixedRate(
                this::runHighlighterInBackground,
                50,
                50,
                TimeUnit.MILLISECONDS
        );

        FABulator.getApplication().addClosedListener(this::close);
    }

    public void open(File file) throws IOException {
        this.codeMatcher = new CodeMatcher(Code.of(file));

        List<String> lines = FileUtils.read(file);
        String fileContent = String.join(NEW_LINE, lines);

        this.clear();
        this.getUndoManager().forgetHistory();

        this.appendText(fileContent);
        this.computeSyntaxHighlighting();

        this.moveTo(0);
        this.requestFollowCaret();
    }

    private void computeSyntaxHighlighting() {
        StyleSpans<Collection<String>> style = this.codeMatcher.computeHighlighting(this.getText());

        Platform.runLater(() -> {
            try {
                this.setStyleSpans(0, style);
            } catch (Exception ignored) {

            }
        });
    }

    private void runHighlighterInBackground() {
        if (this.computeHighlightingProperty.get()) {

            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<?> future = executor.submit(this::computeSyntaxHighlighting);

            try {
                future.get(2000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException | ExecutionException ignored) {
            } catch (TimeoutException e) {
                future.cancel(true);
            }
            executor.shutdown();
        }
    }

    public void close() {
        this.scheduler.shutdown();
    }
}
