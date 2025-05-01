package com.duck.flexilogger.flexihttplogger

import com.duck.flexilogger.FlexiLog
import okhttp3.logging.HttpLoggingInterceptor

abstract class FlexiLogHttpLoggingInterceptorLogger : HttpLoggingInterceptor.Logger {
    abstract val logger: FlexiLog
    open val tag = "HttpLoggingInterceptor"

    override fun log(message: String) {
        if (message.isNotBlank()) {
            logger.i(tag, message)
        }
    }

    companion object {
        fun with(logger: FlexiLog): FlexiLogHttpLoggingInterceptorLogger =
            object : FlexiLogHttpLoggingInterceptorLogger() {
                override val logger: FlexiLog = logger
            }
        fun with(logger: FlexiLog, tag: String): FlexiLogHttpLoggingInterceptorLogger =
            object : FlexiLogHttpLoggingInterceptorLogger() {
                override val logger: FlexiLog = logger
                override val tag: String = tag
            }
    }
}