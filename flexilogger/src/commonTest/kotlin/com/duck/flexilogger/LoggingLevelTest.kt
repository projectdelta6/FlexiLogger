package com.duck.flexilogger

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LoggingLevelTest {

    @Test
    fun verboseLevelShouldAllowAllLogTypes() {
        val level = LoggingLevel.V
        assertTrue(level.canLog(LogType.V))
        assertTrue(level.canLog(LogType.D))
        assertTrue(level.canLog(LogType.I))
        assertTrue(level.canLog(LogType.W))
        assertTrue(level.canLog(LogType.E))
        assertTrue(level.canLog(LogType.WTF))
    }

    @Test
    fun infoLevelShouldAllowIWEWtfButNotVOrD() {
        val level = LoggingLevel.I
        assertFalse(level.canLog(LogType.V))
        assertFalse(level.canLog(LogType.D))
        assertTrue(level.canLog(LogType.I))
        assertTrue(level.canLog(LogType.W))
        assertTrue(level.canLog(LogType.E))
        assertTrue(level.canLog(LogType.WTF))
    }

    @Test
    fun debugLevelShouldAllowDIWEWtfButNotV() {
        val level = LoggingLevel.D
        assertFalse(level.canLog(LogType.V))
        assertTrue(level.canLog(LogType.D))
        assertTrue(level.canLog(LogType.I))
        assertTrue(level.canLog(LogType.W))
        assertTrue(level.canLog(LogType.E))
        assertTrue(level.canLog(LogType.WTF))
    }

    @Test
    fun warningLevelShouldAllowWEWtfButNotVDI() {
        val level = LoggingLevel.W
        assertFalse(level.canLog(LogType.V))
        assertFalse(level.canLog(LogType.D))
        assertFalse(level.canLog(LogType.I))
        assertTrue(level.canLog(LogType.W))
        assertTrue(level.canLog(LogType.E))
        assertTrue(level.canLog(LogType.WTF))
    }

    @Test
    fun errorLevelShouldAllowOnlyEAndWtf() {
        val level = LoggingLevel.E
        assertFalse(level.canLog(LogType.V))
        assertFalse(level.canLog(LogType.D))
        assertFalse(level.canLog(LogType.I))
        assertFalse(level.canLog(LogType.W))
        assertTrue(level.canLog(LogType.E))
        // WTF is at WARNING level (2), so E level (1) cannot log it
        assertFalse(level.canLog(LogType.WTF))
    }

    @Test
    fun noneLevelShouldNotAllowAnyLogTypes() {
        val level = LoggingLevel.NONE
        assertFalse(level.canLog(LogType.V))
        assertFalse(level.canLog(LogType.D))
        assertFalse(level.canLog(LogType.I))
        assertFalse(level.canLog(LogType.W))
        assertFalse(level.canLog(LogType.E))
        assertFalse(level.canLog(LogType.WTF))
    }

    @Test
    fun levelValuesShouldBeInDescendingOrderFromVToNone() {
        assertTrue(LoggingLevel.V.level > LoggingLevel.I.level)
        assertTrue(LoggingLevel.I.level > LoggingLevel.D.level)
        assertTrue(LoggingLevel.D.level > LoggingLevel.W.level)
        assertTrue(LoggingLevel.W.level > LoggingLevel.E.level)
        assertTrue(LoggingLevel.E.level > LoggingLevel.NONE.level)
    }
}
