package com.kakaouo.mochi.test;

import com.kakaouo.mochi.texts.LiteralText;
import com.kakaouo.mochi.utils.Logger;
import com.kakaouo.mochi.utils.terminal.BrigadierTerminal;
import com.kakaouo.mochi.utils.terminal.Terminal;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import java.util.Random;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;

public class Test {

    public static void main(String[] args) {
        Logger.addLogListener(Logger::logToEmulatedTerminalAsync);
        Logger.runThreaded();

        var timerThread = new Thread(Test::timerThread);
        timerThread.start();

        var dispatcher = new CommandDispatcher<>();
        dispatcher.register(literal("foo")
            .then(literal("bar")
                .then(argument("value", IntegerArgumentType.integer())
                    .executes(ctx -> {
                        var i = ctx.getArgument("value", Integer.class);
                        Logger.info("Foo bar with " + i + "!");
                        return 1;
                    })
                )
                .executes(ctx -> {
                    Logger.info("Foo bar!");
                    return 1;
                })
            )
        );

        while (true) {
            var source = new Object();
            var line = Terminal.readLine(LiteralText.of("> "),
                BrigadierTerminal.createCompleter(dispatcher, source),
                BrigadierTerminal.createHighlighter(dispatcher, source)
            );

            try {
                dispatcher.execute(line, source);
            } catch (CommandSyntaxException e) {
                Logger.error("Error!");
            }
        }
    }

    private static void timerThread() {
        var i = 0;
        while (true) {
            try {
                //noinspection BusyWait
                Thread.sleep(1000);
                i++;

                var j = new Random().nextInt(100);
                Logger.warn(LiteralText.of("Random! " + j + " (" + i + ")"));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
