package com.duck.flexilogger

import java.util.logging.Level
import java.util.logging.Logger
import kotlin.reflect.KClass

internal actual fun platformLog(
    type: LogType,
    tag: String,
    message: String,
    throwable: Throwable?
) {
    val logger = Logger.getLogger(tag)
    val level = when (type) {
        LogType.V -> Level.FINEST
        LogType.D -> Level.FINE
        LogType.I -> Level.INFO
        LogType.W -> Level.WARNING
        LogType.E, LogType.WTF -> Level.SEVERE
    }
    if (throwable != null) {
        logger.log(level, message, throwable)
    } else {
        logger.log(level, message)
    }
}

internal actual fun getSimpleClassName(obj: Any): String = obj::class.java.simpleName

internal actual fun getSimpleClassName(clazz: KClass<*>): String = clazz.java.simpleName

internal actual fun currentTimeMillis(): Long = System.currentTimeMillis()

/**
 * Internal packages that are always skipped when capturing call site.
 */
private val internalSkipPackages = listOf(
    "com.duck.flexilogger",
    "java.lang.Thread",
    "kotlin.coroutines.jvm.internal"
)

internal actual fun captureCallSite(additionalSkipPackages: List<String>): CallSite? {
    val stackTrace = Thread.currentThread().stackTrace

    for (element in stackTrace) {
        val className = element.className

        // Skip internal packages
        if (internalSkipPackages.any { className.startsWith(it) }) continue

        // Skip additional user-specified packages
        if (additionalSkipPackages.any { className.startsWith(it) }) continue

        // Skip if this class extends FlexiLog (the user's Log object)
        try {
            val clazz = Class.forName(className)
            if (FlexiLog::class.java.isAssignableFrom(clazz)) continue
        } catch (_: Exception) {
            // Class not found or other error, continue checking
        }

        return CallSite(
            className = className,
            methodName = element.methodName,
            fileName = element.fileName,
            lineNumber = element.lineNumber
        )
    }
    return null
}
