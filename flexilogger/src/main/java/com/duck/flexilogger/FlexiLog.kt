package com.duck.flexilogger

import androidx.annotation.IntDef

/**
 * Created by Bradley Duck on 2019/02/14.
 * <p/>
 * Logging helper to provide flexible Log methods.
 */
abstract class FlexiLog {

    /**
     * Gets the class name string for the given [Object].
     *
     * @param o [Object] To get the String name of.
     * @return [String] The simple Name of the Object's Class.
     */
    private fun getClassName(o: Any): String {
        return CLASS + o.javaClass.simpleName
    }

    /**
     * Gets the class name string for the given [Class].
     *
     * @param clazz [Class] To get the String name of.
     * @return [String] The simple Name of the Class.
     */
    private fun getClassName(clazz: Class<*>): String {
        return CLASS + clazz.simpleName
    }

    /**
     * Calls [i].
     *
     * @param caller [Any] The caller of this method. This will be used to get the Class name of the Object.
     * @param msg    [String] The Message to be Logged.
     * @param tr     [Throwable] to be attached to the Log.
     */
    @JvmOverloads
    fun i(caller: Any, msg: String, tr: Throwable? = null) {
        if (tr == null) {
            i(getClassName(caller), msg)
        } else {
            i(getClassName(caller), msg, tr)
        }
    }

    /**
     * Calls [i].
     *
     * @param caller [Class] The Class of the caller of this method. This will be used to get the Class name of the Object.
     * @param msg    [String] The Message to be Logged.
     * @param tr     [Throwable] to be attached to the Log.
     */
    @JvmOverloads
    fun i(caller: Class<*>, msg: String, tr: Throwable? = null) {
        if (tr == null) {
            i(getClassName(caller), msg)
        } else {
            i(getClassName(caller), msg, tr)
        }
    }

    /**
     * Implement the actual Logging.
     *
     * @param tag [Class] The Log tag
     * @param msg [String] The Log message.
     */
    fun i(tag: String, msg: String) {
        if (canLogToConsole(i)) {
            logToConsole(i, tag, msg)
        }
        if (mustReport(i)) {
            report(i, tag, msg)
        }
    }

    /**
     * Implement the actual Logging.
     *
     * @param tag [Class] The Log tag
     * @param msg [String] The Log message.
     * @param tr  [Throwable] to be attached to the Log.
     */
    fun i(tag: String, msg: String, tr: Throwable) {
        if (canLogToConsole(i)) {
            logToConsole(i, tag, msg, tr)
        }
        if (mustReport(i)) {
            report(i, tag, msg, tr)
        }
    }

    /**
     * Calls [d].
     *
     * @param caller [Any] The caller of this method. This will be used to get the Class name of the Object.
     * @param msg    [String] The Message to be Logged.
     * @param tr     [Throwable] to be attached to the Log.
     */
    @JvmOverloads
    fun d(caller: Any, msg: String, tr: Throwable? = null) {
        if (tr == null) {
            d(getClassName(caller), msg)
        } else {
            d(getClassName(caller), msg, tr)
        }
    }

    /**
     * Calls [d].
     *
     * @param caller [Class] The Class of the caller of this method. This will be used to get the Class name of the Object.
     * @param msg    [String] The Message to be Logged.
     * @param tr     [Throwable] to be attached to the Log.
     */
    @JvmOverloads
    fun d(caller: Class<*>, msg: String, tr: Throwable? = null) {
        if (tr == null) {
            d(getClassName(caller), msg)
        } else {
            d(getClassName(caller), msg, tr)
        }
    }

    /**
     * Implement the actual Logging.
     *
     * @param tag [Class] The Log tag
     * @param msg [String] The Log message.
     */
    fun d(tag: String, msg: String) {
        if (canLogToConsole(d)) {
            logToConsole(d, tag, msg)
        }
        if (mustReport(d)) {
            report(d, tag, msg)
        }
    }

    /**
     * Implement the actual Logging.
     *
     * @param tag [Class] The Log tag
     * @param msg [String] The Log message.
     * @param tr  [Throwable] to be attached to the Log.
     */
    fun d(tag: String, msg: String, tr: Throwable) {
        if (canLogToConsole(d)) {
            logToConsole(d, tag, msg, tr)
        }
        if (mustReport(d)) {
            report(d, tag, msg, tr)
        }
    }

    /**
     * Calls [v].
     *
     * @param caller [Any] The caller of this method. This will be used to get the Class name of the Object.
     * @param msg    [String] The Message to be Logged.
     * @param tr     [Throwable] to be attached to the Log.
     */
    @JvmOverloads
    fun v(caller: Any, msg: String, tr: Throwable? = null) {
        if (tr == null) {
            v(getClassName(caller), msg)
        } else {
            v(getClassName(caller), msg, tr)
        }
    }

    /**
     * Calls [v].
     *
     * @param caller [Class] The Class of the caller of this method. This will be used to get the Class name of the Object.
     * @param msg    [String] The Message to be Logged.
     * @param tr     [Throwable] to be attached to the Log.
     */
    @JvmOverloads
    fun v(caller: Class<*>, msg: String, tr: Throwable? = null) {
        if (tr == null) {
            v(getClassName(caller), msg)
        } else {
            v(getClassName(caller), msg, tr)
        }
    }

