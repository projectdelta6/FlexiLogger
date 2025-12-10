package com.duck.flexilogger

class LoggerWithLevel(
    private var level: LoggingLevel,
    private var logger: FlexiLog
) {
    /**
     * Update the logger implementation and/or the logging level.
     *
     * @param newLogger [FlexiLog] The new logger implementation. Default is the current logger.
     * @param newLevel  [LoggingLevel] The new logging level. Default is the current level.
     */
    @JvmOverloads
    fun updateLogger(
        newLogger: FlexiLog = logger,
        newLevel: LoggingLevel = level
    ) {
        this.logger = newLogger
        this.level = newLevel
    }

    /**
     * Check if a LogType can be logged based on the current LoggingLevel.
     *
     * @param logType [LogType] The LogType to check.
     * @return [Boolean] True if the LogType can be logged, False otherwise.
     */
    fun canLog(logType: LogType): Boolean =
        level.canLog(logType)

    /**
     * Calls [i].
     *
     * @param caller [Any] The caller of this method. This will be used to get the Class name of the Object.
     * @param msg    [String] The Message to be Logged.
     * @param tr     [Throwable] to be attached to the Log.
     */
    @JvmOverloads
    fun i(caller: Any, msg: String? = null, tr: Throwable? = null) {
        if(level.canLog(LogType.I)) logger.i(caller, msg, tr)
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
        if(level.canLog(LogType.I)) logger.i(caller, msg, tr)
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
        if(level.canLog(LogType.I)) logger.i(tag, msg, tr)
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
        if(level.canLog(LogType.D)) logger.d(caller, msg, tr)
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
        if(level.canLog(LogType.D)) logger.d(caller, msg, tr)
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
        if(level.canLog(LogType.D)) logger.d(tag, msg, tr)
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
        if(level.canLog(LogType.V)) logger.v(caller, msg, tr)
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
        if(level.canLog(LogType.V)) logger.v(caller, msg, tr)
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
        if(level.canLog(LogType.V)) logger.v(tag, msg, tr)
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
        if(level.canLog(LogType.E)) logger.e(caller, msg, tr, forceReport)
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
        if(level.canLog(LogType.E)) logger.e(caller, msg, tr, forceReport)
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
        if(level.canLog(LogType.E)) logger.e(tag, msg, tr, forceReport)
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
        if(level.canLog(LogType.W)) logger.w(caller, msg, tr)
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
        if(level.canLog(LogType.W)) logger.w(caller, msg, tr)
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
        if(level.canLog(LogType.W)) logger.w(tag, msg, tr)
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
        if(level.canLog(LogType.WTF)) logger.wtf(caller, msg, tr)
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
        if(level.canLog(LogType.WTF)) logger.wtf(caller, msg, tr)
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
        if(level.canLog(LogType.WTF)) logger.wtf(tag, msg, tr)
    }
}