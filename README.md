# FlexiLogger

Kotlin Multiplatform logging library providing flexible, configurable logging with support for console output, crash reporting integration, and file logging.

**Supported Platforms:** Android, iOS, JVM (Desktop), JavaScript (Browser/Node.js)

[![Maven Central](https://img.shields.io/maven-central/v/io.github.projectdelta6/flexilogger.svg)](https://central.sonatype.com/artifact/io.github.projectdelta6/flexilogger)

## Features

- Cross-platform logging abstraction
- Configurable log levels (Verbose, Debug, Info, Warning, Error, WTF)
- Automatic message chunking for long messages (4000+ chars)
- **Call site capture** for accurate crash reporting (Android/JVM)
- Crash reporting integration hooks (Sentry, Crashlytics, etc.)
- File logging support
- OkHttp integration (JVM/Android)
- Ktor HTTP client integration (all platforms)
- Java interoperability with `@JvmOverloads`

## Installation

### Maven Central (Recommended)

Add the dependency to your project:

**Kotlin Multiplatform:**
```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("io.github.projectdelta6:flexilogger:2.1.0")

            // Optional: Ktor HTTP logging (all platforms)
            implementation("io.github.projectdelta6:flexilogger-ktor:2.1.0")
        }

        // Optional: OkHttp HTTP logging (JVM/Android only)
        jvmMain.dependencies {
            implementation("io.github.projectdelta6:flexilogger-okhttp:2.1.0")
        }
        androidMain.dependencies {
            implementation("io.github.projectdelta6:flexilogger-okhttp:2.1.0")
        }
    }
}
```

**Android/JVM only:**
```kotlin
dependencies {
    implementation("io.github.projectdelta6:flexilogger:2.1.0")
    implementation("io.github.projectdelta6:flexilogger-okhttp:2.1.0")  // Optional
}
```

### JitPack (Legacy 1.x Versions)

For projects still using the Android-only 1.x versions, JitPack remains available:

<details>
<summary>Click to expand JitPack instructions</summary>

Add JitPack to your `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        maven { url = uri("https://jitpack.io") }
    }
}
```

**build.gradle.kts:**
```kotlin
dependencies {
    implementation("com.github.projectdelta6.FlexiLogger:FlexiLogger:1.x.x")
    implementation("com.github.projectdelta6.FlexiLogger:FlexiHttpLogger:1.x.x")  // Optional
}
```

Replace `1.x.x` with the desired [1.x release tag](https://github.com/projectdelta6/FlexiLogger/releases).

</details>

## Usage

### Basic Setup

Create a class that extends `FlexiLog`. For convenience, create it as a Kotlin `object` named `Log`:

```kotlin
package com.example

import com.duck.flexilogger.FlexiLog
import com.duck.flexilogger.LogType

object Log : FlexiLog() {

    override fun canLogToConsole(type: LogType): Boolean {
        // Return true to output logs to the platform console
        // Android: android.util.Log
        // iOS: NSLog
        // JVM: java.util.logging
        // JS: console.log/warn/error
        return true // or: BuildConfig.DEBUG on Android
    }

    override fun shouldReport(type: LogType): Boolean {
        // Return true for log types you want sent to crash reporting
        return type == LogType.E || type == LogType.WTF
    }

    override fun shouldReportException(tr: Throwable): Boolean {
        // Filter which exceptions get reported
        // Return false to ignore network errors, cancellations, etc.
        return true
    }

    override fun report(type: LogType, tag: String, msg: String) {
        // Implement crash reporting (Sentry, Crashlytics, etc.)
        // Note: For call site info, override the CallSite version instead
    }

    override fun report(type: LogType, tag: String, msg: String, tr: Throwable) {
        // Implement crash reporting with exception
        // Note: For call site info, override the CallSite version instead
    }
}
```

### Call Site Capture for Crash Reporting (Android/JVM)

When integrating with crash reporting tools like Sentry or Crashlytics, the default behavior often shows the logging library's internal code as the error source instead of where `Log.e()` was actually called.

FlexiLogger solves this by capturing the actual call site and passing it to your `report()` methods via the `CallSite` parameter.

**Override the CallSite-aware report methods:**

```kotlin
import com.duck.flexilogger.CallSite
import com.duck.flexilogger.FlexiLog
import com.duck.flexilogger.LogType
import io.sentry.Sentry
import io.sentry.SentryEvent
import io.sentry.protocol.Message
import io.sentry.protocol.SentryException
import io.sentry.protocol.SentryStackFrame
import io.sentry.protocol.SentryStackTrace

object Log : FlexiLog() {
    // ... other overrides ...

    // Override these methods to receive call site information
    override fun report(type: LogType, tag: String, msg: String, callSite: CallSite?) {
        Sentry.captureEvent(SentryEvent().apply {
            message = Message().apply { message = msg }
            logger = tag
            // Use callSite to show the actual source location in Sentry
            callSite?.let { site ->
                exceptions = listOf(
                    SentryException().apply {
                        this.type = "LoggedError"
                        this.value = msg
                        this.module = site.className
                        this.stacktrace = SentryStackTrace(listOf(
                            SentryStackFrame().apply {
                                module = site.className
                                function = site.methodName
                                filename = site.fileName
                                lineno = site.lineNumber
                                isInApp = true
                            }
                        ))
                    }
                )
            }
        })
    }

    override fun report(type: LogType, tag: String, msg: String, tr: Throwable, callSite: CallSite?) {
        Sentry.captureEvent(SentryEvent().apply {
            message = Message().apply { message = msg }
            logger = tag
            throwable = tr
            // Add call site as context when there's already a throwable
            callSite?.let { site ->
                setTag("log_call_site", "${site.simpleClassName}.${site.methodName}")
                setExtra("log_location", site.toFormattedString())
            }
        })
    }
}
```

**CallSite Properties:**

| Property              | Description                     | Example                               |
|-----------------------|---------------------------------|---------------------------------------|
| `className`           | Fully qualified class name      | `com.example.app.data.DataRepo`       |
| `simpleClassName`     | Class name without package      | `DataRepo`                            |
| `methodName`          | Method name                     | `fetchData`                           |
| `fileName`            | Source file name (nullable)     | `DataRepo.kt`                         |
| `lineNumber`          | Line number (-1 if unavailable) | `123`                                 |
| `toFormattedString()` | Formatted display string        | `DataRepo.fetchData(DataRepo.kt:123)` |

**Platform Support:**
- ✅ Android - Full support
- ✅ JVM - Full support
- ❌ iOS - Returns `null` (not supported)
- ❌ JS - Returns `null` (not supported)

**Skipping Additional Packages:**

FlexiLogger automatically skips its own internal frames and any class extending `FlexiLog`. If you have additional wrapper classes to skip:

```kotlin
object Log : FlexiLog() {
    override fun getAdditionalSkipPackages(): List<String> =
        listOf("com.myapp.util.LogWrapper")
}
```

### Logging

```kotlin
import com.example.Log

// Using 'this' as the tag (extracts class name)
Log.d(this, "Debug message")
Log.i(this, "Info message")
Log.w(this, "Warning message")
Log.e(this, "Error message", exception)

// Using a string tag
Log.d("MyTag", "Debug message")

// With throwable
Log.e("NetworkError", "Failed to fetch data", exception)

// Force report (bypasses shouldReport check)
Log.e("CriticalError", "This must be reported", forceReport = true)
```

### Conditional Logging

```kotlin
// Only logs if condition is true
Log.onCondition(BuildConfig.DEBUG) { logger ->
    logger.d(this, "This only logs in debug builds")
}

// Suspend version for coroutines
Log.onConditionSuspend(isVerboseMode) { logger ->
    logger.v(this, "Verbose logging enabled")
}
```

### Level-Based Logging

Use `LoggerWithLevel` for subsystem-specific log filtering:

```kotlin
// Create a logger that only logs WARNING and above
val networkLogger = Log.withLevel(LoggingLevel.W)

networkLogger.d("Network", "This won't be logged")  // Filtered out
networkLogger.w("Network", "This will be logged")   // Passes through
networkLogger.e("Network", "This will be logged")   // Passes through
```

### File Logging

Override the file logging methods to enable persistent logging:

```kotlin
object Log : FlexiLog() {
    // ... other overrides ...

    override fun shouldLogToFile(type: LogType): Boolean {
        return true // or filter by type
    }

    override fun writeLogToFile(
        timestamp: Long,
        type: LogType,
        tag: String,
        msg: String,
        tr: Throwable?
    ) {
        // Implement your file writing logic
        val logLine = "[$timestamp] ${type.name}/$tag: $msg"
        // Write to file...
    }
}
```

## HTTP Logging

### OkHttp Integration (JVM/Android)

```kotlin
import com.duck.flexilogger.okhttp.FlexiLogHttpLoggingInterceptorLogger
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

val loggingInterceptor = HttpLoggingInterceptor(
    FlexiLogHttpLoggingInterceptorLogger.with(Log, LoggingLevel.D)
).apply {
    level = HttpLoggingInterceptor.Level.BODY
}

val client = OkHttpClient.Builder()
    .addInterceptor(loggingInterceptor)
    .build()
```

### Ktor Integration (All Platforms)

```kotlin
import com.duck.flexilogger.ktor.FlexiLoggerPlugin
import com.duck.flexilogger.ktor.installFlexiLogger
import io.ktor.client.*

// Simple installation
val client = HttpClient {
    installFlexiLogger(Log, LoggingLevel.D)
}

// Or with full configuration
val client = HttpClient {
    install(FlexiLoggerPlugin) {
        logger = Log
        level = LoggingLevel.D
        tag = "HTTP"
        logHeaders = true
        logBody = false
        sanitizedHeaders = setOf("Authorization", "Cookie")
    }
}
```

## API Reference

### LogType

```kotlin
enum class LogType {
    D,    // Debug
    E,    // Error
    I,    // Info
    V,    // Verbose
    W,    // Warning
    WTF   // What a Terrible Failure
}
```

### LoggingLevel

Hierarchical levels for filtering (higher value = more verbose):

```kotlin
enum class LoggingLevel(val level: Int) {
    V(5),    // Verbose - logs everything
    I(4),    // Info
    D(3),    // Debug
    W(2),    // Warning
    E(1),    // Error only
    NONE(0)  // Logs nothing
}
```

### FlexiLog Methods

| Method                               | Description                  |
|--------------------------------------|------------------------------|
| `i(caller, msg?, tr?)`               | Info log                     |
| `d(caller, msg?, tr?)`               | Debug log                    |
| `v(caller, msg?, tr?)`               | Verbose log                  |
| `w(caller, msg?, tr?)`               | Warning log                  |
| `e(caller, msg?, tr?, forceReport?)` | Error log                    |
| `wtf(caller, msg?, tr?)`             | What a Terrible Failure log  |
| `onCondition(condition, log)`        | Conditional logging          |
| `withLevel(level)`                   | Create level-filtered logger |

The `caller` parameter can be:
- `Any` - class name is extracted automatically
- `String` - used directly as the tag
- `Class<*>` - (JVM/Android only) class name is extracted

### FlexiLog Protected Methods (Override in Subclass)

| Method                                  | Description                                         |
|-----------------------------------------|-----------------------------------------------------|
| `canLogToConsole(type)`                 | **Required.** Whether to log to console             |
| `shouldReport(type)`                    | **Required.** Whether to send to crash reporting    |
| `shouldReportException(tr)`             | **Required.** Filter exceptions for reporting       |
| `report(type, tag, msg)`                | **Required.** Handle crash report without throwable |
| `report(type, tag, msg, tr)`            | **Required.** Handle crash report with throwable    |
| `report(type, tag, msg, callSite?)`     | Handle crash report with call site info             |
| `report(type, tag, msg, tr, callSite?)` | Handle crash report with throwable and call site    |
| `getAdditionalSkipPackages()`           | Additional packages to skip for call site detection |
| `shouldLogToFile(type)`                 | Whether to write to file (default: false)           |
| `writeLogToFile(...)`                   | Handle file writing                                 |

### CallSite

```kotlin
data class CallSite(
    val className: String,      // Fully qualified class name
    val methodName: String,     // Method name
    val fileName: String?,      // Source file name (nullable)
    val lineNumber: Int         // Line number (-1 if unavailable)
) {
    val simpleClassName: String // Class name without package
    fun toFormattedString(): String // e.g., "DataRepo.fetchData(DataRepo.kt:123)"
}
```

## Platform-Specific Behavior

| Platform | Console Output                | Class Name Extraction        |
|----------|-------------------------------|------------------------------|
| Android  | `android.util.Log`            | `obj::class.java.simpleName` |
| JVM      | `java.util.logging.Logger`    | `obj::class.java.simpleName` |
| iOS      | `NSLog`                       | `obj::class.simpleName`      |
| JS       | `console.log/info/warn/error` | `obj::class.simpleName`      |

## Migration

### From 2.0.x to 2.1.x

Version 2.1.0 adds call site capture for crash reporting. **This is fully backward compatible:**

- Existing `report()` implementations continue to work unchanged
- To use call site info, override the new `report(..., callSite: CallSite?)` methods
- The new methods have default implementations that delegate to the existing ones

### From 1.x to 2.x

If you're upgrading from the Android-only version:

1. **Dependency change**: Update your dependencies to use the new module names
2. **Package change for OkHttp**: `com.duck.flexihttplogger` → `com.duck.flexilogger.okhttp`
3. **No code changes required**: The core API remains identical

## License

This project is licensed under the GNU General Public License v3.0 - see the [LICENSE](LICENSE) file for details.
