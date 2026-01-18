# FlexiLogger

Kotlin Multiplatform logging library providing flexible, configurable logging with support for console output, crash reporting integration, and file logging.

**Supported Platforms:** Android, iOS, JVM (Desktop), JavaScript (Browser/Node.js)

[![Release](https://jitpack.io/v/projectdelta6/FlexiLogger.svg)](https://jitpack.io/#projectdelta6/FlexiLogger)

## Features

- Cross-platform logging abstraction
- Configurable log levels (Verbose, Debug, Info, Warning, Error, WTF)
- Automatic message chunking for long messages (4000+ chars)
- Crash reporting integration hooks
- File logging support
- OkHttp integration (JVM/Android)
- Ktor HTTP client integration (all platforms)
- Java interoperability with `@JvmOverloads`

## Installation

> **Note:** Maven Central publishing is planned but not yet configured. For now, you can use the library via JitPack (Android only) or by including it as a local/git dependency.

### Kotlin Multiplatform Projects

Add the library as a git submodule or include it directly in your project, then add the dependency:

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":flexilogger"))

            // Optional: Ktor HTTP logging (all platforms)
            implementation(project(":flexilogger-ktor"))
        }

        // Optional: OkHttp HTTP logging (JVM/Android only)
        jvmMain.dependencies {
            implementation(project(":flexilogger-okhttp"))
        }
        androidMain.dependencies {
            implementation(project(":flexilogger-okhttp"))
        }
    }
}
```

### Android Projects (JitPack)

> **Note:** JitPack support for KMP is limited. This will work for Android consumption but not for iOS/JS targets.

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
    implementation("com.github.projectdelta6.FlexiLogger:flexilogger:Tag")
    implementation("com.github.projectdelta6.FlexiLogger:flexilogger-okhttp:Tag")  // Optional
}
```

Replace `Tag` with the release tag or commit hash.

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
    }

    override fun report(type: LogType, tag: String, msg: String, tr: Throwable) {
        // Implement crash reporting with exception
    }
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

| Method | Description |
|--------|-------------|
| `i(caller, msg?, tr?)` | Info log |
| `d(caller, msg?, tr?)` | Debug log |
| `v(caller, msg?, tr?)` | Verbose log |
| `w(caller, msg?, tr?)` | Warning log |
| `e(caller, msg?, tr?, forceReport?)` | Error log |
| `wtf(caller, msg?, tr?)` | What a Terrible Failure log |
| `onCondition(condition, log)` | Conditional logging |
| `withLevel(level)` | Create level-filtered logger |

The `caller` parameter can be:
- `Any` - class name is extracted automatically
- `String` - used directly as the tag
- `Class<*>` - (JVM/Android only) class name is extracted

## Platform-Specific Behavior

| Platform | Console Output | Class Name Extraction |
|----------|----------------|----------------------|
| Android | `android.util.Log` | `obj::class.java.simpleName` |
| JVM | `java.util.logging.Logger` | `obj::class.java.simpleName` |
| iOS | `NSLog` | `obj::class.simpleName` |
| JS | `console.log/info/warn/error` | `obj::class.simpleName` |

## Migration from 1.x

If you're upgrading from the Android-only version:

1. **Dependency change**: Update your dependencies to use the new module names
2. **Package change for OkHttp**: `com.duck.flexihttplogger` → `com.duck.flexilogger.okhttp`
3. **No code changes required**: The core API remains identical

## License

This project is licensed under the GNU General Public License v3.0 - see the [LICENSE](LICENSE) file for details.
