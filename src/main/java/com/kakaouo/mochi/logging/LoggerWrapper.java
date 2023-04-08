package com.kakaouo.mochi.logging;


import com.kakaouo.mochi.texts.LiteralText;
import com.kakaouo.mochi.texts.Text;
import com.kakaouo.mochi.texts.TextColor;
import com.kakaouo.mochi.utils.Logger;
import org.slf4j.Marker;

public class LoggerWrapper implements org.slf4j.Logger {
    private static Logger.Level level = Logger.Level.INFO;
    public final String tagName;

    public LoggerWrapper(String tagName) {
        this.tagName = tagName;
    }

    private Text<?> createClassOrNormalTag(String str) {
        if (str == null) return LiteralText.of("<null>").setColor(TextColor.RED);

        try {
            var clz = Class.forName(str);
            return Text.representClass(clz);
        } catch (ClassNotFoundException e) {
            return LiteralText.of(str);
        }
    }

    private Text<?> getTag() {
        return createClassOrNormalTag(tagName);
    }

    @Override
    public String getName() {
        return tagName;
    }

    @Override
    public boolean isTraceEnabled() {
        return false;
    }

    private String processFormat(String format, Object... arguments) {
        if (format == null) return null;
        return String.format(format.replace("{}", "%s"), arguments);
    }

    @Override
    public void trace(String msg) {
        if (level.ordinal() > Logger.Level.VERBOSE.ordinal()) return;
        Logger.verbose(msg, getTag());
    }

    @Override
    public void trace(String format, Object arg) {
        if (level.ordinal() > Logger.Level.VERBOSE.ordinal()) return;
        Logger.verbose(processFormat(format, arg), getTag());
    }

    @Override
    public void trace(String format, Object arg1, Object arg2) {
        if (level.ordinal() > Logger.Level.VERBOSE.ordinal()) return;
        Logger.verbose(processFormat(format, arg1, arg2), getTag());
    }

    @Override
    public void trace(String format, Object... arguments) {
        if (level.ordinal() > Logger.Level.VERBOSE.ordinal()) return;
        Logger.verbose(processFormat(format, arguments), getTag());
    }

    @Override
    public void trace(String msg, Throwable t) {
        if (level.ordinal() > Logger.Level.VERBOSE.ordinal()) return;
        Logger.verbose(msg, getTag());
        Logger.verbose(t, getTag());
    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
        return false;
    }

    @Override
    public void trace(Marker marker, String msg) {
        trace(msg);
    }

    @Override
    public void trace(Marker marker, String format, Object arg) {
        trace(format, arg);
    }

    @Override
    public void trace(Marker marker, String format, Object arg1, Object arg2) {
        trace(format, arg1, arg2);
    }

    @Override
    public void trace(Marker marker, String format, Object... argArray) {
        trace(format, argArray);
    }

    @Override
    public void trace(Marker marker, String msg, Throwable t) {
        trace(msg, t);
    }

    @Override
    public boolean isDebugEnabled() {
        return false;
    }

    @Override
    public void debug(String msg) {
        if (level.ordinal() > Logger.Level.LOG.ordinal()) return;
        Logger.log(msg, getTag());
    }

    @Override
    public void debug(String format, Object arg) {
        if (level.ordinal() > Logger.Level.LOG.ordinal()) return;
        Logger.log(processFormat(format, arg), getTag());
    }

    @Override
    public void debug(String format, Object arg1, Object arg2) {
        if (level.ordinal() > Logger.Level.LOG.ordinal()) return;
        Logger.log(processFormat(format, arg1, arg2), getTag());
    }

    @Override
    public void debug(String format, Object... arguments) {
        if (level.ordinal() > Logger.Level.LOG.ordinal()) return;
        Logger.log(processFormat(format, arguments), getTag());
    }

    @Override
    public void debug(String msg, Throwable t) {
        if (level.ordinal() > Logger.Level.LOG.ordinal()) return;
        Logger.log(msg, getTag());
        Logger.log(t, getTag());
    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
        return false;
    }

    @Override
    public void debug(Marker marker, String msg) {
        debug(msg);
    }

    @Override
    public void debug(Marker marker, String format, Object arg) {
        debug(format, arg);
    }

    @Override
    public void debug(Marker marker, String format, Object arg1, Object arg2) {
        debug(format, arg1, arg2);
    }

    @Override
    public void debug(Marker marker, String format, Object... arguments) {
        debug(format, arguments);
    }

    @Override
    public void debug(Marker marker, String msg, Throwable t) {
        debug(msg, t);
    }

    @Override
    public boolean isInfoEnabled() {
        return false;
    }

