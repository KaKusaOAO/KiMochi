package com.kakaouo.mochi.texts;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public abstract class Text<T extends Text<T>> {
    public List<Text<?>> extra = new ArrayList<>();
    private Text<?> parent = null;
    private TextColor color = null;
    private boolean bold = false;
    private boolean italic = false;
    private boolean obfuscated = false;
    private boolean underlined = false;
    private boolean strikethrough = false;
    private boolean reset = false;

    @NotNull
    @Contract("_->this")
    public T setBold(boolean flag) {
        bold = flag;
        return this.resolveThis();
    }

    public boolean isBold() {
        return bold;
    }

    @Nullable
    public TextColor getColor() {
        return color;
    }

    public boolean isItalic() {
        return italic;
    }

    public boolean isObfuscated() {
        return obfuscated;
    }

    public boolean isReset() {
        return reset;
    }

    public boolean isStrikethrough() {
        return strikethrough;
    }

    public boolean isUnderlined() {
        return underlined;
    }

    @NotNull
    public List<Text<?>> getExtra() {
        return Collections.unmodifiableList(extra);
    }

    @NotNull
    public Text<?> getParent() {
        return parent;
    }

    @NotNull
    @Contract("null->fail;!null->this")
    public T setExtra(List<Text<?>> extra) {
        if (extra == null) {
           throw new IllegalArgumentException("extra cannot be null.");
        }

        this.extra = extra;
        return this.resolveThis();
    }

    @NotNull
    @Contract("_->this")
    public T setItalic(boolean italic) {
        this.italic = italic;
        return this.resolveThis();
    }

    @NotNull
    @Contract("_->this")
    public T setObfuscated(boolean obfuscated) {
        this.obfuscated = obfuscated;
        return this.resolveThis();
    }

    @NotNull
    @Contract("_->this")
    public T setReset(boolean reset) {
        this.reset = reset;
        return this.resolveThis();
    }

    @NotNull
    @Contract("_->this")
    public T setStrikethrough(boolean strikethrough) {
        this.strikethrough = strikethrough;
        return this.resolveThis();
    }

    @NotNull
    @Contract("_->this")
    public T setUnderlined(boolean underlined) {
        this.underlined = underlined;
        return this.resolveThis();
    }

    @NotNull
    public TextColor getParentColor() {
        return Optional.ofNullable(parent)
            .flatMap(p ->
                Optional.ofNullable(p.color)
                    .or(() ->
                        Optional.of(p.getParentColor())
                    )
            )
            .orElse(TextColor.WHITE);
    }

    @NotNull
    public String toAnsi() {
        var color = this.getParentColor().getColor();
        AttributedStyle style = new AttributedStyle().foregroundRgb(color.getRGB());

        AttributedStringBuilder builder = new AttributedStringBuilder();
        for (Text<?> e : this.extra) {
            builder
                .append(e.toAnsi())
                .style(Optional.ofNullable(this.color)
                    .or(() -> Optional.of(this.getParentColor()))
                    .map(c -> new AttributedStyle().foregroundRgb(c.getColor().getRGB()))
                    .orElse(new AttributedStyle())
            );
        }

        return builder.style(style).toAnsi();
    }

    @NotNull
    public String toPlainText() {
        StringBuilder extra = new StringBuilder();
        for (Text<?> e : this.extra) {
            extra.append(e.toPlainText());
        }
        return extra.toString();
    }

    @NotNull
    @Contract("_,_->new")
    public static <T> Text<?> representClass(Class<T> clz, TextColor color) {
        String name = clz == null ? "?" : clz.getTypeName().substring(clz.getPackageName().length() + 1);
        String pack = clz == null ? "?" : clz.getPackageName();
        return TranslateText.of("%s." + name)
                .setColor(Optional.ofNullable(color).orElse(TextColor.GOLD))
                .addWith(LiteralText.of(pack)
                        .setColor(TextColor.DARK_GRAY));
    }

    @NotNull
    @Contract("_->new")
    public static <T> Text<?> representClass(Class<T> clz) {
        return Text.representClass(clz, null);
    }

    @NotNull
    @Contract("->this")
    protected abstract T resolveThis();

    @NotNull
    @Contract("_->this")
    public T setColor(TextColor color) {
        this.color = color;
        return this.resolveThis();
    }

    @NotNull
    @Contract("null,_->fail;_,null->fail;!null,!null->this")
    public T addExtra(Text<?> text, Text<?>... more) {
        if (text == null) {
            throw new IllegalArgumentException("text cannot be null");
        }

        text.parent = this;
        this.extra.add(text);

        for (var t : more) {
            if (t == null) {
                throw new IllegalArgumentException("One of the given texts is null");
            }

            t.parent = this;
            this.extra.add(t);
        }

        return this.resolveThis();
    }

    @NotNull
    protected abstract T createCopy();

    @NotNull
    @Contract("->new")
    public T copy() {
        T clone = this.createCopy();
        clone.setColor(this.color);
        clone.setExtra(new ArrayList<>(this.extra));
        return clone;
    }
}

