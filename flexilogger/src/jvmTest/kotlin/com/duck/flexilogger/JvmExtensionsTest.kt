package com.duck.flexilogger

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Covers the JVM-only `Class<*>` convenience overloads and the platform
 * `getSimpleClassName` actuals.
 */
class JvmExtensionsTest {

    private class TagCapturingLog : FlexiLog() {
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

    private class SampleClass

    @Test
    @Suppress("DEPRECATION") // Verifying the deprecated helper still behaves until it is removed.
    fun getClassNameFromClassShouldReturnSimpleName() {
        val log = TagCapturingLog()
        assertEquals("SampleClass", log.getClassName(SampleClass::class.java))
    }

    @Test
    fun classArgumentShouldResolveToRepresentedClassName() {
        // Regression guard: the member `Any` overload shadows the `Class<*>` extension,
        // so a Kotlin caller passing a Class lands in `log.d(caller: Any)`. That path
        // must still tag with the represented class name ("SampleClass"), not "Class".
        val log = TagCapturingLog()

        log.v(SampleClass::class.java, "v")
        log.d(SampleClass::class.java, "d")
        log.i(SampleClass::class.java, "i")
        log.w(SampleClass::class.java, "w")
        log.e(SampleClass::class.java, "e")
        log.wtf(SampleClass::class.java, "wtf")

        assertEquals(6, log.entries.size)
        assertEquals(listOf("SampleClass"), log.entries.map { it.tag }.distinct())
        assertEquals(
            listOf(LogType.V, LogType.D, LogType.I, LogType.W, LogType.E, LogType.WTF),
            log.entries.map { it.type }
        )
    }

    @Test
    fun kClassArgumentShouldResolveToRepresentedClassName() {
        val log = TagCapturingLog()

        log.d(SampleClass::class, "msg")

        assertEquals(1, log.entries.size)
        assertEquals("SampleClass", log.entries[0].tag)
    }

    @Test
    fun classArgumentShouldForwardThrowable() {
        val log = TagCapturingLog()
        val exception = IllegalStateException("kaboom")

        log.e(SampleClass::class.java, "broke", exception)

        assertEquals(1, log.entries.size)
        assertEquals("SampleClass", log.entries[0].tag)
        assertEquals(LogType.E, log.entries[0].type)
    }

    @Test
    fun anyOverloadShouldForwardTypeAndThrowable() {
        val log = TagCapturingLog()
        val exception = IllegalStateException("kaboom")

        log.e(SampleClass(), "broke", exception)

        assertEquals(1, log.entries.size)
        assertEquals("SampleClass", log.entries[0].tag)
        assertEquals(LogType.E, log.entries[0].type)
    }

    @Test
    fun getSimpleClassNameFromObjectShouldStripPackage() {
        assertEquals("SampleClass", getSimpleClassName(SampleClass()))
    }

    @Test
    fun getSimpleClassNameFromKClassShouldStripPackage() {
        assertEquals("SampleClass", getSimpleClassName(SampleClass::class))
    }
}
