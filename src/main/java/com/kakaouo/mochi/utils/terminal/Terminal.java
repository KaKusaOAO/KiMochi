package com.kakaouo.mochi.utils.terminal;

import com.kakaouo.mochi.texts.Text;
import org.jline.reader.Completer;
import org.jline.reader.Highlighter;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.util.Optional;

public enum Terminal {
    ;

    private static org.jline.terminal.Terminal terminal;

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
        // Hope it works?
        terminal.writer().println(text);
    }

    public String readLine(Text<?> prompt, Completer completer, Highlighter highlighter) {
        var reader = LineReaderBuilder.builder()
            .highlighter(highlighter)
            .completer(completer)
            .terminal(terminal)
            .build();

        var p = Optional.ofNullable(prompt).map(Text::toAscii).orElse(null);
        var line = reader.readLine(p);

        // Should we clean up the reader in some way?
        return line;
    }
}

