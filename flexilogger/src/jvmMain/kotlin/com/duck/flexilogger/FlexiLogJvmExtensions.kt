@file:JvmName("FlexiLogJvm")
@file:Suppress("EXTENSION_SHADOWED_BY_MEMBER") // Class<*> overloads are shadowed by the Any overloads in Kotlin (by design); kept for Java interop.
package com.duck.flexilogger

/**
 * JVM-specific extensions for FlexiLog to maintain backward compatibility
 * with Class<*> parameters.
 */

private const val CLASS_OVERLOAD_DEPRECATION =
    "Redundant: FlexiLog's Any overload now resolves Class/KClass callers to the correct tag. " +
        "Shadowed in Kotlin; kept only for legacy Java interop."

/**
 * Gets the class name string for the given [Class].
 *
 * @param clazz [Class] To get the String name of.
 * @return [String] The simple Name of the Class.
 */
@Deprecated(
    "Redundant helper; use clazz.simpleName directly.",
    ReplaceWith("clazz.simpleName")
)
fun FlexiLog.getClassName(clazz: Class<*>): String = clazz.simpleName

/**
 * Calls [i].
 *
 * @param caller [Class] The Class of the caller of this method.
 * @param msg    [String] The Message to be Logged.
 * @param tr     [Throwable] to be attached to the Log.
 */
@Deprecated(CLASS_OVERLOAD_DEPRECATION)
@JvmOverloads
fun FlexiLog.i(caller: Class<*>, msg: String? = null, tr: Throwable? = null) {
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
fun FlexiLog.d(caller: Class<*>, msg: String? = null, tr: Throwable? = null) {
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
fun FlexiLog.v(caller: Class<*>, msg: String? = null, tr: Throwable? = null) {
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
fun FlexiLog.e(caller: Class<*>, msg: String? = null, tr: Throwable? = null, forceReport: Boolean = false) {
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
fun FlexiLog.w(caller: Class<*>, msg: String? = null, tr: Throwable? = null) {
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
fun FlexiLog.wtf(caller: Class<*>, msg: String? = null, tr: Throwable? = null) {
    wtf(caller.simpleName, msg, tr)
}
