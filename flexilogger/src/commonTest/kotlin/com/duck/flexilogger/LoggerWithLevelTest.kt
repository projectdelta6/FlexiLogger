package com.duck.flexilogger

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LoggerWithLevelTest {

    private class TestLogger : FlexiLog() {
        val loggedMessages = mutableListOf<Pair<LogType, String>>()

        override fun canLogToConsole(type: LogType): Boolean = true
        override fun shouldReport(type: LogType): Boolean = false
        override fun shouldReportException(tr: Throwable): Boolean = false
        override fun report(type: LogType, tag: String, msg: String) {}
        override fun report(type: LogType, tag: String, msg: String, tr: Throwable) {}

        override fun logToConsole(type: LogType, tag: String, msg: String, tr: Throwable?) {
            loggedMessages.add(type to msg)
        }
    }

    @Test
    fun canLogShouldRespectLoggingLevel() {
        val baseLogger = TestLogger()
        val logger = LoggerWithLevel(LoggingLevel.W, baseLogger)

        assertFalse(logger.canLog(LogType.V))
        assertFalse(logger.canLog(LogType.D))
        assertFalse(logger.canLog(LogType.I))
        assertTrue(logger.canLog(LogType.W))
        assertTrue(logger.canLog(LogType.E))
    }

    @Test
    fun debugLogsShouldBeFilteredAtWarningLevel() {
        val baseLogger = TestLogger()
        val logger = LoggerWithLevel(LoggingLevel.W, baseLogger)

        logger.d("Tag", "debug message")
        logger.w("Tag", "warning message")

        assertEquals(1, baseLogger.loggedMessages.size)
        assertEquals(LogType.W, baseLogger.loggedMessages[0].first)
    }

    @Test
    fun allLogsShouldPassAtVerboseLevel() {
        val baseLogger = TestLogger()
        val logger = LoggerWithLevel(LoggingLevel.V, baseLogger)

        logger.v("Tag", "verbose")
        logger.d("Tag", "debug")
        logger.i("Tag", "info")
        logger.w("Tag", "warning")
        logger.e("Tag", "error")
        logger.wtf("Tag", "wtf")

        assertEquals(6, baseLogger.loggedMessages.size)
    }

    @Test
    fun noLogsShouldPassAtNoneLevel() {
        val baseLogger = TestLogger()
        val logger = LoggerWithLevel(LoggingLevel.NONE, baseLogger)

        logger.v("Tag", "verbose")
        logger.d("Tag", "debug")
        logger.i("Tag", "info")
        logger.w("Tag", "warning")
        logger.e("Tag", "error")
        logger.wtf("Tag", "wtf")

        assertEquals(0, baseLogger.loggedMessages.size)
    }

    @Test
    fun updateLoggerShouldChangeTheLevel() {
        val baseLogger = TestLogger()
        val logger = LoggerWithLevel(LoggingLevel.E, baseLogger)

        // At E level, only errors pass
        logger.d("Tag", "debug1")
        assertEquals(0, baseLogger.loggedMessages.size)

        // Update to D level
        logger.updateLogger(newLevel = LoggingLevel.D)

        // Now debug should pass
        logger.d("Tag", "debug2")
        assertEquals(1, baseLogger.loggedMessages.size)
    }

    @Test
    fun updateLoggerShouldChangeTheLogger() {
        val baseLogger1 = TestLogger()
        val baseLogger2 = TestLogger()
        val logger = LoggerWithLevel(LoggingLevel.V, baseLogger1)

        logger.d("Tag", "message1")
        assertEquals(1, baseLogger1.loggedMessages.size)
        assertEquals(0, baseLogger2.loggedMessages.size)

        logger.updateLogger(newLogger = baseLogger2)

        logger.d("Tag", "message2")
        assertEquals(1, baseLogger1.loggedMessages.size)
        assertEquals(1, baseLogger2.loggedMessages.size)
    }

    @Test
    fun loggingWithAnyCallerShouldWork() {
        val baseLogger = TestLogger()
        val logger = LoggerWithLevel(LoggingLevel.V, baseLogger)

        logger.i(this, "test")

        assertEquals(1, baseLogger.loggedMessages.size)
    }
}
