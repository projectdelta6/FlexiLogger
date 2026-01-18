package com.duck.flexilogger.ktor

import com.duck.flexilogger.FlexiLog
import com.duck.flexilogger.LoggerWithLevel
import com.duck.flexilogger.LoggingLevel
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.api.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.util.*
import io.ktor.utils.io.*

/**
 * Configuration for the FlexiLogger Ktor plugin.
 */
class FlexiLoggerConfig {
    /**
     * The FlexiLog instance to use for logging.
     */
    var logger: FlexiLog? = null

    /**
     * The logging level to use. Defaults to DEBUG.
     */
    var level: LoggingLevel = LoggingLevel.D

    /**
     * The tag to use for HTTP logs. Defaults to "HTTP".
     */
    var tag: String = "HTTP"

    /**
     * Whether to log request headers. Defaults to true.
     */
    var logHeaders: Boolean = true

    /**
     * Whether to log request/response bodies. Defaults to false.
     * Note: Enabling this may impact performance and memory usage.
     */
    var logBody: Boolean = false

    /**
     * Headers to redact from logs (e.g., Authorization).
     */
    var sanitizedHeaders: Set<String> = setOf("Authorization", "Cookie", "Set-Cookie")

    internal fun createLoggerWithLevel(): LoggerWithLevel? {
        return logger?.let { LoggerWithLevel(level, it) }
    }
}

/**
 * FlexiLogger plugin for Ktor HTTP client.
 *
 * Usage:
 * ```kotlin
 * val client = HttpClient {
 *     install(FlexiLoggerPlugin) {
 *         logger = MyFlexiLog
 *         level = LoggingLevel.D
 *         logHeaders = true
 *         logBody = false
 *     }
 * }
 * ```
 */
val FlexiLoggerPlugin = createClientPlugin("FlexiLogger", ::FlexiLoggerConfig) {
    val loggerWithLevel = pluginConfig.createLoggerWithLevel()
        ?: return@createClientPlugin // No logger configured, plugin is a no-op

    val tag = pluginConfig.tag
    val logHeaders = pluginConfig.logHeaders
    val logBody = pluginConfig.logBody
    val sanitizedHeaders = pluginConfig.sanitizedHeaders.map { it.lowercase() }.toSet()

    fun sanitizeHeaderValue(name: String, value: String): String {
        return if (name.lowercase() in sanitizedHeaders) "***" else value
    }

    fun logRequest(request: HttpRequestBuilder) {
        val method = request.method.value
        val url = request.url.buildString()

        loggerWithLevel.d(tag, "--> $method $url")

        if (logHeaders) {
            request.headers.entries().forEach { (name, values) ->
                values.forEach { value ->
                    loggerWithLevel.d(tag, "$name: ${sanitizeHeaderValue(name, value)}")
                }
            }
        }

        if (logBody) {
            val body = request.body
            when (body) {
                is TextContent -> loggerWithLevel.d(tag, "Body: ${body.text}")
                is OutgoingContent -> loggerWithLevel.d(tag, "Body: [${body.contentType}]")
                else -> loggerWithLevel.d(tag, "Body: $body")
            }
        }

        loggerWithLevel.d(tag, "--> END $method")
    }

    suspend fun logResponse(response: HttpResponse) {
        val code = response.status.value
        val message = response.status.description
        val url = response.request.url.toString()
        val method = response.request.method.value
        val duration = response.responseTime.timestamp - response.requestTime.timestamp

        loggerWithLevel.d(tag, "<-- $code $message ${duration}ms $url")

        if (logHeaders) {
            response.headers.entries().forEach { (name, values) ->
                values.forEach { value ->
                    loggerWithLevel.d(tag, "$name: ${sanitizeHeaderValue(name, value)}")
                }
            }
        }

        loggerWithLevel.d(tag, "<-- END HTTP ($method)")
    }

    onRequest { request, _ ->
        logRequest(request)
    }

    onResponse { response ->
        logResponse(response)
    }
}

/**
 * Installs FlexiLogger plugin with a simple configuration.
 *
 * Usage:
 * ```kotlin
 * val client = HttpClient {
 *     installFlexiLogger(MyFlexiLog, LoggingLevel.D)
 * }
 * ```
 */
fun HttpClientConfig<*>.installFlexiLogger(
    logger: FlexiLog,
    level: LoggingLevel = LoggingLevel.D,
    tag: String = "HTTP",
    logHeaders: Boolean = true,
    logBody: Boolean = false
) {
    install(FlexiLoggerPlugin) {
        this.logger = logger
        this.level = level
        this.tag = tag
        this.logHeaders = logHeaders
        this.logBody = logBody
    }
}
