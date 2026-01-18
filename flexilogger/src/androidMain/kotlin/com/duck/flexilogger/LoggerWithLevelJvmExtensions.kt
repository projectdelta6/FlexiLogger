@file:JvmName("LoggerWithLevelJvm")
package com.duck.flexilogger

/**
 * JVM/Android-specific extensions for LoggerWithLevel to maintain backward compatibility
 * with Class<*> parameters.
 */

/**
 * Calls [i].
 *
 * @param caller [Class] The Class of the caller of this method.
 * @param msg    [String] The Message to be Logged.
 * @param tr     [Throwable] to be attached to the Log.
 */
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
@JvmOverloads
fun LoggerWithLevel.wtf(caller: Class<*>, msg: String? = null, tr: Throwable? = null) {
    wtf(caller.simpleName, msg, tr)
}
