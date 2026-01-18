package com.duck.flexilogger

import platform.Foundation.NSDate
import platform.Foundation.NSLog
import platform.Foundation.timeIntervalSince1970
import kotlin.reflect.KClass

internal actual fun platformLog(
    type: LogType,
    tag: String,
    message: String,
    throwable: Throwable?
) {
    val level = when (type) {
        LogType.V -> "VERBOSE"
        LogType.D -> "DEBUG"
        LogType.I -> "INFO"
        LogType.W -> "WARN"
        LogType.E -> "ERROR"
        LogType.WTF -> "WTF"
    }
    val throwableInfo = throwable?.let { "\n${it.stackTraceToString()}" } ?: ""
    NSLog("[$level] $tag: $message$throwableInfo")
}

internal actual fun getSimpleClassName(obj: Any): String =
    obj::class.simpleName ?: obj::class.toString()

internal actual fun getSimpleClassName(clazz: KClass<*>): String =
    clazz.simpleName ?: clazz.toString()

internal actual fun currentTimeMillis(): Long =
    (NSDate().timeIntervalSince1970 * 1000).toLong()
