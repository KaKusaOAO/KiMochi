package com.kakaouo.mochi.utils

import kotlinx.coroutines.*
import java.util.concurrent.CompletableFuture

object UtilsKt {
    fun <T> T?.asNullable(): T? = this

    @OptIn(DelicateCoroutinesApi::class)
    fun <T> async(block: suspend () -> T): Deferred<T> {
        return GlobalScope.async inner@ {
            return@inner block()
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun asyncDiscard(block: suspend () -> Unit) {
        @Suppress("DeferredResultUnused")
        GlobalScope.async inner@ {
            try {
                return@inner block()
            } catch (ex: Throwable) {
                Logger.error("Unhandled exception is discarded!")
                Logger.error(ex.stackTraceToString())
            }
        }
    }

    @JvmName("promisifyVoid")
    fun <T> promisify(block: suspend () -> T): CompletableFuture<T> {
        val future = CompletableFuture<T>()
        asyncDiscard {
            try {
                val result = block()
                future.complete(result)
            } catch(ex: Throwable) {
                future.completeExceptionally(ex)
            }
        }

        return future
    }

    fun promisify(block: suspend () -> Unit): CompletableFuture<Void> {
        val future = CompletableFuture<Void>()
        asyncDiscard {
            try {
                block()
                future.complete(null)
            } catch(ex: Throwable) {
                future.completeExceptionally(ex)
            }
        }

        return future
    }
}