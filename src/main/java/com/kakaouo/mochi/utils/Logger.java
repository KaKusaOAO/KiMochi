package com.kakaouo.mochi.utils;

import com.kakaouo.mochi.texts.*;
import com.kakaouo.mochi.utils.terminal.Terminal;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

public enum Logger {
    ;

    private static Level level = Level.VERBOSE;

    public static Level getLevel() {
        return level;
    }

    public static void setLevel(Level level) {
        Logger.level = level;
    }

    private static final Semaphore lock = new Semaphore(1);
    private static final List<AsyncLogListener> listeners = new ArrayList<>();
    private static boolean bootstrapped = false;
    private static boolean isShutdown = false;
    private static volatile Thread thread = null;
    private static final Queue<Runnable> queue = new ConcurrentLinkedQueue<>();

    private static TranslateText createPrefixFormat() {
        return TranslateText.of("%3$s - %1$s %2$s");
    }

    public static void addLogListener(AsyncLogListener listener) {
        listeners.add(listener);
    }

    public static void removeLogListener(AsyncLogListener listener) {
        listeners.remove(listener);
    }

    public static void runThreaded() {
        if (bootstrapped) return;
        bootstrapped = true;

        var t = new Thread(Logger::runEventLoop);
        t.setName("Logger Thread");
        t.setDaemon(true);

        thread = t;
        t.start();
    }

    private static void runEventLoop() {
        while (!isShutdown) {
            try {
                while (queue.isEmpty() && !isShutdown) {
                    Thread.sleep(16);
                    Thread.yield();
                }

                pollEvents();
                Thread.yield();
            } catch (Throwable ex) {
                System.out.println(ex.getMessage());
                System.out.println(ex);
            }
        }
    }

    public static void runManualPoll() {
        if (bootstrapped) return;
        bootstrapped = true;
        thread = Thread.currentThread();
    }

    public static void runBlocking() {
        runManualPoll();
        runEventLoop();
    }

