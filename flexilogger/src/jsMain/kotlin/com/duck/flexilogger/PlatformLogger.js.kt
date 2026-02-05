package com.duck.flexilogger

import kotlin.js.Date
import kotlin.reflect.KClass

internal actual fun platformLog(
    type: LogType,
    tag: String,
    message: String,
    throwable: Throwable?
) {
    val formattedMessage = "[$tag] $message"
    val throwableInfo = throwable?.let { "\n${it.stackTraceToString()}" } ?: ""
    val fullMessage = "$formattedMessage$throwableInfo"

    when (type) {
        LogType.V, LogType.D -> console.log(fullMessage)
        LogType.I -> console.info(fullMessage)
        LogType.W -> console.warn(fullMessage)
        LogType.E, LogType.WTF -> console.error(fullMessage)
    }
}

internal actual fun getSimpleClassName(obj: Any): String =
    obj::class.simpleName ?: "Unknown"

internal actual fun getSimpleClassName(clazz: KClass<*>): String =
    clazz.simpleName ?: "Unknown"

internal actual fun currentTimeMillis(): Long = Date.now().toLong()

/**
 * Call site capture is not reliably supported on JavaScript.
 * Returns null as JS stack traces are not easily parseable in a cross-browser way.
 */
@Suppress("UNUSED_PARAMETER")
internal actual fun captureCallSite(additionalSkipPackages: List<String>): CallSite? = null
