package com.duck.flexilogger.okhttp

import com.duck.flexilogger.FlexiLog
import com.duck.flexilogger.LogType
import com.duck.flexilogger.LoggerWithLevel
import com.duck.flexilogger.LoggingLevel
import kotlin.test.Test
import kotlin.test.assertEquals

class FlexiLogHttpLoggingInterceptorLoggerTest {

    private class TestLogger : FlexiLog() {
        data class Entry(val type: LogType, val tag: String, val msg: String)
        val entries = mutableListOf<Entry>()

        override fun canLogToConsole(type: LogType): Boolean = false
        override fun shouldReport(type: LogType): Boolean = false
        override fun shouldReportException(tr: Throwable): Boolean = false
        override fun report(type: LogType, tag: String, msg: String) {}
        override fun report(type: LogType, tag: String, msg: String, tr: Throwable) {}

        override fun logToConsole(type: LogType, tag: String, msg: String, tr: Throwable?) {
            entries.add(Entry(type, tag, msg))
        }
    }

    @Test
    fun logShouldRouteToInfoWithDefaultTag() {
        val base = TestLogger()
        val interceptorLogger = FlexiLogHttpLoggingInterceptorLogger.with(base, LoggingLevel.D)

        interceptorLogger.log("GET /users 200")

        assertEquals(1, base.entries.size)
        assertEquals(LogType.I, base.entries[0].type)
        assertEquals("HttpLoggingInterceptor", base.entries[0].tag)
        assertEquals("GET /users 200", base.entries[0].msg)
    }

    @Test
    fun blankMessagesShouldBeSkipped() {
        val base = TestLogger()
        val interceptorLogger = FlexiLogHttpLoggingInterceptorLogger.with(base, LoggingLevel.D)

        interceptorLogger.log("")
        interceptorLogger.log("   ")

        assertEquals(0, base.entries.size)
    }

    @Test
    fun loggingLevelShouldFilterOutInfoWhenBelowThreshold() {
        val base = TestLogger()
        // E level cannot log INFO, so nothing should reach the console.
        val interceptorLogger = FlexiLogHttpLoggingInterceptorLogger.with(base, LoggingLevel.E)

        interceptorLogger.log("should be filtered")

        assertEquals(0, base.entries.size)
    }

    @Test
    fun customTagOverloadShouldBeUsed() {
        val base = TestLogger()
        val interceptorLogger =
            FlexiLogHttpLoggingInterceptorLogger.with(base, LoggingLevel.D, tag = "API")

        interceptorLogger.log("payload")

        assertEquals(1, base.entries.size)
        assertEquals("API", base.entries[0].tag)
    }

    @Test
    fun loggerWithLevelOverloadShouldWork() {
        val base = TestLogger()
        val interceptorLogger =
            FlexiLogHttpLoggingInterceptorLogger.with(LoggerWithLevel(LoggingLevel.V, base))

        interceptorLogger.log("verbose-context message")

        assertEquals(1, base.entries.size)
        assertEquals(LogType.I, base.entries[0].type)
    }

    @Test
    fun loggerWithLevelAndTagOverloadShouldWork() {
        val base = TestLogger()
        val interceptorLogger =
            FlexiLogHttpLoggingInterceptorLogger.with(LoggerWithLevel(LoggingLevel.V, base), tag = "NET")

        interceptorLogger.log("message")

        assertEquals(1, base.entries.size)
        assertEquals("NET", base.entries[0].tag)
    }
}
