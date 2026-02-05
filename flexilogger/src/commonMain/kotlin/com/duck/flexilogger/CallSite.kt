package com.duck.flexilogger

/**
 * Represents the location where a log call was made.
 * Used to provide accurate call site information to crash reporting tools like Sentry or Crashlytics.
 *
 * @property className The fully qualified class name (e.g., "com.example.app.data.repo.DataRepo")
 * @property methodName The method name (e.g., "fetchData")
 * @property fileName The source file name (e.g., "DataRepo.kt"), may be null if unavailable
 * @property lineNumber The line number in the source file, or -1 if unavailable
 */
data class CallSite(
    val className: String,
    val methodName: String,
    val fileName: String?,
    val lineNumber: Int
) {
    /**
     * Returns the simple class name (without package).
     */
    val simpleClassName: String
        get() = className.substringAfterLast('.')

    /**
     * Returns a formatted string representation suitable for display.
     * Example: "DataRepo.fetchData(DataRepo.kt:123)"
     */
    fun toFormattedString(): String {
        val location = if (fileName != null && lineNumber > 0) {
            "($fileName:$lineNumber)"
        } else if (fileName != null) {
            "($fileName)"
        } else {
            ""
        }
        return "$simpleClassName.$methodName$location"
    }

    override fun toString(): String = toFormattedString()
}
