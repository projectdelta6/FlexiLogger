package com.duck.flexiloggerTestApp

import com.duck.flexilogger.CallSite
import com.duck.flexilogger.FlexiLog
import com.duck.flexilogger.LogType
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlin.coroutines.cancellation.CancellationException

object Log : FlexiLog() {
    /**
     * Used to determine if we should log to the console or not.
     */
    override fun canLogToConsole(type: LogType): Boolean {
        return true
    }

    /**
     * Used to determine if we should send a report (to Crashlytics or equivalent)
     */
    override fun shouldReport(type: LogType): Boolean {
        return false
    }

    override fun shouldReportException(tr: Throwable): Boolean {
        return when (tr) {
            is CancellationException,
            is UnknownHostException,
            is ConnectException,
            is SocketException,
            is SocketTimeoutException -> false
            else -> true
        }
    }

    /**
     * Implement the actual reporting (without call site info).
     *
     * Note: For call site information, override [report] with [CallSite] parameter instead.
     *
     * @param type [LogType], the type of log this came from.
     * @param tag [String] The Log tag
     * @param msg [String] The Log message.
     */
    override fun report(type: LogType, tag: String, msg: String) {
        // Example: Firebase Crashlytics
        // FirebaseCrashlytics.getInstance().log("[$type] $tag: $msg")
        //
        // Example: Sentry (basic)
        // Sentry.captureMessage("[$tag] $msg", when (type) {
        //     LogType.E, LogType.WTF -> SentryLevel.ERROR
        //     LogType.W -> SentryLevel.WARNING
        //     else -> SentryLevel.INFO
        // })
    }

    /**
     * Implement the actual reporting with a throwable (without call site info).
     *
     * Note: For call site information, override [report] with [CallSite] parameter instead.
     *
     * @param type [LogType], the type of log this came from.
     * @param tag [String] The Log tag
     * @param msg [String] The Log message.
     * @param tr  [Throwable] to be attached to the Log.
     */
    override fun report(type: LogType, tag: String, msg: String, tr: Throwable) {
        // Example: Firebase Crashlytics
        // FirebaseCrashlytics.getInstance().apply {
        //     log("[$type] $tag: $msg")
        //     recordException(tr)
        // }
        //
        // Example: Sentry (basic)
        // Sentry.captureException(tr) { scope ->
        //     scope.setTag("log_tag", tag)
        //     scope.setExtra("log_message", msg)
        // }
    }

    /**
     * Handle crash report with call site information.
     *
     * The [callSite] parameter provides the actual location where the log was called,
     * which is useful for crash reporting tools to show the correct source location
     * instead of FlexiLogger internals.
     *
     * @param type [LogType], the type of log this came from.
     * @param tag [String] The Log tag
     * @param msg [String] The Log message.
     * @param callSite [CallSite] The location where the log call was made, or null if unavailable.
     */
    override fun report(type: LogType, tag: String, msg: String, callSite: CallSite?) {
        // Example: Sentry with call site for accurate error location
        // Sentry.captureEvent(SentryEvent().apply {
        //     message = Message().apply { message = msg }
        //     logger = tag
        //     level = when (type) {
        //         LogType.E, LogType.WTF -> SentryLevel.ERROR
        //         LogType.W -> SentryLevel.WARNING
        //         else -> SentryLevel.INFO
        //     }
        //     // Use callSite to show actual source location in Sentry dashboard
        //     callSite?.let { site ->
        //         exceptions = listOf(
        //             SentryException().apply {
        //                 this.type = "LoggedError"
        //                 this.value = msg
        //                 this.module = site.className
        //                 this.stacktrace = SentryStackTrace(listOf(
        //                     SentryStackFrame().apply {
        //                         module = site.className
        //                         function = site.methodName
        //                         filename = site.fileName
        //                         lineno = site.lineNumber
        //                         isInApp = true
        //                     }
        //                 ))
        //             }
        //         )
        //     }
        // })

        // For demo: log the call site to console
        callSite?.let {
            android.util.Log.d("FlexiLogger-CallSite", "Report from: ${it.toFormattedString()}")
        }
    }

    /**
     * Handle crash report with a throwable and call site information.
     *
     * @param type [LogType], the type of log this came from.
     * @param tag [String] The Log tag
     * @param msg [String] The Log message.
     * @param tr  [Throwable] to be attached to the Log.
     * @param callSite [CallSite] The location where the log call was made, or null if unavailable.
     */
    override fun report(type: LogType, tag: String, msg: String, tr: Throwable, callSite: CallSite?) {
        // Example: Sentry with throwable and call site context
        // Sentry.captureEvent(SentryEvent().apply {
        //     message = Message().apply { message = msg }
        //     logger = tag
        //     throwable = tr
        //     // Add call site as additional context when there's already a throwable
        //     callSite?.let { site ->
        //         setTag("log_call_site", "${site.simpleClassName}.${site.methodName}")
        //         setExtra("log_location", site.toFormattedString())
        //         setExtra("log_class", site.className)
        //         setExtra("log_method", site.methodName)
        //         setExtra("log_file", site.fileName)
        //         setExtra("log_line", site.lineNumber)
        //     }
        // })

        // For demo: log the call site to console
        callSite?.let {
            android.util.Log.d("FlexiLogger-CallSite", "Report (with exception) from: ${it.toFormattedString()}")
        }
    }

    /**
     * Override to skip additional packages when determining call site.
     *
     * FlexiLogger automatically skips its own packages and any class extending [FlexiLog].
     * Use this to skip your own wrapper classes if needed.
     */
    override fun getAdditionalSkipPackages(): List<String> {
        // Example: Skip custom wrapper classes
        // return listOf(
        //     "com.myapp.util.LogWrapper",
        //     "com.myapp.analytics.EventLogger"
        // )
        return emptyList()
    }

    override fun shouldLogToFile(type: LogType): Boolean {
        return false
    }

    override fun writeLogToFile(timestamp: Long, type: LogType, tag: String, msg: String, tr: Throwable?) {
        // Example: Write to a file
        // val logLine = "${Date(timestamp)} [$type] $tag: $msg${tr?.let { "\n${it.stackTraceToString()}" } ?: ""}"
        // File(context.filesDir, "app.log").appendText(logLine + "\n")
    }
}