package com.duck.flexilogger

import kotlin.reflect.KClass

/**
 * Platform-specific console logging implementation.
 *
 * @param type The log level type.
 * @param tag The log tag.
 * @param message The log message.
 * @param throwable Optional throwable to log.
 */
internal expect fun platformLog(
    type: LogType,
    tag: String,
    message: String,
    throwable: Throwable? = null
)

/**
 * Platform-specific class name extraction from an object instance.
 *
 * @param obj The object to get the class name from.
 * @return The simple class name.
 */
internal expect fun getSimpleClassName(obj: Any): String

/**
 * Platform-specific class name extraction from a KClass.
 *
 * @param clazz The KClass to get the name from.
 * @return The simple class name.
 */
internal expect fun getSimpleClassName(clazz: KClass<*>): String

/**
 * Platform-specific call site capture.
 *
 * Captures the call site of the log call by walking the stack trace and finding
 * the first frame that is not part of FlexiLogger internals or a FlexiLog subclass.
 *
 * @param additionalSkipPackages Additional package prefixes to skip when walking the stack.
 * @return The [CallSite] representing where the log was called from, or null if unavailable.
 */
internal expect fun captureCallSite(additionalSkipPackages: List<String> = emptyList()): CallSite?
