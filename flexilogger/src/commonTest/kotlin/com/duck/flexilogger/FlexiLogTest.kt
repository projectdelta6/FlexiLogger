package com.duck.flexilogger

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FlexiLogTest {

    /**
     * Test implementation that captures logged messages
     */
    private class TestLogger : FlexiLog() {
        val loggedMessages = mutableListOf<LoggedMessage>()
        val reportedMessages = mutableListOf<LoggedMessage>()

        data class LoggedMessage(
            val type: LogType,
            val tag: String,
            val message: String,
            val throwable: Throwable? = null
        )

        override fun canLogToConsole(type: LogType): Boolean = true

        override fun shouldReport(type: LogType): Boolean = type == LogType.E

        override fun shouldReportException(tr: Throwable): Boolean = true

        override fun report(type: LogType, tag: String, msg: String) {
            reportedMessages.add(LoggedMessage(type, tag, msg))
        }

        override fun report(type: LogType, tag: String, msg: String, tr: Throwable) {
            reportedMessages.add(LoggedMessage(type, tag, msg, tr))
        }

        // Override to capture logged messages
        override fun logToConsole(type: LogType, tag: String, msg: String, tr: Throwable?) {
            loggedMessages.add(LoggedMessage(type, tag, msg, tr))
        }
    }

    @Test
    fun loggingWithTagShouldPassCorrectTypeAndTag() {
        val logger = TestLogger()
        logger.d("TestTag", "Test message")

        assertEquals(1, logger.loggedMessages.size)
        assertEquals(LogType.D, logger.loggedMessages[0].type)
        assertEquals("TestTag", logger.loggedMessages[0].tag)
        assertEquals("Test message", logger.loggedMessages[0].message)
    }

    @Test
    fun loggingWithObjectShouldExtractClassName() {
        val logger = TestLogger()
        logger.i(this, "Test message")

        assertEquals(1, logger.loggedMessages.size)
        assertEquals(LogType.I, logger.loggedMessages[0].type)
        assertEquals("FlexiLogTest", logger.loggedMessages[0].tag)
    }

    @Test
    fun loggingWithThrowableShouldIncludeThrowable() {
        val logger = TestLogger()
        val exception = RuntimeException("Test exception")
        logger.e("Tag", "Error message", exception)

        assertEquals(1, logger.loggedMessages.size)
        assertEquals(exception, logger.loggedMessages[0].throwable)
    }

    @Test
    fun nullMessageShouldBecomeEmptyString() {
        val logger = TestLogger()
        logger.d("Tag", null)

        assertEquals(1, logger.loggedMessages.size)
        assertEquals("", logger.loggedMessages[0].message)
    }

    @Test
    fun errorLogShouldTriggerReport() {
        val logger = TestLogger()
        logger.e("Tag", "Error message")

        assertEquals(1, logger.reportedMessages.size)
        assertEquals(LogType.E, logger.reportedMessages[0].type)
    }

    @Test
    fun debugLogShouldNotTriggerReport() {
        val logger = TestLogger()
        logger.d("Tag", "Debug message")

        assertEquals(0, logger.reportedMessages.size)
    }

    @Test
    fun forceReportShouldTriggerReportForAnyLogType() {
        val logger = TestLogger()
        logger.e("Tag", "Debug message", forceReport = true)

        assertEquals(1, logger.reportedMessages.size)
    }

    @Test
    fun longMessageShouldBeChunked() {
        val logger = TestLogger()
        val longMessage = "x".repeat(8500) // > 4000 chars = 3 chunks

        logger.d("Tag", longMessage)

        assertEquals(3, logger.loggedMessages.size)
        assertEquals(4000, logger.loggedMessages[0].message.length)
        assertEquals(4000, logger.loggedMessages[1].message.length)
        assertEquals(500, logger.loggedMessages[2].message.length)
    }

    @Test
    fun throwableShouldOnlyBeAttachedToLastChunk() {
        val logger = TestLogger()
        val longMessage = "x".repeat(5000) // 2 chunks
        val exception = RuntimeException("Test")

        logger.d("Tag", longMessage, exception)

        assertEquals(2, logger.loggedMessages.size)
        assertEquals(null, logger.loggedMessages[0].throwable)
        assertEquals(exception, logger.loggedMessages[1].throwable)
    }

    @Test
    fun onConditionShouldOnlyLogWhenConditionIsTrue() {
        val logger = TestLogger()

        logger.onCondition(false) { it.d("Tag", "Should not log") }
        assertEquals(0, logger.loggedMessages.size)

        logger.onCondition(true) { it.d("Tag", "Should log") }
        assertEquals(1, logger.loggedMessages.size)
    }

    @Test
    fun withLevelShouldReturnLoggerWithLevel() {
        val logger = TestLogger()
        val loggerWithLevel = logger.withLevel(LoggingLevel.D)

        assertTrue(loggerWithLevel is LoggerWithLevel)
    }

    @Test
    fun allLogTypesShouldWork() {
        val logger = TestLogger()

        logger.v("Tag", "verbose")
        logger.d("Tag", "debug")
        logger.i("Tag", "info")
        logger.w("Tag", "warning")
        logger.e("Tag", "error")
        logger.wtf("Tag", "wtf")

        assertEquals(6, logger.loggedMessages.size)
        assertEquals(LogType.V, logger.loggedMessages[0].type)
        assertEquals(LogType.D, logger.loggedMessages[1].type)
        assertEquals(LogType.I, logger.loggedMessages[2].type)
        assertEquals(LogType.W, logger.loggedMessages[3].type)
        assertEquals(LogType.E, logger.loggedMessages[4].type)
        assertEquals(LogType.WTF, logger.loggedMessages[5].type)
    }
}
