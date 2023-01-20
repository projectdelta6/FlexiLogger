package com.duck.flexilog_sentry

import com.duck.flexilogger.FlexiLog
import com.duck.flexilogger.helper.LogType
import io.sentry.Breadcrumb
import io.sentry.Sentry
import io.sentry.SentryEvent
import io.sentry.SentryLevel
import io.sentry.protocol.Message
import org.jetbrains.annotations.Contract

abstract class FlexiLogSentry: FlexiLog() {
	/**
	 * Implement the actual reporting.
	 *
	 * @param type [Int] @[LogType], the type of log this came from.
	 * @param tag [Class] The Log tag
	 * @param msg [String] The Log message.
	 */
	override fun report(type: LogType, tag: String, msg: String) {
		Sentry.captureEvent(SentryEvent().apply {
			message = Message().apply { message = msg }
			level = getLevel(type)
			logger = tag
		})
	}

	/**
	 * Implement the actual reporting.
	 *
	 * @param type [Int] @[LogType], the type of log this came from.
	 * @param tag [Class] The Log tag
	 * @param msg [String] The Log message.
	 * @param tr  [Throwable] to be attached to the Log.
	 */
	override fun report(type: LogType, tag: String, msg: String, tr: Throwable) {
		Sentry.captureEvent(SentryEvent().apply {
			message = Message().apply { message = msg }
			level = getLevel(type)
			logger = tag
			throwable = tr
		})
	}

	@Contract(pure = true)
	private fun getLevel(type: LogType): SentryLevel {
		return when (type) {
			LogType.E -> SentryLevel.ERROR
			LogType.D, LogType.V, LogType.WTF -> SentryLevel.DEBUG
			LogType.I -> SentryLevel.INFO
			LogType.W -> SentryLevel.WARNING
		}
	}

	//------ Breadcrumbs -------

	/**
	 * Create a breadcrumb
	 *
	 * @param caller Any object, the Class name will be used as the 'tag' in the breadcrumb message
	 * @param message The message for the breadcrumb
	 */
	fun breadCrumb(caller: Any, message: String, category: String = "") {
		breadCrumb(getClassName(caller), message, category)
	}

	/**
	 * Create a breadcrumb
	 *
	 * @param caller Class object, the Class name will be used as the 'tag' in the breadcrumb message
	 * @param message The message for the breadcrumb
	 */
	fun breadCrumb(caller: Class<*>, message: String, category: String = "") {
		breadCrumb(getClassName(caller), message, category)
	}

	/**
	 * Create a breadcrumb
	 *
	 * @param tag String used to construct the breadcrumb message using [formatBreadcrumbMessage]
	 * @param message The message for the breadcrumb
	 */
	fun breadCrumb(tag: String, message: String, category: String = "") {
		if(shouldLogBreadcrumbs()) {
			breadCrumb(formatBreadcrumbMessage(tag, message), category)
		}
	}

	/**
	 * Create a breadcrumb
	 *
	 * @param message The message for the breadcrumb
	 */
	fun breadCrumb(message: String, category: String = "") {
		if(shouldLogBreadcrumbs()) {
			Sentry.addBreadcrumb(message, category)
		}
	}

	val breadcrumb = BreadcrumbBuilder()

	inner class BreadcrumbBuilder {
		/**
		 * Creates a breadcrumb using [Breadcrumb.http]
		 */
		fun http(url: String, method: String, code: Int? = null) {
			breadcrumb(Breadcrumb.http(url, method, code))
		}

		/**
		 * Creates a breadcrumb using [Breadcrumb.navigation]
		 */
		fun navigation(from: String, to: String) {
			breadcrumb(Breadcrumb.navigation(from, to))
		}

		/**
		 * Creates a breadcrumb using [Breadcrumb.transaction]
		 */
		fun transaction(message: String) {
			breadcrumb(Breadcrumb.transaction(message))
		}

		/**
		 * Creates a breadcrumb using [Breadcrumb.debug]
		 */
		fun debug(message: String) {
			breadcrumb(Breadcrumb.debug(message))
		}

		/**
		 * Creates a breadcrumb using [Breadcrumb.error]
		 */
		fun error(message: String) {
			breadcrumb(Breadcrumb.error(message))
		}

		/**
		 * Creates a breadcrumb using [Breadcrumb.info]
		 */
		fun info(message: String) {
			breadcrumb(Breadcrumb.info(message))
		}

		/**
		 * Creates a breadcrumb using [Breadcrumb.query]
		 */
		fun query(message: String) {
			breadcrumb(Breadcrumb.query(message))
		}

		/**
		 * Creates a breadcrumb using [Breadcrumb.ui]
		 */
		fun ui(category: String, message: String) {
			breadcrumb(Breadcrumb.ui(category, message))
		}

		/**
		 * Creates a breadcrumb using [Breadcrumb.user]
		 */
		fun user(category: String, message: String) {
			breadcrumb(Breadcrumb.user(category, message))
		}

		/**
		 * Creates a breadcrumb using [Breadcrumb.userInteraction]
		 */
		fun userInteraction(
			subCategory: String,
			viewId: String? = null,
			viewClass: String? = null,
			additionalData: Map<String,Any> = emptyMap()
		) {
			breadcrumb(Breadcrumb.userInteraction(subCategory, viewId, viewClass, additionalData))
		}
	}

	/**
	 * Create a breadcrumb from a Sentry [Breadcrumb] object.
	 */
	fun breadcrumb(breadcrumb: Breadcrumb) {
		if(shouldLogBreadcrumbs()) {
			Sentry.addBreadcrumb(breadcrumb)
		}
	}

	/**
	 * Join the [tag] and [message] into a single string.
	 *
	 * The base implementation constructs the string with the format "[tag]::[message]"
	 */
	open fun formatBreadcrumbMessage(tag: String, message: String): String {
		return if(tag.isBlank()) message else "$tag::$message"
	}

	/**
	 * Determine if the breadcrumb should be reported or not.
	 */
	protected abstract fun shouldLogBreadcrumbs(): Boolean

	/**
	 * Clear captured breadCrumbs.
	 */
	fun clearBreadCrumbs() {
		Sentry.clearBreadcrumbs()
	}
}