package com.kakaouo.mochi.i18n;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.kakaouo.mochi.texts.LiteralText;
import com.kakaouo.mochi.texts.TextColor;
import com.kakaouo.mochi.texts.TranslateText;
import com.kakaouo.mochi.utils.Logger;
import com.kakaouo.mochi.utils.Utils;
import org.stringtemplate.v4.ST;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class I18n {
    private static final String BASE_NAME = "base";
    private static final String LOCALE_DIR = "lang";
    private final JsonNode baseLocale = getLocaleJson(BASE_NAME);

    public static JsonNode getLocaleJson(String name) {
        var localeDir = new File(Utils.getRootDirectory(), LOCALE_DIR + "/");
        if (!localeDir.exists() && !localeDir.mkdir()) {
            throw new RuntimeException("Cannot create the config directory.");
        }

        var baseFile = new File(localeDir, name + ".json");
        if (baseFile.exists()) {
            try {
                return new JsonMapper().readTree(baseFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            return null;
        }
    }

    private final I18n parent;
    private JsonNode locale = null;

    public I18n(Locale locale) {
        this(locale, null);
    }

    public I18n(Locale locale, I18n parent) {
        this(Optional.ofNullable(locale)
            .map(Locale::toString).orElse(""), parent);
    }

    private static boolean isNullOrEmpty(String str) {
        if (str == null) return true;
        return str.isEmpty();
    }

    public I18n() {
        this("", null);
    }

    // Sometimes we need custom locale as well
    public I18n(String locale, I18n parent) {
        if (parent == null && !(isNullOrEmpty(locale) || locale.equals(BASE_NAME))) {
            parent = new I18n();
        }
        this.parent = parent;

        if (!isNullOrEmpty(locale)) {
            setLocale(locale);
        }
    }

    public final void setLocale(String locale) {
        if (isNullOrEmpty(locale)) return;
        this.locale = getLocaleJson(locale);
    }

    public final String getOrNull(String key) {
        return Optional.ofNullable(locale)
            .flatMap(l -> Optional.ofNullable(l.get(key))
                .map(JsonNode::textValue)
                .or(() ->
                    Optional.ofNullable(
                        JsonPathHelper.INSTANCE.readString(l, "$." + key)
                    )
                )
            )
            .or(() -> Optional.ofNullable(parent)
                .map(p -> p.get(key))
            )
            .or(() -> Optional.ofNullable(baseLocale)
                .flatMap(b -> Optional.ofNullable(b.get(key))
                    .map(JsonNode::textValue)
                    .or(() ->
                        Optional.ofNullable(
                            JsonPathHelper.INSTANCE.readString(b, "$." + key)
                        )
                    )
                )
            ).orElse(null);

        /*
        return locale?.get(key)?.textValue() ?:
        locale?.read<String>("$.$key") ?:
        parent?.get(key) ?:
        baseLocale?.get(key)?.textValue() ?:
        baseLocale?.read<String>("$.$key")
         */
    }

    public String get(String key) {
        var result = getOrNull(key);
        if (result != null) return result;

        Logger.warn(TranslateText.of("The message is not set for key %s!")
            .addWith(LiteralText.of(key).setColor(TextColor.AQUA)));
        return key;
    }

    public final String of(String key) {
        return of(key, (Map<String, Object>) null);
    }

    public final String of(String key, Map<String, Object> placeholders) {
        Map<String, Object> map = placeholders == null ?
            new HashMap<>() :
            new HashMap<>(placeholders);
        writeDefaultPlaceholders(map);

        var formatter = new ST(get(key));
        for (var entry : map.entrySet()) {
            formatter.add(entry.getKey(), entry.getValue());
        }

        return formatter.render();
    }

    protected void writeDefaultPlaceholders(Map<String, Object> placeholders) {

    }

    public final String of(String key, IPlaceholder... placeholders) {
        var map = new HashMap<String, Object>();
        for (var p : placeholders) {
            p.writePlaceholders(map);
        }

        return of(key, map);
    }
}