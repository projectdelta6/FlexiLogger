package com.duck.flexilogger.flexihttplogger

import com.duck.flexilogger.FlexiLog
import com.duck.flexilogger.LoggerWithLevel
import com.duck.flexilogger.LoggingLevel
import okhttp3.logging.HttpLoggingInterceptor

abstract class FlexiLogHttpLoggingInterceptorLogger : HttpLoggingInterceptor.Logger {
	abstract val logger: LoggerWithLevel
	open val tag = "HttpLoggingInterceptor"

	override fun log(message: String) {
		if (message.isNotBlank()) {
			logger.i(tag, message)
		}
	}

	companion object {
		@JvmOverloads
		fun with(
			logger: FlexiLog,
			loggingLevel: LoggingLevel = LoggingLevel.D
		) = with(LoggerWithLevel(loggingLevel, logger))

		fun with(
			logger: LoggerWithLevel
		): FlexiLogHttpLoggingInterceptorLogger =
			object : FlexiLogHttpLoggingInterceptorLogger() {
				override val logger: LoggerWithLevel = logger
			}

		@JvmOverloads
		fun with(
			logger: FlexiLog,
			loggingLevel: LoggingLevel = LoggingLevel.D,
			tag: String
		) = with(LoggerWithLevel(loggingLevel, logger), tag)

		fun with(
			logger: LoggerWithLevel,
			tag: String
		): FlexiLogHttpLoggingInterceptorLogger =
			object : FlexiLogHttpLoggingInterceptorLogger() {
				override val logger: LoggerWithLevel = logger
				override val tag: String = tag
			}
	}
}