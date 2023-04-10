package com.kakaouo.mochi.utils.terminal;

import com.kakaouo.mochi.texts.LiteralText;
import com.kakaouo.mochi.texts.Text;
import com.kakaouo.mochi.texts.TextColor;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.ParsedCommandNode;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import org.jline.reader.*;
import org.jline.utils.AttributedString;

import java.lang.StringBuilder;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public enum BrigadierTerminal {
    ;

    public static <T> Completer createCompleter(CommandDispatcher<T> dispatcher, T source) {
        return (reader, line, candidates) -> {
            var input = line.line();
            var result = dispatcher.parse(input, source);
            Stream<String> suggestions;
            try {
                suggestions = dispatcher.getCompletionSuggestions(result).get()
                    .getList().stream().map(it -> it.apply(input));
            } catch (InterruptedException | ExecutionException e) {
                suggestions = Stream.of();
            }

            int index = input.lastIndexOf(' ') + 1;
            candidates.addAll(suggestions.map(c -> new Candidate(c.substring(index))).toList());
        };
    }

    private static void writeWithSuggestion(Text<?> text, StringBuilder sb) {
        sb.append(text.toAnsi());
    }

    private static void writeError(String msg, StringBuilder sb) {
        sb.append(LiteralText.of(msg).setColor(TextColor.RED).toAnsi());
    }

    private static void writeUsage(String msg, StringBuilder sb) {
        sb.append(LiteralText.of(msg).setColor(TextColor.DARK_GRAY).toAnsi());
    }

    public static <T> Highlighter createHighlighter(CommandDispatcher<T> dispatcher, T source) {
        return new Highlighter() {
            @Override
            public AttributedString highlight(LineReader lineReader, String buffer){
                var colors = new TextColor[]{
                    TextColor.AQUA, TextColor.YELLOW, TextColor.GREEN, TextColor.PURPLE
                };
                var colorIndex = 0;

                var result = dispatcher.parse(buffer, source);
                var reader = result.getReader();
                var context = result.getContext();
                var sb = new StringBuilder();

                if (reader.canRead()) {
                    if (context.getRange().isEmpty()) {
                        writeWithSuggestion(LiteralText.of(buffer).setColor(TextColor.RED), sb);
                    }
                }

                ParsedCommandNode<T> lastProcessedNode = null;
                var started = false;
                var startFrom = 0;

                /*
                fun writeWithSuggestion(text: com.kakaouo.mochi.texts.Text<*>) {
                    sb.append(text.toAscii())
                }

                fun build(): AttributedString {
                    return AttributedString.fromAnsi(sb.toString())
                }
                 */

                while (context != null) {
                    for (var node : context.getNodes()) {
                        if (node == null) {
                            Terminal.writeLine("node is null??");
                            continue;
                        }

                        var range = node.getRange();
                        try {
                            if (started) sb.append(' ');
                            startFrom = range.getEnd();

                            var useColor = !(node.getNode() instanceof LiteralCommandNode<T>);
                            var text = LiteralText.of(range.get(reader)).setColor(useColor ? colors[colorIndex++] : null);
                            sb.append(text.toAnsi());

                            colorIndex %= colors.length;
                            started = true;
                            lastProcessedNode = node;
                        } catch (Throwable ex) {
                            writeWithSuggestion(LiteralText.of(buffer.substring(range.getStart(), range.getEnd()))
                                .setColor(TextColor.RED), sb);
                            writeError(ex.getMessage(), sb);
                            return AttributedString.fromAnsi(sb.toString());
                        }
                    }

                    var child = context.getChild();
                    if (child == null && reader.canRead()) {
                        var nodes = context.getNodes();
                        var last = nodes.isEmpty() ? null : nodes.get(nodes.size() - 1);
                        var nextNode = last == null ? null : last.getNode().getChildren().stream().findFirst().orElse(null);
                        var usage = (nextNode instanceof ArgumentCommandNode<T, ?>) ? nextNode.getUsageText() : null;

                        writeWithSuggestion(LiteralText.of(reader.getString().substring(startFrom))
                                .setColor(TextColor.RED), sb);
                        if (usage != null) {
                            writeUsage(" :" + usage, sb);
                        }

                        var errMsg = "Incorrect argument";
                        var err = result.getExceptions();
                        if (!err.isEmpty()) {
                            errMsg = err.values().stream().findFirst().get().getMessage();
                        }

                        writeError(" <- " + errMsg, sb);
                        return AttributedString.fromAnsi(sb.toString());
                    }

                    context = child;
                }

                if (lastProcessedNode != null && lastProcessedNode.getNode().getCommand() == null) {
                    sb.append(LiteralText.of(" <- Incomplete command").setColor(TextColor.RED).toAnsi());
                }

                return AttributedString.fromAnsi(sb.toString());
            }

            @Override
            public void setErrorPattern(Pattern errorPattern) {

            }

            @Override
            public void setErrorIndex(int errorIndex) {

            }
        };
    }
}