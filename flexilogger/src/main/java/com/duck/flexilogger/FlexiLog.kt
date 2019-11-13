package com.duck.flexilogger

import androidx.annotation.IntDef

/**
 * Created by Bradley Duck on 2019/02/14.
 * <p/>
 * Logging helper to provide flexible Log methods.
 */
abstract class FlexiLog {

    /**
     * Gets the class name string for the given [Any].
     *
     * @param o [Any] To get the String name of.
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
    fun i(caller: Any, msg: String? = null, tr: Throwable? = null) {
        val message: String = msg ?: ""
        if (tr == null) {
            i(getClassName(caller), message)
        } else {
            i(getClassName(caller), message, tr)
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
    fun i(caller: Class<*>, msg: String? = null, tr: Throwable? = null) {
        val message: String = msg ?: ""
        if (tr == null) {
            i(getClassName(caller), message)
        } else {
            i(getClassName(caller), message, tr)
        }
    }

    /**
     * Implement the actual Logging.
     *
     * @param tag [Class] The Log tag
     * @param msg [String] The Log message.
     * @param tr  [Throwable] to be attached to the Log.
     */
    @JvmOverloads
    fun i(tag: String, msg: String? = null, tr: Throwable? = null) {
        val message: String = msg ?: ""
        if (canLogToConsole(i)) {
            logToConsole(i, tag, message, tr)
        }
        if (mustReport(i)) {
            reportInternal(i, tag, message, tr)
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
    fun d(caller: Any, msg: String? = null, tr: Throwable? = null) {
        val message: String = msg ?: ""
        if (tr == null) {
            d(getClassName(caller), message)
        } else {
            d(getClassName(caller), message, tr)
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
    fun d(caller: Class<*>, msg: String? = null, tr: Throwable? = null) {
        val message: String = msg ?: ""
        if (tr == null) {
            d(getClassName(caller), message)
        } else {
            d(getClassName(caller), message, tr)
        }
    }

    /**
     * Implement the actual Logging.
     *
     * @param tag [Class] The Log tag
     * @param msg [String] The Log message.
     * @param tr  [Throwable] to be attached to the Log.
     */
    @JvmOverloads
    fun d(tag: String, msg: String? = null, tr: Throwable? = null) {
        val message: String = msg ?: ""
        if (canLogToConsole(d)) {
            logToConsole(d, tag, message, tr)
        }
        if (mustReport(d)) {
            reportInternal(d, tag, message, tr)
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
    fun v(caller: Any, msg: String? = null, tr: Throwable? = null) {
        val message: String = msg ?: ""
        if (tr == null) {
            v(getClassName(caller), message)
        } else {
            v(getClassName(caller), message, tr)
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
    fun v(caller: Class<*>, msg: String? = null, tr: Throwable? = null) {
        val message: String = msg ?: ""
        if (tr == null) {
            v(getClassName(caller), message)
        } else {
            v(getClassName(caller), message, tr)
        }
    }

    /**
     * Implement the actual Logging.
     *
     * @param tag [Class] The Log tag
     * @param msg [String] The Log message.
     * @param tr  [Throwable] to be attached to the Log.
     */
    @JvmOverloads
    fun v(tag: String, msg: String? = null, tr: Throwable? = null) {
        val message: String = msg ?: ""
        if (canLogToConsole(v)) {
            logToConsole(v, tag, message, tr)
        }
        if (mustReport(v)) {
            reportInternal(v, tag, message, tr)
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
    fun e(caller: Any, msg: String? = null, tr: Throwable? = null) {
        val message: String = msg ?: ""
        if (tr == null) {
            e(getClassName(caller), message)
        } else {
            e(getClassName(caller), message, tr)
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
    fun e(caller: Class<*>, msg: String? = null, tr: Throwable? = null) {
        val message: String = msg ?: ""
        if (tr == null) {
            e(getClassName(caller), message)
        } else {
            e(getClassName(caller), message, tr)
        }
    }

    /**
     * The actual Logging.
     *
     * @param tag [Class] The Log tag
     * @param msg [String] The Log message.
     * @param tr  [Throwable] to be attached to the Log.
     */
    @JvmOverloads
    fun e(tag: String, msg: String? = null, tr: Throwable? = null, forceReport: Boolean = false) {
        val message: String = msg ?: ""
        if (canLogToConsole(e)) {
            logToConsole(e, tag, message, tr)
        }
        if (forceReport || mustReport(e)) {
            reportInternal(e, tag, message, tr)
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
    private fun logToConsole(@LogType type: Int, tag: String, msg: String, tr: Throwable? = null) {
        if (tr != null) {
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
        } else {
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
    }

    private fun reportInternal(@LogType type: Int, tag: String, msg: String, tr: Throwable? = null) {
        if (tr == null) {
            report(type, tag, msg)
        } else {
            report(type, tag, msg, tr)
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