    /**
     * Implement the actual Logging.
     *
     * @param tag [Class] The Log tag
     * @param msg [String] The Log message.
     */
    fun v(tag: String, msg: String) {
        if (canLogToConsole(v)) {
            logToConsole(v, tag, msg)
        }
        if (mustReport(v)) {
            report(v, tag, msg)
        }
    }

    /**
     * Implement the actual Logging.
     *
     * @param tag [Class] The Log tag
     * @param msg [String] The Log message.
     * @param tr  [Throwable] to be attached to the Log.
     */
    fun v(tag: String, msg: String, tr: Throwable) {
        if (canLogToConsole(v)) {
            logToConsole(v, tag, msg, tr)
        }
        if (mustReport(v)) {
            report(v, tag, msg, tr)
        }
    }

    /**
     * Calls [e].
     *
     * @param caller [Object] The caller of this method. This will be used to get the Class name of the Object.
     * @param msg    [String] The Message to be Logged.
     * @param tr     [Throwable] to be attached to the Log.
     */
    @JvmOverloads
    fun e(caller: Any, msg: String, tr: Throwable? = null) {
        if (tr == null) {
            e(getClassName(caller), msg)
        } else {
            e(getClassName(caller), msg, tr)
        }
    }

    /**
     * Calls [e].
     *
     * @param caller [Class] The Class of the caller of this method. This will be used to get the Class name of the Object.
     * @param msg    [String] The Message to be Logged.
     * @param tr     [Throwable] to be attached to the Log.
     */
    @JvmOverloads
    fun e(caller: Class<*>, msg: String, tr: Throwable? = null) {
        if (tr == null) {
            e(getClassName(caller), msg)
        } else {
            e(getClassName(caller), msg, tr)
        }
    }

    /**
     * Implement the actual Logging.
     *
     * @param tag [Class] The Log tag
     * @param msg [String] The Log message.
     */
    fun e(tag: String, msg: String) {
        if (canLogToConsole(e)) {
            logToConsole(e, tag, msg)
        }
        if (mustReport(e)) {
            report(e, tag, msg)
        }
    }

    /**
     * The actual Logging.
     *
     * @param tag [Class] The Log tag
     * @param msg [String] The Log message.
     * @param tr  [Throwable] to be attached to the Log.
     */
    fun e(tag: String, msg: String, tr: Throwable) {
        if (canLogToConsole(e)) {
            logToConsole(e, tag, msg, tr)
        }
        if (mustReport(e)) {
            report(e, tag, msg, tr)
        }
    }

    /**
     * The actual Logging out to the console.
     *
     * @param type [Int] @[LogType], the type of log this came from.
     * @param tag [Class] The Log tag
     * @param msg [String] The Log message..
     */
    private fun logToConsole(@LogType type: Int, tag: String, msg: String) {
        when (type) {
            i -> {
                android.util.Log.i(tag, msg)
            }
            d -> {
                android.util.Log.d(tag, msg)
            }
            v -> {
                android.util.Log.v(tag, msg)
            }
            e -> {
                android.util.Log.e(tag, msg)
            }
        }
    }

    /**
     * The actual Logging out to the console.
     *
     * @param type [Int] @[LogType], the type of log this came from.
     * @param tag [Class] The Log tag
     * @param msg [String] The Log message.
     * @param tr  [Throwable] to be attached to the Log.
     */
    private fun logToConsole(@LogType type: Int, tag: String, msg: String, tr: Throwable) {
        when (type) {
            i -> {
                android.util.Log.i(tag, msg, tr)
            }
            d -> {
                android.util.Log.d(tag, msg, tr)
            }
            v -> {
                android.util.Log.v(tag, msg, tr)
            }
            e -> {
                android.util.Log.e(tag, msg, tr)
            }
        }
    }

    /**
     * Implement the actual reporting.
     *
     * @param type [Int] @[LogType], the type of log this came from.
     * @param tag [Class] The Log tag
     * @param msg [String] The Log message.
     */
    abstract fun report(@LogType type: Int, tag: String, msg: String)

    /**
     * Implement the actual reporting.
     *
     * @param type [Int] @[LogType], the type of log this came from.
     * @param tag [Class] The Log tag
     * @param msg [String] The Log message.
     * @param tr  [Throwable] to be attached to the Log.
     */
    abstract fun report(@LogType type: Int, tag: String, msg: String, tr: Throwable)

    /**
     * Identifier for the type of Log message.
     * can be on of: [Companion.i], [Companion.d], [Companion.v], [Companion.e]
     */
    @IntDef(this.i, this.d, this.v, this.e)
    annotation class LogType

    /**
     * Used to determine if we should Lod to the console or not.
     */
    abstract fun canLogToConsole(@LogType type: Int): Boolean

    /**
     * Used to determine if we should send a report (to Crashlytics or equivalent)
     */
    abstract fun mustReport(@LogType type: Int): Boolean

    companion object {
        private val CLASS = "class: "
        /**
         * ID for [android.util.Log.i]
         */
        const val i: Int = 1
        /**
         * ID for [android.util.Log.d]
         */
        const val d: Int = 2
        /**
         * ID for [android.util.Log.v]
         */
        const val v: Int = 3
        /**
         * ID for [android.util.Log.e]
         */
        const val e: Int = 4
    }
}
