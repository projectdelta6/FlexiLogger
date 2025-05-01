package com.duck.app

import com.duck.flexilogger.FlexiLog
import com.duck.flexilogger.LogType
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlin.coroutines.cancellation.CancellationException

object Log: FlexiLog() {
    /**
     * Used to determine if we should Lod to the console or not.
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
     * Implement the actual reporting.
     *
     * @param type [Int] @[LogType], the type of log this came from.
     * @param tag [Class] The Log tag
     * @param msg [String] The Log message.
     */
    override fun report(type: LogType, tag: String, msg: String) {
        /* nothing */
    }

    /**
     * Implement the actual reporting.
     *
     * @param type [Int] @[LogType], the type of log this came from.
     * @param tag [Class] The Log tag
     * @param msg [String] The Log message.
     * @param tr  [Throwable] to be attached to the Log.
     */
    override fun report(type: LogType, tag: String, msg: String, tr: Throwable) {

    }

    override fun shouldLogToFile(type: LogType): Boolean {
        return false
    }

    override fun writeLogToFile(timestamp: Long, type: LogType, tag: String, msg: String, tr: Throwable?) {
        /* nothing */
    }
}