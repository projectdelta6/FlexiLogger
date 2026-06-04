@file:JvmName("LoggerWithLevelJvm")
@file:Suppress("EXTENSION_SHADOWED_BY_MEMBER") // Class<*> overloads are shadowed by the Any overloads in Kotlin (by design); kept for Java interop.
package com.duck.flexilogger

/**
 * JVM-specific extensions for LoggerWithLevel to maintain backward compatibility
 * with Class<*> parameters.
 */

private const val CLASS_OVERLOAD_DEPRECATION =
    "Redundant: LoggerWithLevel's Any overload now resolves Class/KClass callers to the correct tag. " +
        "Shadowed in Kotlin; kept only for legacy Java interop."

/**
 * Calls [i].
 *
 * @param caller [Class] The Class of the caller of this method.
 * @param msg    [String] The Message to be Logged.
 * @param tr     [Throwable] to be attached to the Log.
 */
@Deprecated(CLASS_OVERLOAD_DEPRECATION)
@JvmOverloads
fun LoggerWithLevel.i(caller: Class<*>, msg: String? = null, tr: Throwable? = null) {
    i(caller.simpleName, msg, tr)
}

/**
 * Calls [d].
 *
 * @param caller [Class] The Class of the caller of this method.
 * @param msg    [String] The Message to be Logged.
 * @param tr     [Throwable] to be attached to the Log.
 */
@Deprecated(CLASS_OVERLOAD_DEPRECATION)
@JvmOverloads
fun LoggerWithLevel.d(caller: Class<*>, msg: String? = null, tr: Throwable? = null) {
    d(caller.simpleName, msg, tr)
}

/**
 * Calls [v].
 *
 * @param caller [Class] The Class of the caller of this method.
 * @param msg    [String] The Message to be Logged.
 * @param tr     [Throwable] to be attached to the Log.
 */
@Deprecated(CLASS_OVERLOAD_DEPRECATION)
@JvmOverloads
fun LoggerWithLevel.v(caller: Class<*>, msg: String? = null, tr: Throwable? = null) {
    v(caller.simpleName, msg, tr)
}

/**
 * Calls [e].
 *
 * @param caller [Class] The Class of the caller of this method.
 * @param msg    [String] The Message to be Logged.
 * @param tr     [Throwable] to be attached to the Log.
 * @param forceReport [Boolean] Force reporting even if shouldReport returns false.
 */
@Deprecated(CLASS_OVERLOAD_DEPRECATION)
@JvmOverloads
fun LoggerWithLevel.e(caller: Class<*>, msg: String? = null, tr: Throwable? = null, forceReport: Boolean = false) {
    e(caller.simpleName, msg, tr, forceReport)
}

/**
 * Calls [w].
 *
 * @param caller [Class] The Class of the caller of this method.
 * @param msg    [String] The Message to be Logged.
 * @param tr     [Throwable] to be attached to the Log.
 */
@Deprecated(CLASS_OVERLOAD_DEPRECATION)
@JvmOverloads
fun LoggerWithLevel.w(caller: Class<*>, msg: String? = null, tr: Throwable? = null) {
    w(caller.simpleName, msg, tr)
}

/**
 * Calls [wtf].
 *
 * @param caller [Class] The Class of the caller of this method.
 * @param msg    [String] The Message to be Logged.
 * @param tr     [Throwable] to be attached to the Log.
 */
@Deprecated(CLASS_OVERLOAD_DEPRECATION)
@JvmOverloads
fun LoggerWithLevel.wtf(caller: Class<*>, msg: String? = null, tr: Throwable? = null) {
    wtf(caller.simpleName, msg, tr)
}