    @Override
    public void info(String msg) {
        if (level.ordinal() > Logger.Level.INFO.ordinal()) return;
        Logger.info(msg, getTag());
    }

    @Override
    public void info(String format, Object arg) {
        if (level.ordinal() > Logger.Level.INFO.ordinal()) return;
        Logger.info(processFormat(format, arg), getTag());
    }

    @Override
    public void info(String format, Object arg1, Object arg2) {
        if (level.ordinal() > Logger.Level.INFO.ordinal()) return;
        Logger.info(processFormat(format, arg1, arg2), getTag());
    }

    @Override
    public void info(String format, Object... arguments) {
        if (level.ordinal() > Logger.Level.INFO.ordinal()) return;
        Logger.info(processFormat(format, arguments), getTag());
    }

    @Override
    public void info(String msg, Throwable t) {
        if (level.ordinal() > Logger.Level.INFO.ordinal()) return;
        Logger.info(msg, getTag());
        Logger.info(t, getTag());
    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
        return false;
    }

    @Override
    public void info(Marker marker, String msg) {
        info(msg);
    }

    @Override
    public void info(Marker marker, String format, Object arg) {
        info(format, arg);
    }

    @Override
    public void info(Marker marker, String format, Object arg1, Object arg2) {
        info(format, arg1, arg2);
    }

    @Override
    public void info(Marker marker, String format, Object... arguments) {
        info(format, arguments);
    }

    @Override
    public void info(Marker marker, String msg, Throwable t) {
        info(msg, t);
    }

    @Override
    public boolean isWarnEnabled() {
        return false;
    }

    @Override
    public void warn(String msg) {
        if (level.ordinal() > Logger.Level.WARN.ordinal()) return;
        Logger.warn(msg, getTag());
    }

    @Override
    public void warn(String format, Object arg) {
        if (level.ordinal() > Logger.Level.WARN.ordinal()) return;
        Logger.warn(processFormat(format, arg), getTag());
    }

    @Override
    public void warn(String format, Object... arguments) {
        if (level.ordinal() > Logger.Level.WARN.ordinal()) return;
        Logger.warn(processFormat(format, arguments), getTag());
    }

    @Override
    public void warn(String format, Object arg1, Object arg2) {
        if (level.ordinal() > Logger.Level.WARN.ordinal()) return;
        Logger.warn(processFormat(format, arg1, arg2), getTag());
    }

    @Override
    public void warn(String msg, Throwable t) {
        if (level.ordinal() > Logger.Level.WARN.ordinal()) return;
        Logger.warn(msg, getTag());
        Logger.warn(t, getTag());
    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
        return false;
    }

    @Override
    public void warn(Marker marker, String msg) {
        warn(msg);
    }

    @Override
    public void warn(Marker marker, String format, Object arg) {
        warn(format, arg);
    }

    @Override
    public void warn(Marker marker, String format, Object arg1, Object arg2) {
        warn(format, arg1, arg2);
    }

    @Override
    public void warn(Marker marker, String format, Object... arguments) {
        warn(format, arguments);
    }

    @Override
    public void warn(Marker marker, String msg, Throwable t) {
        warn(msg, t);
    }

    @Override
    public boolean isErrorEnabled() {
        return false;
    }

    @Override
    public void error(String msg) {
        if (level.ordinal() > Logger.Level.ERROR.ordinal()) return;
        Logger.error(msg, getTag());
    }

    @Override
    public void error(String format, Object arg) {
        if (level.ordinal() > Logger.Level.ERROR.ordinal()) return;
        Logger.error(processFormat(format, arg), getTag());
    }

    @Override
    public void error(String format, Object arg1, Object arg2) {
        if (level.ordinal() > Logger.Level.ERROR.ordinal()) return;
        Logger.error(processFormat(format, arg1, arg2), getTag());
    }

    @Override
    public void error(String format, Object... arguments) {
        if (level.ordinal() > Logger.Level.ERROR.ordinal()) return;
        Logger.error(processFormat(format, arguments), getTag());
    }

    @Override
    public void error(String msg, Throwable t) {
        if (level.ordinal() > Logger.Level.ERROR.ordinal()) return;
        Logger.error(msg, getTag());
        Logger.error(t, getTag());
    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
        return false;
    }

    @Override
    public void error(Marker marker, String msg) {
        error(msg);
    }

    @Override
    public void error(Marker marker, String format, Object arg) {
        error(format, arg);
    }

    @Override
    public void error(Marker marker, String format, Object arg1, Object arg2) {
        error(format, arg1, arg2);
    }

    @Override
    public void error(Marker marker, String format, Object... arguments) {
        error(format, arguments);
    }

    @Override
    public void error(Marker marker, String msg, Throwable t) {
        error(msg, t);
    }
}