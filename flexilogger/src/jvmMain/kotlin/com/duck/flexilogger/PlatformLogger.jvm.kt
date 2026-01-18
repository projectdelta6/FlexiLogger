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
