package com.kakaouo.mochi.utils.terminal;

import com.kakaouo.mochi.texts.Text;
import org.jline.reader.*;
import org.jline.reader.impl.DefaultParser;
import org.jline.reader.impl.history.DefaultHistory;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.Semaphore;

public enum Terminal {
    ;

    private static org.jline.terminal.Terminal terminal;
    private static LineReader reader = null;
    private static Semaphore lock = new Semaphore(1);
    private static Semaphore readLock = new Semaphore(1);
    private static History history = new DefaultHistory();

    static {
        var builder = TerminalBuilder.builder();
        try {
            terminal = builder.build();
        } catch (IOException e) {
            //
        }
    }

    public static void writeLine(Text<?> text) {
        writeLine(text.toAnsi());
    }

    public static org.jline.terminal.Terminal getTerminal() {
        return terminal;
    }

    public static void writeLine(String text) {
        try {
            lock.acquire();
            if (reader != null) {
                reader.printAbove(text);
            } else {
                var writer = terminal.writer();
                writer.println(text);

                // Always flushing after writing a new line
                writer.flush();
            }
        } catch (InterruptedException e) {
            //
        } finally {
            lock.release();
        }
    }

    public static String readLine(Text<?> prompt, Completer completer, Highlighter highlighter) {
        try {
            readLock.acquire();
            reader = LineReaderBuilder.builder()
                .parser(new DefaultParser())
                .history(history)
                .highlighter(highlighter)
                .completer(completer)
                .terminal(terminal)
                .build();
            reader.setAutosuggestion(LineReader.SuggestionType.COMPLETER);

            var p = Optional.ofNullable(prompt).map(Text::toAnsi).orElse(null);
            var line = reader.readLine(p);
            reader = null;

            // Should we clean up the reader in some way?
            return line;
        } catch (InterruptedException e) {
            //
            return "";
        } finally {
            readLock.release();
        }
    }
}

