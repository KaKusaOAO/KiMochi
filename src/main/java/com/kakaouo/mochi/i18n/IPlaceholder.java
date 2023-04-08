package com.kakaouo.mochi.i18n;

import java.util.Map;

@FunctionalInterface
public interface IPlaceholder {
    void writePlaceholders(Map<String, Object> placeholders);
}