    static {
        var shutdownHook = new Thread(() -> {
            flush();
            isShutdown = true;
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        Runtime.getRuntime().addShutdownHook(shutdownHook);
    }

    public static void pollEvents() {
        if (!bootstrapped) {
            throw new RuntimeException("Logger is not bootstrapped");
        }

        if (thread != Thread.currentThread()) {
            throw new RuntimeException("pollEvents() called from wrong thread");
        }

        while (!queue.isEmpty()) {
            var action = queue.poll();
            action.run();
            Thread.yield();
        }
    }

    public static CompletableFuture<Void> logToEmulatedTerminalAsync(Entry entry) {
        if (Logger.level.ordinal() > entry.level.ordinal()) {
            return CompletableFuture.completedFuture(null);
        }

        try {
            lock.acquire();
            getDefaultFormatteedLines(entry, true).forEach(Terminal::writeLine);
        } catch (InterruptedException e) {
            //
        } finally {
            lock.release();
        }

        return CompletableFuture.completedFuture(null);
    }

    private static void catchHandlerException(AsyncLogListener listener, Throwable ex) {

    }

    private static void callOrQueue(Runnable runnable) {
        if (!bootstrapped) {
            runThreaded();
            while (thread == null) {
                try {
                    Thread.sleep(16);
                } catch (InterruptedException e) {
                    //
                }
                Thread.yield();
            }

            var entry = new Entry(
                Level.WARN,
                LiteralText.of("*** Logger is not bootstrapped. ***"),
                Text.representClass(Logger.class),
                TextColor.GOLD,
                thread,
                Instant.now()
            );
            internalOnLogged(entry);

            entry = new Entry(
                Level.WARN,
                LiteralText.of("Logger now requires either runThreaded(), runBlocking() or runManualPoll() to poll log events."),
                Text.representClass(Logger.class),
                TextColor.GOLD,
                thread,
                Instant.now()
            );
            internalOnLogged(entry);

            entry = new Entry(
                Level.WARN,
                LiteralText.of("The threaded approach will be used by default."),
                Text.representClass(Logger.class),
                TextColor.GOLD,
                thread,
                Instant.now()
            );
            internalOnLogged(entry);
        }
        if (!bootstrapped) {
            throw new RuntimeException("Logger is not bootstrapped.");
        }

        if (Thread.currentThread() != thread) {
            queue.add(runnable);
        } else {
            runnable.run();
        }
    }

    public static void flush() {
        var called = new AtomicBoolean(false);
        callOrQueue(() -> {
            called.set(true);
        });

        while (!called.get()) {
            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                //
            }
            Thread.yield();
        }
    }

    private static void internalOnLogged(Entry entry) {
        listeners.forEach(listener -> {
            //noinspection CatchMayIgnoreException
            try {
                var future = listener.invoke(entry);
                future.whenComplete((v, ex) -> {
                    if (ex != null) {
                        catchHandlerException(listener, ex);
                    }
                });
            } catch (Throwable ex) {
                catchHandlerException(listener, ex);
            }
        });
    }

    /*
    private static String padLeft(String str, int totalLength) {
        if (totalLength < 0) {
            throw new IllegalArgumentException("totalLength is less than 0");
        }

        int length = str.length();
        int count = totalLength - length;
        if (count <= 0) return str;

        return " ".repeat(count) + str;
    }
     */

    public static List<String> getDefaultFormatteedLines(Entry entry, boolean ascii) {
        var name = entry.tag.copy();
        var f = createPrefixFormat();
        var thread = entry.thread;
        name.setColor(entry.color);

        var tag = LiteralText
            .of(String.format("[%s@%s] ", thread.getName(), thread.getId()))
            .setColor(TextColor.DARK_GRAY)
            .addExtra(
                TranslateText.of("[%s]")
                    .addWith(name)
                    .setColor(entry.color)
            );

        var now = new SimpleDateFormat().format(new Date());
        var t = entry.text.copy();
        var prefix = f.addWith(tag, LiteralText.of(""), LiteralText.of(now));

        var pPlain = prefix.toPlainText();
        var pf = ascii ? prefix.toAscii() : pPlain;
        var content = ascii ? t.toAscii() : t.toPlainText();
        var lines = content.split("\n");

        var remainPrefixPlain = "+ ->> ";
        var remainPrefix = (ascii ? TextColor.DARK_GRAY.toAsciiCode() : "") +
            remainPrefixPlain + (ascii ? AsciiColor.RESET.toAsciiCode() : "");

        var builder = new ArrayList<String>();
        builder.add(pf + lines[0]);
        for (int i = 1; i < lines.length; i++) {
            var c = lines[i];
            builder.add(" ".repeat(pPlain.length() - remainPrefixPlain.length()) + remainPrefix + c);
        }

        return builder;
    }

    public static CompletableFuture<Void> logToConsoleAsync(Entry entry) {
        if (Logger.level.ordinal() > entry.level.ordinal()) {
            return CompletableFuture.completedFuture(null);
        }

        try {
            lock.acquire();
            getDefaultFormatteedLines(entry, true).forEach(System.out::println);
        } catch (InterruptedException e) {
            //
        } finally {
            lock.release();
        }

        return CompletableFuture.completedFuture(null);
    }

    private static Text<?> convertToText(Object obj) {
        if (obj == null) return LiteralText.of("<null>").setColor(TextColor.RED);
        if (obj instanceof Text<?> text) return text;
        if (obj instanceof Throwable throwable) {
            var sw = new StringWriter();
            var pw = new PrintWriter(sw);
            throwable.printStackTrace(pw);
            pw.flush();
            return LiteralText.of(sw.toString());
        }
        if (obj instanceof Class<?> clz) {
            return Text.representClass(clz);
        }

        return LiteralText.of(obj.toString());
    }

    private static Class<?> getCallSourceClass() {
        var stackTrace = Thread.currentThread().getStackTrace();
        try {
            return Class.forName(stackTrace[3].getClassName());
        } catch (ClassNotFoundException ex) {
            return null;
        }
    }

    private static void log(Level level, TextColor color, Text<?> name, Text<?> text) {
        var entry = new Entry(level, text, name, color, Thread.currentThread(), Instant.now());
        callOrQueue(() -> internalOnLogged(entry));
    }

    public static void verbose(Object text, Object tag) {
        log(Level.VERBOSE, TextColor.DARK_GRAY, convertToText(tag), convertToText(text));
    }

    public static void verbose(Object text) {
        verbose(text, getCallSourceClass());
    }

    public static void log(Object text, Object tag) {
        log(Level.LOG, TextColor.GRAY, convertToText(tag), convertToText(text));
    }

    public static void log(Object text) {
        log(text, getCallSourceClass());
    }

    public static void info(Object text, Object tag) {
        log(Level.INFO, TextColor.GREEN, convertToText(tag), convertToText(text));
    }

    public static void info(Object text) {
        info(text, getCallSourceClass());
    }

    public static void warn(Object text, Object tag) {
        log(Level.WARN, TextColor.GOLD, convertToText(tag), convertToText(text));
    }

    public static void warn(Object text) {
        warn(text, getCallSourceClass());
    }

    public static void error(Object text, Object tag) {
        log(Level.ERROR, TextColor.RED, convertToText(tag), convertToText(text));
    }

    public static void error(Object text) {
        error(text, getCallSourceClass());
    }

    @FunctionalInterface
    public interface AsyncLogListener {
        CompletableFuture<Void> invoke(Entry entry);
    }

    public record Entry(Level level,
                        Text<?> text,
                        Text<?> tag,
                        TextColor color,
                        Thread thread,
                        Instant timestamp) {

    }

    public enum Level {
        VERBOSE,
        LOG,
        INFO,
        WARN,
        ERROR,
        FATAL
    }
}
