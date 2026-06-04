package com.duck.flexilogger.ktor

import com.duck.flexilogger.FlexiLog
import com.duck.flexilogger.LogType
import com.duck.flexilogger.LoggingLevel
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FlexiLoggerPluginTest {

    private class TestLogger : FlexiLog() {
        val messages = mutableListOf<String>()

        override fun canLogToConsole(type: LogType): Boolean = false
        override fun shouldReport(type: LogType): Boolean = false
        override fun shouldReportException(tr: Throwable): Boolean = false
        override fun report(type: LogType, tag: String, msg: String) {}
        override fun report(type: LogType, tag: String, msg: String, tr: Throwable) {}

        override fun logToConsole(type: LogType, tag: String, msg: String, tr: Throwable?) {
            messages.add(msg)
        }
    }

    private fun clientWith(
        logger: FlexiLog?,
        configure: FlexiLoggerConfig.() -> Unit = {}
    ) = HttpClient(MockEngine) {
        engine {
            addHandler {
                respond(
                    content = "OK",
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "text/plain")
                )
            }
        }
        install(FlexiLoggerPlugin) {
            this.logger = logger
            this.level = LoggingLevel.D
            configure()
        }
    }

    @Test
    fun shouldLogRequestAndResponseLines() = runTest {
        val base = TestLogger()
        val client = clientWith(base)

        client.get("https://example.com/users")

        assertTrue(base.messages.any { it.startsWith("--> GET") && it.contains("example.com/users") })
        assertTrue(base.messages.any { it == "--> END GET" })
        assertTrue(base.messages.any { it.startsWith("<-- 200") })
        assertTrue(base.messages.any { it == "<-- END HTTP (GET)" })
    }

    @Test
    fun shouldRedactSanitizedHeaders() = runTest {
        val base = TestLogger()
        val client = clientWith(base)

        client.get("https://example.com/secure") {
            header("Authorization", "super-secret-token")
        }

        assertFalse(
            base.messages.any { it.contains("super-secret-token") },
            "Authorization value should never be logged in clear text"
        )
        assertTrue(base.messages.any { it.contains("Authorization") && it.contains("***") })
    }

    @Test
    fun shouldNotLogHeadersWhenDisabled() = runTest {
        val base = TestLogger()
        val client = clientWith(base) { logHeaders = false }

        client.get("https://example.com/users") {
            header("X-Custom", "value")
        }

        assertFalse(base.messages.any { it.startsWith("X-Custom") })
        // The request/response summary lines should still be present.
        assertTrue(base.messages.any { it.startsWith("--> GET") })
    }

    @Test
    fun shouldUseConfiguredTag() = runTest {
        var capturedTag: String? = null
        val base = object : FlexiLog() {
            override fun canLogToConsole(type: LogType): Boolean = false
            override fun shouldReport(type: LogType): Boolean = false
            override fun shouldReportException(tr: Throwable): Boolean = false
            override fun report(type: LogType, tag: String, msg: String) {}
            override fun report(type: LogType, tag: String, msg: String, tr: Throwable) {}
            override fun logToConsole(type: LogType, tag: String, msg: String, tr: Throwable?) {
                capturedTag = tag
            }
        }
        val client = clientWith(base) { tag = "NETWORK" }

        client.get("https://example.com/users")

        assertEquals("NETWORK", capturedTag)
    }

    @Test
    fun shouldBeNoOpWhenNoLoggerConfigured() = runTest {
        // No logger -> plugin short-circuits; the request must still succeed.
        val client = clientWith(logger = null)

        val response = client.get("https://example.com/users")

        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun installFlexiLoggerHelperShouldWire() = runTest {
        val base = TestLogger()
        val client = HttpClient(MockEngine) {
            engine {
                addHandler {
                    respond(
                        content = "OK",
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "text/plain")
                    )
                }
            }
            installFlexiLogger(base, LoggingLevel.D, tag = "HTTP")
        }

        client.get("https://example.com/users")

        assertTrue(base.messages.any { it.startsWith("--> GET") })
        assertTrue(base.messages.any { it.startsWith("<-- 200") })
    }
}
