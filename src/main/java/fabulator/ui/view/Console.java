package fabulator.ui.view;

import javafx.geometry.Insets;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;


public class Console extends TextArea {

    private static final String PROMPT_INDICATOR = ">>> ";
    private static final int CMD_SAVED_AMOUNT = 32;

    private Runtime runtime;
    private int lastValidPos;
    private LinkedList<String> lastCommands;
    private ListIterator<String> commandPointer;

    public Console() {
        this.setPadding(
                new Insets(4)
        );
        this.runtime = Runtime.getRuntime();
        this.lastCommands = new LinkedList<>();
        this.commandPointer = this.lastCommands.listIterator();

        this.setText(PROMPT_INDICATOR);
        this.lastValidPos = PROMPT_INDICATOR.length();
        this.setWrapText(true);
        this.registerListeners();
    }

    private void registerListeners() {
        // TODO: use switch statement
        this.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
            if (event.getCode() == KeyCode.UP) {
                if (this.getCaretPosition() >= this.lastValidPos) this.suggestPrevCommand();
                event.consume();
            }
            if (event.getCode() == KeyCode.DOWN) {
                if (this.getCaretPosition() >= this.lastValidPos) this.suggestNextCommand();
                event.consume();
            }
        });

        this.addEventFilter(KeyEvent.ANY, event -> {
            if (event.getCode() == KeyCode.UP) {
                if (this.getCaretPosition() >= this.lastValidPos) event.consume();
            }
            if (event.getCode() == KeyCode.DOWN) {
                if (this.getCaretPosition() >= this.lastValidPos) event.consume();
            }

            if (event.getCode() == KeyCode.BACK_SPACE) {
                if (this.getCaretPosition() <= this.lastValidPos) event.consume();
            }

            if (event.getCode() == KeyCode.DELETE) {
                if (this.getCaretPosition() < this.lastValidPos) event.consume();
            }

            if (event.getCode() == KeyCode.ENTER) {
                if (this.getCaretPosition() > this.lastValidPos) {    // TODO: should rather be called Invalid
                    String currentCommand = this.getCurrentCommand();
                    try {
                        this.execute(currentCommand);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    this.setText(this.getText() + "\n" + PROMPT_INDICATOR);
                    this.lastValidPos = this.getText().length();
                }
                this.positionCaret(this.getText().length());
                event.consume();
            }
            if (this.getCaretPosition() < this.lastValidPos) {
                event.consume();
                this.positionCaret(this.lastValidPos);
            }
        });

    }

    private String getCurrentCommand() {
        String text = this.getText();
        String line = text.substring(this.lastValidPos);
        return line;
    }

    private void suggestPrevCommand() {
        if (this.commandPointer.hasNext()) {
            String prevCommand = this.commandPointer.next();
            this.replaceCurrentCommand(prevCommand);
        }
    }

    private void suggestNextCommand() {
        if (this.commandPointer.hasPrevious()) {
            String nextCommand = this.commandPointer.previous();
            this.replaceCurrentCommand(nextCommand);
        }
    }

    private void replaceCurrentCommand(String newCommand) {
        String text = this.getText();
        String beforeCommand = text.substring(0, this.lastValidPos);
        this.setText(beforeCommand + newCommand);
        this.positionCaret(this.getText().length());
    }

    private void print(String string) {
        String text = this.getText();
        this.setText(text + "\n" + string);
        this.positionCaret(this.getText().length());
    }

    private void execute(String command) throws IOException {
        if (execSpecialCommands(command)) {
            return;
        }

        this.lastCommands.addFirst(command);
        this.commandPointer = this.lastCommands.listIterator();

        if (this.lastCommands.size() > CMD_SAVED_AMOUNT) {
            this.lastCommands.removeLast();
        }
        String interpreter = "cmd.exe /c ";
        try {
            Process process = this.runtime.exec(interpreter + command);

            InputStreamReader reader = new InputStreamReader(process.getInputStream());
            BufferedReader stdInput = new BufferedReader(reader);

            List<String> lines = new ArrayList<>(stdInput.lines().toList());

            stdInput.close();
            reader.close();

            reader = new InputStreamReader(process.getErrorStream());
            stdInput = new BufferedReader(reader);

            lines.addAll(stdInput.lines().toList());

            stdInput.close();
            reader.close();

            process.destroy();

            this.print(String.join("\n", lines));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean execSpecialCommands(String command) {
        boolean executed = false;

        if (command.equals("clear")) {
            this.clear();

            executed = true;
        }
        return executed;
    }
}
