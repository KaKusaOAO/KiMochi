package com.kakaouo.mochi.texts;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class TranslateText extends Text<TranslateText> {
    public String translate;
    public List<Text<?>> with = new ArrayList<>();

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
    public @NotNull String toAscii() {
        String extra = super.toAscii();
        String color = Optional.ofNullable(this.getColor()).orElse(this.getParentColor()).toAsciiCode();
        Object[] args = with.stream().map(text -> text.toAscii() + color).toArray(Object[]::new);
        return color + String.format(translate, args) + extra;
    }

    @Override
    public @NotNull String toPlainText() {
        String extra = super.toPlainText();
        Object[] args = with.stream().map(Text::toPlainText).toArray(Object[]::new);
        return String.format(translate, args) + extra;
    }
}
