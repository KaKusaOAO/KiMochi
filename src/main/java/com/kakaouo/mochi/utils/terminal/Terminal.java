package com.kakaouo.mochi.utils.terminal;

import com.kakaouo.mochi.texts.Text;
import org.jline.reader.Completer;
import org.jline.reader.Highlighter;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.DefaultParser;
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

    static {
        var builder = TerminalBuilder.builder();
        try {
            terminal = builder.build();
        } catch (IOException e) {
            //
        }
    }

    public static void writeLine(Text<?> text) {
        writeLine(text.toAscii());
    }


    public static void writeLine(String text) {
        try {
            lock.acquire();
            if (reader != null) {
                reader.printAbove(text);
            } else {
                terminal.writer().println(text);
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
                .highlighter(highlighter)
                .completer(completer)
                .terminal(terminal)
                .build();

            var p = Optional.ofNullable(prompt).map(Text::toAscii).orElse(null);
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

