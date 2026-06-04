package com.duck.flexilogger

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

/**
 * Covers the reporting filters, file-logging hooks, call-site delegation and
 * the suspend conditional helper — branches not exercised by [FlexiLogTest].
 */
class FlexiLogReportingTest {

    /**
     * Highly configurable test logger that records every hook invocation so the
     * branching in [FlexiLog] can be asserted.
     */
    private class TestLogger(
        private val reportException: Boolean = true,
        private val logToFile: Boolean = false,
        private val reportTypes: Set<LogType> = setOf(LogType.E)
    ) : FlexiLog() {
        data class Reported(val type: LogType, val tag: String, val msg: String, val tr: Throwable?)
        data class FileWrite(val timestamp: Long, val type: LogType, val tag: String, val msg: String, val tr: Throwable?)

        val reported = mutableListOf<Reported>()
        val fileWrites = mutableListOf<FileWrite>()
        var lastCallSiteSeen: CallSite? = null
        var callSiteOverloadInvoked = false

        override fun canLogToConsole(type: LogType): Boolean = false
        override fun shouldReport(type: LogType): Boolean = type in reportTypes
        override fun shouldReportException(tr: Throwable): Boolean = reportException
        override fun shouldLogToFile(type: LogType): Boolean = logToFile

        override fun report(type: LogType, tag: String, msg: String) {
            reported.add(Reported(type, tag, msg, null))
        }

        override fun report(type: LogType, tag: String, msg: String, tr: Throwable) {
            reported.add(Reported(type, tag, msg, tr))
        }

        override fun report(type: LogType, tag: String, msg: String, callSite: CallSite?) {
            callSiteOverloadInvoked = true
            lastCallSiteSeen = callSite
            super.report(type, tag, msg, callSite)
        }

        override fun writeLogToFile(timestamp: Long, type: LogType, tag: String, msg: String, tr: Throwable?) {
            fileWrites.add(FileWrite(timestamp, type, tag, msg, tr))
        }
    }

    @Test
    fun exceptionShouldNotBeReportedWhenShouldReportExceptionIsFalse() {
        val logger = TestLogger(reportException = false)

        logger.e("Tag", "boom", RuntimeException("nope"))

        assertEquals(0, logger.reported.size)
    }

    @Test
    fun exceptionShouldBeReportedWhenShouldReportExceptionIsTrue() {
        val logger = TestLogger(reportException = true)
        val exception = RuntimeException("yep")

        logger.e("Tag", "boom", exception)

        assertEquals(1, logger.reported.size)
        assertSame(exception, logger.reported[0].tr)
    }

    @Test
    fun forceReportShouldBypassShouldReport() {
        // shouldReport returns false for everything here.
        val logger = TestLogger(reportTypes = emptySet())

        logger.e("Tag", "not forced")
        assertEquals(0, logger.reported.size)

        logger.e("Tag", "forced", forceReport = true)
        assertEquals(1, logger.reported.size)
        assertEquals(LogType.E, logger.reported[0].type)
    }

    @Test
    fun forceReportStillRespectsShouldReportException() {
        // forceReport bypasses shouldReport, but the throwable filter still applies.
        val logger = TestLogger(reportException = false, reportTypes = emptySet())

        logger.e("Tag", "msg", RuntimeException("filtered"), forceReport = true)

        assertEquals(0, logger.reported.size)
    }

    @Test
    fun callSiteOverloadShouldBeInvokedForReportedLogs() {
        val logger = TestLogger()

        logger.e("Tag", "boom")

        assertTrue(logger.callSiteOverloadInvoked)
        assertEquals(1, logger.reported.size)
    }

    @Test
    fun fileShouldNotBeWrittenByDefault() {
        val logger = TestLogger(logToFile = false)

        logger.d("Tag", "msg")

        assertEquals(0, logger.fileWrites.size)
    }

    @Test
    fun fileShouldBeWrittenWhenShouldLogToFileIsTrue() {
        val logger = TestLogger(logToFile = true)
        val exception = RuntimeException("disk")

        logger.w("Tag", "to disk", exception)

        assertEquals(1, logger.fileWrites.size)
        val write = logger.fileWrites[0]
        assertEquals(LogType.W, write.type)
        assertEquals("Tag", write.tag)
        assertEquals("to disk", write.msg)
        assertSame(exception, write.tr)
        assertTrue(write.timestamp > 0)
    }

    @Test
    fun fileWriteShouldReceiveEachChunkOfLongMessages() {
        val logger = TestLogger(logToFile = true)
        val longMessage = "x".repeat(5000) // 2 chunks

        logger.d("Tag", longMessage)

        // Reporting/file-writing happens on the full message, not per-chunk.
        assertEquals(1, logger.fileWrites.size)
        assertEquals(5000, logger.fileWrites[0].msg.length)
    }

    @Test
    fun onConditionSuspendShouldOnlyLogWhenConditionIsTrue() = runTest {
        val logger = TestLogger()

        logger.onConditionSuspend(false) { it.e("Tag", "should not report") }
        assertEquals(0, logger.reported.size)

        logger.onConditionSuspend(true) { it.e("Tag", "should report") }
        assertEquals(1, logger.reported.size)
    }

    @Test
    fun debugLogShouldNeverHitCallSiteOverloadWhenNotReported() {
        val logger = TestLogger()

        logger.d("Tag", "not reported")

        assertFalse(logger.callSiteOverloadInvoked)
        assertNull(logger.lastCallSiteSeen)
    }
}
