package com.duck.flexilogger

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
    protected fun getClassName(o: Any): String {
        return CLASS + o.javaClass.simpleName
    }

    /**
     * Gets the class name string for the given [Class].
     *
     * @param clazz [Class] To get the String name of.
     * @return [String] The simple Name of the Class.
     */
    protected fun getClassName(clazz: Class<*>): String {
        return CLASS + clazz.simpleName
    }

    /**
     * Only calls [log] if the [condition] is true.
     *
     * @param condition [Boolean] The condition to check.
     * @param log [FlexiLog.() -> Unit] The Log to call if the condition is true.
     */
    fun onCondition(condition: Boolean, log: (FlexiLog) -> Unit) {
        if(condition) {
            log(this)
        }
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
        actionLog(LogType.I, tag, msg ?: "", tr)
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
        actionLog(LogType.D, tag, msg ?: "", tr)
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
        actionLog(LogType.V, tag, msg ?: "", tr)
    }

    /**
     * Calls [e].
     *
     * @param caller [Object] The caller of this method. This will be used to get the Class name of the Object.
     * @param msg    [String] The Message to be Logged.
     * @param tr     [Throwable] to be attached to the Log.
     */
    @JvmOverloads
    fun e(caller: Any, msg: String? = null, tr: Throwable? = null, forceReport: Boolean = false) {
        val message: String = msg ?: ""
        if (tr == null) {
            e(getClassName(caller), message, forceReport = forceReport)
        } else {
            e(getClassName(caller), message, tr, forceReport)
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
    fun e(caller: Class<*>, msg: String? = null, tr: Throwable? = null, forceReport: Boolean = false) {
        val message: String = msg ?: ""
        if (tr == null) {
            e(getClassName(caller), message, forceReport = forceReport)
        } else {
            e(getClassName(caller), message, tr, forceReport)
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
        actionLog(LogType.E, tag, msg ?: "", tr, forceReport)
    }

    /**
     * Calls [w].
     *
     * @param caller [Any] The caller of this method. This will be used to get the Class name of the Object.
     * @param msg    [String] The Message to be Logged.
     * @param tr     [Throwable] to be attached to the Log.
     */
    @JvmOverloads
    fun w(caller: Any, msg: String? = null, tr: Throwable? = null) {
        val message: String = msg ?: ""
        if (tr == null) {
            w(getClassName(caller), message)
        } else {
            w(getClassName(caller), message, tr)
        }
    }

    /**
     * Calls [w].
     *
     * @param caller [Class] The Class of the caller of this method. This will be used to get the Class name of the Object.
     * @param msg    [String] The Message to be Logged.
     * @param tr     [Throwable] to be attached to the Log.
     */
    @JvmOverloads
    fun w(caller: Class<*>, msg: String? = null, tr: Throwable? = null) {
        val message: String = msg ?: ""
        if (tr == null) {
            w(getClassName(caller), message)
        } else {
            w(getClassName(caller), message, tr)
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
    fun w(tag: String, msg: String? = null, tr: Throwable? = null) {
        actionLog(LogType.W, tag, msg ?: "", tr)
    }

    /**
     * Calls [wtf].
     *
     * @param caller [Any] The caller of this method. This will be used to get the Class name of the Object.
     * @param msg    [String] The Message to be Logged.
     * @param tr     [Throwable] to be attached to the Log.
     */
    @JvmOverloads
    fun wtf(caller: Any, msg: String? = null, tr: Throwable? = null) {
        val message: String = msg ?: ""
        if (tr == null) {
            wtf(getClassName(caller), message)
        } else {
            wtf(getClassName(caller), message, tr)
        }
    }

    /**
     * Calls [wtf].
     *
     * @param caller [Class] The Class of the caller of this method. This will be used to get the Class name of the Object.
     * @param msg    [String] The Message to be Logged.
     * @param tr     [Throwable] to be attached to the Log.
     */
    @JvmOverloads
    fun wtf(caller: Class<*>, msg: String? = null, tr: Throwable? = null) {
        val message: String = msg ?: ""
        if (tr == null) {
            wtf(getClassName(caller), message)
        } else {
            wtf(getClassName(caller), message, tr)
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
    fun wtf(tag: String, msg: String? = null, tr: Throwable? = null) {
        actionLog(LogType.WTF, tag, msg ?: "", tr)
    }

    protected fun actionLog(type: LogType, tag: String, msg: String, tr: Throwable? = null, forceReport: Boolean = false) {
        logToConsole(type, tag, msg, tr)
        reportInternal(type, tag, msg, tr, forceReport)
        writeToFileInternal(System.currentTimeMillis(), type, tag, msg, tr)
    }

    /**
     * The actual Logging out to the console.
     *
     * @param type [Int] @[LogType], the type of log this came from.
     * @param tag [Class] The Log tag
     * @param msg [String] The Log message.
     * @param tr  [Throwable] to be attached to the Log.
     */
    protected fun logToConsole(type: LogType, tag: String, msg: String, tr: Throwable? = null) {
        if (canLogToConsole(type)) {
            if (tr != null) {
                when (type) {
                    LogType.I -> android.util.Log.i(tag, msg, tr)
                    LogType.D -> android.util.Log.d(tag, msg, tr)
                    LogType.V -> android.util.Log.v(tag, msg, tr)
                    LogType.E -> android.util.Log.e(tag, msg, tr)
                    LogType.W -> android.util.Log.w(tag, msg, tr)
                    LogType.WTF -> android.util.Log.wtf(tag, msg, tr)
                }
            } else {
                when (type) {
                    LogType.I -> android.util.Log.i(tag, msg)
                    LogType.D -> android.util.Log.d(tag, msg)
                    LogType.V -> android.util.Log.v(tag, msg)
                    LogType.E -> android.util.Log.e(tag, msg)
                    LogType.W -> android.util.Log.w(tag, msg)
                    LogType.WTF -> android.util.Log.wtf(tag, msg)
                }
            }
        }
    }

    protected fun reportInternal(type: LogType, tag: String, msg: String, tr: Throwable? = null, forceReport: Boolean = false) {
        if (forceReport || shouldReport(type)) {
            if (tr == null) {
                report(type, tag, msg)
            } else {
                if(shouldReportException(tr)) {
                    report(type, tag, msg, tr)
                }
            }
        }
    }

    /**
     * check the [tr] exception to decide if it should be reported.
     *
     * This can be used to ignore certain types of exceptions.
     */
    protected abstract fun shouldReportException(tr: Throwable): Boolean

    protected fun writeToFileInternal(timestamp: Long, type: LogType, tag: String, msg: String, tr: Throwable? = null) {
        if(shouldLogToFile(type)) {
            writeLogToFile(timestamp, type, tag, msg, tr)
        }
    }

    /**
     * Implement the actual reporting.
     *
     * @param type [Int] @[LogType], the type of log this came from.
     * @param tag [Class] The Log tag
     * @param msg [String] The Log message.
     */
    protected abstract fun report(type: LogType, tag: String, msg: String)

    /**
     * Implement the actual reporting.
     *
     * @param type [Int] @[LogType], the type of log this came from.
     * @param tag [Class] The Log tag
     * @param msg [String] The Log message.
     * @param tr  [Throwable] to be attached to the Log.
     */
    protected abstract fun report(type: LogType, tag: String, msg: String, tr: Throwable)

    /**
     * Used to determine if we should Lod to the console or not.
     */
    protected abstract fun canLogToConsole(type: LogType): Boolean

    /**
     * Used to determine if we should send a report (to Crashlytics or equivalent)
     */
    protected abstract fun shouldReport(type: LogType): Boolean

    /**
     * Used to determine if we should call [writeLogToFile]
     */
    protected open fun shouldLogToFile(type: LogType): Boolean {
        return false
    }

    /**
     * Implement writing of the Log to your file.
     * This will be called if [shouldLogToFile] returns true.
     *
     * @param timestamp [Long] The timestamp of the log.
     * @param type [Int] @[LogType], the type of log this came from.
     * @param tag [Class] The Log tag
     * @param msg [String] The Log message.
     * @param tr  [Throwable] to be attached to the Log.
     */
    protected open fun writeLogToFile(timestamp: Long, type: LogType, tag: String, msg: String, tr: Throwable?) {
        //does nothing - override to implement writing to file
    }

    companion object {
        protected const val CLASS = "class: "
    }
}
