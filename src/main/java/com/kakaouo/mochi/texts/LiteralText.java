package com.kakaouo.mochi.texts;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class LiteralText extends Text<LiteralText> {
    public String text;

    public LiteralText(String text) {
        this.text = text;
    }

    public static LiteralText of(String text) {
        return new LiteralText(text);
    }

    @Override
    protected @NotNull LiteralText resolveThis() {
        return this;
    }

    @Override
    protected @NotNull LiteralText createCopy() {
        return new LiteralText(text);
    }

    @Override
    public @NotNull String toPlainText() {
        return text + super.toPlainText();
    }

    @Override
    public @NotNull String toAscii() {
        var extra = super.toAscii();
        var color = Optional.ofNullable(this.getColor()).orElse(this.getParentColor()).toAsciiCode();
        return color + text + extra;
    }
}
