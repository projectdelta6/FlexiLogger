package com.duck.flexilogger

import kotlin.test.Test
import kotlin.test.assertEquals

class CallSiteTest {

    @Test
    fun simpleClassNameShouldStripPackage() {
        val callSite = CallSite(
            className = "com.example.app.data.repo.DataRepo",
            methodName = "fetchData",
            fileName = "DataRepo.kt",
            lineNumber = 123
        )

        assertEquals("DataRepo", callSite.simpleClassName)
    }

    @Test
    fun simpleClassNameShouldHandleClassWithNoPackage() {
        val callSite = CallSite(
            className = "TopLevel",
            methodName = "run",
            fileName = "TopLevel.kt",
            lineNumber = 1
        )

        assertEquals("TopLevel", callSite.simpleClassName)
    }

    @Test
    fun toFormattedStringShouldIncludeFileAndLineWhenBothPresent() {
        val callSite = CallSite(
            className = "com.example.app.data.repo.DataRepo",
            methodName = "fetchData",
            fileName = "DataRepo.kt",
            lineNumber = 123
        )

        assertEquals("DataRepo.fetchData(DataRepo.kt:123)", callSite.toFormattedString())
    }

    @Test
    fun toFormattedStringShouldOmitLineWhenNotPositive() {
        val callSite = CallSite(
            className = "com.example.app.data.repo.DataRepo",
            methodName = "fetchData",
            fileName = "DataRepo.kt",
            lineNumber = -1
        )

        assertEquals("DataRepo.fetchData(DataRepo.kt)", callSite.toFormattedString())
    }

    @Test
    fun toFormattedStringShouldOmitLocationWhenFileNameNull() {
        val callSite = CallSite(
            className = "com.example.app.data.repo.DataRepo",
            methodName = "fetchData",
            fileName = null,
            lineNumber = 123
        )

        assertEquals("DataRepo.fetchData", callSite.toFormattedString())
    }

    @Test
    fun toStringShouldMatchFormattedString() {
        val callSite = CallSite(
            className = "com.example.app.data.repo.DataRepo",
            methodName = "fetchData",
            fileName = "DataRepo.kt",
            lineNumber = 123
        )

        assertEquals(callSite.toFormattedString(), callSite.toString())
    }
}
