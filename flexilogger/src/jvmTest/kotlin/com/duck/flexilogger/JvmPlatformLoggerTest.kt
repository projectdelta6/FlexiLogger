package com.duck.flexilogger

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Exercises the JVM `platformLog` actual (java.util.logging level mapping) by
 * enabling console logging. Kept in jvmTest — running this in commonTest would
 * route to `android.util.Log` under the Android host test, which is not mocked
 * and throws.
 */
class JvmPlatformLoggerTest {

    private class ConsoleLog : FlexiLog() {
        var consoleCount = 0
        override fun canLogToConsole(type: LogType): Boolean = true
        override fun shouldReport(type: LogType): Boolean = false
        override fun shouldReportException(tr: Throwable): Boolean = false
        override fun report(type: LogType, tag: String, msg: String) {}
        override fun report(type: LogType, tag: String, msg: String, tr: Throwable) {}
        override fun logToConsole(type: LogType, tag: String, msg: String, tr: Throwable?) {
            consoleCount++
            super.logToConsole(type, tag, msg, tr) // -> platformLog (java.util.logging)
        }
    }

    @Test
    fun allLogTypesShouldRouteThroughPlatformLogger() {
        val log = ConsoleLog()

        log.v("Tag", "verbose")
        log.d("Tag", "debug")
        log.i("Tag", "info")
        log.w("Tag", "warning")
        log.e("Tag", "error")
        log.wtf("Tag", "wtf")

        assertEquals(6, log.consoleCount)
    }

    @Test
    fun platformLoggerShouldHandleThrowableBranch() {
        val log = ConsoleLog()

        log.e("Tag", "boom", RuntimeException("with throwable"))
        log.i("Tag", "clean") // no throwable

        assertEquals(2, log.consoleCount)
    }
}
