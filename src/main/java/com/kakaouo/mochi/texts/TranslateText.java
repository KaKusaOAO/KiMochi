package com.kakaouo.mochi.texts;

import com.kakaouo.mochi.utils.terminal.Terminal;
import org.jetbrains.annotations.NotNull;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

import java.util.*;
import java.util.regex.Pattern;

public class TranslateText extends Text<TranslateText> {
    public String translate;
    public List<Text<?>> with = new ArrayList<>();
    private static final Pattern REPLACE_PATTERN = Pattern.compile("%(?:(\\d)\\$)?s");

    public static TranslateText of(String translate) {
        return new TranslateText(translate);
    }

    public static TranslateText of(String translate, Text<?> ...text) {
        return new TranslateText(translate).addWith(text);
    }

    public TranslateText(String translate) {
        this.translate = translate;
    }

    @Override
    protected @NotNull TranslateText resolveThis() {
        return this;
    }

    @Override
    protected @NotNull TranslateText createCopy() {
        return new TranslateText(this.translate).addWith(with.toArray(new Text[0]));
    }

    public TranslateText addWith(Text<?> ...text) {
        with.addAll(Arrays.asList(text));
        return this;
    }

    @Override
    public @NotNull String toAnsi() {
        String extra = super.toAnsi();
        List<Text<?>> converted = new ArrayList<>();
        List<Text<?>> literals = new ArrayList<>();

        int start = 0;
        int counter = 0;
        var matcher = REPLACE_PATTERN.matcher(translate);

        try {
            while (matcher.find()) {
                var startIndex = matcher.start();
                var endIndex = matcher.end();

                // Add the previous text to converted text
                String prev = translate.substring(start, startIndex);
                var tempLiteral = LiteralText.of(prev).setColor(getColor());
                literals.add(tempLiteral);
                converted.add(tempLiteral);
                start = endIndex;

                if (startIndex >= 1 && translate.charAt(startIndex - 1) == '%') {
                    // The entry is considered escaped.
                    // Reduce `start` by one so the format character (the s in %s) in included
                    start -= endIndex - startIndex - 1;
                    continue;
                }

                int index;
                String indexStr = matcher.group(1);
                if (indexStr != null) {
                    index = Integer.parseInt(indexStr) - 1;
                } else {
                    index = counter++;
                }

                if (index >= 0 && index < with.size()) {
                    converted.add(with.get(index));
                } else {
                    converted.add(LiteralText.of(
                            translate.substring(startIndex, endIndex)
                    ));
                }
            }

            var end = LiteralText.of(translate.substring(start))
                .setColor(getColor());
            converted.add(end);
            literals.add(end);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        AttributedStyle style = new AttributedStyle().foregroundRgb(
            Optional.ofNullable(this.getColor()).orElse(this.getParentColor()).getColor().getRGB()
        );

        AttributedStringBuilder builder = new AttributedStringBuilder()
            .style(style);

        for (var text : converted) {
            if (literals.contains(text)) {
                builder.style(style);
            }

            builder.append(text.toAnsi())
                .style(style);
        }
        return builder.append(extra).toAnsi();
    }

    @Override
    public @NotNull String toPlainText() {
        String extra = super.toPlainText();
        Object[] args = with.stream().map(Text::toPlainText).toArray(Object[]::new);
        return String.format(translate, args) + extra;
    }
}
