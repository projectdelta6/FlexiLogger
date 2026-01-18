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
