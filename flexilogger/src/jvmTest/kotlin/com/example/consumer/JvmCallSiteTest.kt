package com.example.consumer

import com.duck.flexilogger.CallSite
import com.duck.flexilogger.FlexiLog
import com.duck.flexilogger.LogType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Exercises the JVM `captureCallSite` actual via the public log API.
 *
 * Deliberately placed in `com.example.consumer` (NOT `com.duck.flexilogger`) so
 * the stack-walk treats this class as the genuine caller rather than skipping it
 * as a FlexiLogger-internal frame.
 */
class JvmCallSiteTest {

    private open class CapturingLog : FlexiLog() {
        var captured: CallSite? = null
        var callSiteReceived = false

        override fun canLogToConsole(type: LogType): Boolean = false
        override fun shouldReport(type: LogType): Boolean = true
        override fun shouldReportException(tr: Throwable): Boolean = true
        override fun report(type: LogType, tag: String, msg: String) {}
        override fun report(type: LogType, tag: String, msg: String, tr: Throwable) {}

        override fun report(type: LogType, tag: String, msg: String, callSite: CallSite?) {
            callSiteReceived = true
            captured = callSite
        }
    }

    @Test
    fun callSiteShouldPointAtTheConsumerCallNotFlexiLoggerInternals() {
        val log = CapturingLog()

        log.e("Tag", "trigger")

        assertTrue(log.callSiteReceived)
        val callSite = assertNotNull(log.captured, "Call site should be captured on the JVM")
        assertEquals("com.example.consumer.JvmCallSiteTest", callSite.className)
        assertEquals("JvmCallSiteTest", callSite.simpleClassName)
        assertEquals("callSiteShouldPointAtTheConsumerCallNotFlexiLoggerInternals", callSite.methodName)
        assertEquals("JvmCallSiteTest.kt", callSite.fileName)
        assertTrue(callSite.lineNumber > 0)
    }

    @Test
    fun additionalSkipPackagesShouldBeHonoured() {
        val log = object : CapturingLog() {
            override fun getAdditionalSkipPackages(): List<String> =
                listOf("com.example.consumer.LogWrapper")
        }

        LogWrapper(log).logSomething()

        val callSite = assertNotNull(log.captured)
        // The wrapper frame is skipped, so the call site resolves to *this* test.
        assertEquals("com.example.consumer.JvmCallSiteTest", callSite.className)
    }
}

/** A wrapper whose fully-qualified name is added to the skip list, to prove it is excluded. */
private class LogWrapper(private val log: FlexiLog) {
    fun logSomething() {
        log.e("Tag", "via wrapper")
    }
}
