package com.duck.flexilogger

import android.util.Log
import kotlin.reflect.KClass

internal actual fun platformLog(
    type: LogType,
    tag: String,
    message: String,
    throwable: Throwable?
) {
    if (throwable != null) {
        when (type) {
            LogType.I -> Log.i(tag, message, throwable)
            LogType.D -> Log.d(tag, message, throwable)
            LogType.V -> Log.v(tag, message, throwable)
            LogType.E -> Log.e(tag, message, throwable)
            LogType.W -> Log.w(tag, message, throwable)
            LogType.WTF -> Log.wtf(tag, message, throwable)
        }
    } else {
        when (type) {
            LogType.I -> Log.i(tag, message)
            LogType.D -> Log.d(tag, message)
            LogType.V -> Log.v(tag, message)
            LogType.E -> Log.e(tag, message)
            LogType.W -> Log.w(tag, message)
            LogType.WTF -> Log.wtf(tag, message)
        }
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
    "dalvik.system.VMStack",
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
