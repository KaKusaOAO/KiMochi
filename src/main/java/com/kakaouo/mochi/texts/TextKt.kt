package com.kakaouo.mochi.texts

object TextKt {
    fun String?.toText(): LiteralText {
        if (this == null) {
            return LiteralText.of("<null>").setColor(com.kakaouo.mochi.texts.TextColor.RED)!!
        }
        return LiteralText.of(this)
    }
}
