package com.kakaouo.mochi.texts;

import com.kakaouo.mochi.utils.RGBColor;
import com.kakaouo.mochi.utils.RGBColor;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

public class RgbAsciiColor implements IAsciiColor {
    private RGBColor color;

    public RgbAsciiColor(RGBColor color) {
        this.color = color;
    }

    public RGBColor getColor() {
        return color;
    }

    @Override
    public String toAsciiCode() {
        return "\u001b[38;2;" + color.red + ";" + color.green + ";" + color.blue + "m";
    }
}
