package com.kakaouo.mochi.texts;

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
    protected LiteralText resolveThis() {
        return this;
    }

    @Override
    protected LiteralText createCopy() {
        return new LiteralText(text);
    }

    @Override
    public String toPlainText() {
        return text + super.toPlainText();
    }

    @Override
    public String toAscii() {
        var extra = super.toAscii();
        var color = Optional.ofNullable(this.color).orElse(this.getParentColor()).toAsciiCode();
        return color + text + extra;
    }
}
