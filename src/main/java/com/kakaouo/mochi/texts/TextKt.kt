package com.kakaouo.mochi.texts

@Suppress("unused")
object TextKt {
    fun String?.toText(): LiteralText {
        if (this == null) {
            return LiteralText.of("<null>").setColor(TextColor.RED)
        }
        return LiteralText.of(this)
    }
}
