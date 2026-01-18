# FlexiLogger KMP Migration Plan

**Purpose:** Document the plan for adapting FlexiLogger from Android-only to Kotlin Multiplatform.
**Status:** COMPLETED
**Target Platforms:** Android, iOS, Desktop (JVM), Web (JS)

---

## 1. Current Architecture Analysis

### 1.1 Module Structure

```
FlexiLogger/
├── FlexiLogger/           # Core logging library
│   └── src/main/java/com/duck/flexilogger/
│       ├── FlexiLog.kt         # Abstract base class (456 lines)
│       ├── LogType.kt          # Enum (pure Kotlin)
│       ├── LoggingLevel.kt     # Enum with logic (pure Kotlin)
│       └── LoggerWithLevel.kt  # Wrapper class
└── FlexiHttpLogger/       # OkHttp interceptor integration
    └── src/main/java/com/duck/flexihttplogger/
        └── FlexiLogHttpLoggingInterceptorLogger.kt
```

### 1.2 Android-Specific Code

**Only ONE location uses Android APIs:**

| File | Line | API Used | Purpose |
|------|------|----------|---------|
| `FlexiLog.kt` | 360-375 | `android.util.Log.*` | Console logging |

**The `logToConsole` function (lines 356-378):**
```kotlin
protected fun logToConsole(type: LogType, tag: String, msg: String, tr: Throwable? = null) {
    if (canLogToConsole(type)) {
        if (tr != null) {
            when (type) {
                LogType.I -> android.util.Log.i(tag, msg, tr)
                LogType.D -> android.util.Log.d(tag, msg, tr)
                // ... etc
            }
        } else {
            when (type) {
                LogType.I -> android.util.Log.i(tag, msg)
                // ... etc
            }
        }
    }
}
```

### 1.3 Pure Kotlin Code (Portable)

| Component | Lines | Notes |
|-----------|-------|-------|
| `LogType` enum | 10 | 100% portable |
| `LoggingLevel` enum | 21 | 100% portable |
| `LoggerWithLevel` | ~30 | 100% portable |
| `FlexiLog` (excluding `logToConsole`) | ~400 | 100% portable |
| Message chunking (`chopDown`) | 14 | 100% portable |
| Conditional logging | 16 | 100% portable |
| Abstract reporting methods | N/A | 100% portable |

**Portability Assessment: ~95% of code is already pure Kotlin**

### 1.4 Java Interop Considerations

The library uses `@JvmOverloads` annotations for Java compatibility. These should be preserved in the JVM/Android targets but can be removed from other targets (or left in place - Kotlin handles this).

The `getClassName(o: Any)` function uses `o.javaClass.simpleName` which needs KMP adaptation:
- Android/JVM: `o::class.java.simpleName`
- Other platforms: `o::class.simpleName` (may include package)

---

## 2. Target KMP Structure

### 2.1 Proposed Module Layout

```
FlexiLogger/
├── flexilogger/                    # KMP core module
│   └── src/
│       ├── commonMain/kotlin/com/duck/flexilogger/
│       │   ├── FlexiLog.kt         # Abstract base (most code)
│       │   ├── LogType.kt          # Enum (unchanged)
│       │   ├── LoggingLevel.kt     # Enum (unchanged)
│       │   ├── LoggerWithLevel.kt  # Wrapper (unchanged)
│       │   └── PlatformLogger.kt   # expect declaration
│       ├── androidMain/kotlin/com/duck/flexilogger/
│       │   └── PlatformLogger.android.kt  # android.util.Log
│       ├── iosMain/kotlin/com/duck/flexilogger/
│       │   └── PlatformLogger.ios.kt      # NSLog
│       ├── jvmMain/kotlin/com/duck/flexilogger/
│       │   └── PlatformLogger.jvm.kt      # java.util.logging
│       └── jsMain/kotlin/com/duck/flexilogger/
│           └── PlatformLogger.js.kt       # console.log
├── flexilogger-okhttp/             # OkHttp integration (JVM/Android only)
│   └── src/
│       └── commonMain/             # OkHttp now supports KMP
└── flexilogger-ktor/               # NEW: Ktor integration (full KMP)
    └── src/
        └── commonMain/
```

### 2.2 Source Set Dependencies

```
commonMain
├── androidMain
├── iosMain
│   ├── iosX64Main
│   ├── iosArm64Main
│   └── iosSimulatorArm64Main
├── jvmMain (Desktop)
└── jsMain (Web)
```

---

## 3. Implementation Plan

### Phase 1: Project Setup

- [ ] Create new KMP project structure (keep old structure for reference)
- [ ] Configure `build.gradle.kts` for multiplatform:
  ```kotlin
  plugins {
      kotlin("multiplatform")
      id("com.android.library")
      id("maven-publish")
  }

  kotlin {
      androidTarget {
          publishLibraryVariants("release")
      }
      iosX64()
      iosArm64()
      iosSimulatorArm64()
      jvm()
      js {
          browser()
          nodejs()
      }
      // Future: wasmJs()

      sourceSets {
          commonMain.dependencies {
              // No dependencies needed for core
          }
      }
  }
  ```
- [ ] Set up source set directories
- [ ] Configure publishing (Maven Central - JitPack doesn't support KMP)

### Phase 2: Core Migration

- [ ] Move `LogType.kt` to `commonMain` (no changes needed)
- [ ] Move `LoggingLevel.kt` to `commonMain` (no changes needed)
- [ ] Move `LoggerWithLevel.kt` to `commonMain` (no changes needed)

- [ ] Create expect/actual for platform logging:

**commonMain/PlatformLogger.kt:**
```kotlin
package com.duck.flexilogger

/**
 * Platform-specific console logging implementation.
 */
internal expect fun platformLog(
    type: LogType,
    tag: String,
    message: String,
    throwable: Throwable? = null
)

/**
 * Platform-specific class name extraction.
 */
internal expect fun getSimpleClassName(obj: Any): String
```

**androidMain/PlatformLogger.android.kt:**
```kotlin
package com.duck.flexilogger

import android.util.Log

internal actual fun platformLog(
    type: LogType,
    tag: String,
    message: String,
    throwable: Throwable?
) {
    if (throwable != null) {
        when (type) {
            LogType.I -> Log.i(tag, message, throwable)
            LogType.D -> Log.d(tag, message, throwable)
            LogType.V -> Log.v(tag, message, throwable)
            LogType.E -> Log.e(tag, message, throwable)
            LogType.W -> Log.w(tag, message, throwable)
            LogType.WTF -> Log.wtf(tag, message, throwable)
        }
    } else {
        when (type) {
            LogType.I -> Log.i(tag, message)
            LogType.D -> Log.d(tag, message)
            LogType.V -> Log.v(tag, message)
            LogType.E -> Log.e(tag, message)
            LogType.W -> Log.w(tag, message)
            LogType.WTF -> Log.wtf(tag, message)
        }
    }
}

internal actual fun getSimpleClassName(obj: Any): String = obj::class.java.simpleName
```

**iosMain/PlatformLogger.ios.kt:**
```kotlin
package com.duck.flexilogger

import platform.Foundation.NSLog

internal actual fun platformLog(
    type: LogType,
    tag: String,
    message: String,
    throwable: Throwable?
) {
    val level = when (type) {
        LogType.V -> "VERBOSE"
        LogType.D -> "DEBUG"
        LogType.I -> "INFO"
        LogType.W -> "WARN"
        LogType.E -> "ERROR"
        LogType.WTF -> "WTF"
    }
    val throwableInfo = throwable?.let { "\n${it.stackTraceToString()}" } ?: ""
    NSLog("[$level] $tag: $message$throwableInfo")
}

internal actual fun getSimpleClassName(obj: Any): String =
    obj::class.simpleName ?: obj::class.toString()
```

**jvmMain/PlatformLogger.jvm.kt:**
```kotlin
package com.duck.flexilogger

import java.util.logging.Level
import java.util.logging.Logger

internal actual fun platformLog(
    type: LogType,
    tag: String,
    message: String,
    throwable: Throwable?
) {
    val logger = Logger.getLogger(tag)
    val level = when (type) {
        LogType.V -> Level.FINEST
        LogType.D -> Level.FINE
        LogType.I -> Level.INFO
        LogType.W -> Level.WARNING
        LogType.E, LogType.WTF -> Level.SEVERE
    }
    if (throwable != null) {
        logger.log(level, message, throwable)
    } else {
        logger.log(level, message)
    }
}

internal actual fun getSimpleClassName(obj: Any): String = obj::class.java.simpleName
```

**jsMain/PlatformLogger.js.kt:**
```kotlin
package com.duck.flexilogger

internal actual fun platformLog(
    type: LogType,
    tag: String,
    message: String,
    throwable: Throwable?
) {
    val formattedMessage = "[$tag] $message"
    val throwableInfo = throwable?.let { "\n${it.stackTraceToString()}" } ?: ""

    when (type) {
        LogType.V, LogType.D -> console.log("$formattedMessage$throwableInfo")
        LogType.I -> console.info("$formattedMessage$throwableInfo")
        LogType.W -> console.warn("$formattedMessage$throwableInfo")
        LogType.E, LogType.WTF -> console.error("$formattedMessage$throwableInfo")
    }
}

internal actual fun getSimpleClassName(obj: Any): String =
    obj::class.simpleName ?: "Unknown"
```

- [ ] Refactor `FlexiLog.kt`:
  - Replace `android.util.Log.*` calls with `platformLog()`
  - Replace `o.javaClass.simpleName` with `getSimpleClassName(o)`
  - Keep all other logic unchanged

### Phase 3: OkHttp Integration

- [ ] Move `FlexiHttpLogger` to separate module `flexilogger-okhttp`
- [ ] OkHttp 5.x supports KMP, so this can remain in commonMain for JVM/Android
- [ ] For other platforms, this module simply won't be available

### Phase 4: Ktor Integration (New)

- [ ] Create new `flexilogger-ktor` module for KMP HTTP logging
- [ ] Implement Ktor `HttpClientPlugin` for request/response logging:

```kotlin
package com.duck.flexilogger.ktor

import com.duck.flexilogger.FlexiLog
import io.ktor.client.plugins.api.*

val FlexiLoggerPlugin = createClientPlugin("FlexiLogger") {
    val logger: FlexiLog = pluginConfig.logger

    onRequest { request, content ->
        logger.d("HTTP", "--> ${request.method.value} ${request.url}")
        // Log headers, body, etc.
    }

    onResponse { response ->
        logger.d("HTTP", "<-- ${response.status.value} ${response.request.url}")
        // Log headers, body, etc.
    }
}
```

### Phase 5: Testing

- [ ] Create shared tests in `commonTest`:
  - Message chunking tests
  - Logging level filtering tests
  - Conditional logging tests
- [ ] Platform-specific tests:
  - Android: Verify android.util.Log is called
  - iOS: Verify NSLog is called (may need instrumentation)
  - JVM: Verify java.util.logging is used
  - JS: Verify console methods are called

### Phase 6: Publishing

- [ ] Set up Maven Central publishing (JitPack doesn't support KMP)
- [ ] Configure signing
- [ ] Create release workflow with GitHub Actions:
  - macOS runner for iOS builds
  - Ubuntu runner for Android/JVM/JS builds
- [ ] Update README with KMP usage instructions

---

## 4. API Changes

### 4.1 Breaking Changes

**None expected.** The public API remains identical:

```kotlin
// Usage remains the same
class MyLogger : FlexiLog() {
    override fun canLogToConsole(type: LogType) = true
    override fun shouldReport(type: LogType) = type == LogType.E
    override fun shouldReportException(tr: Throwable) = true
    override fun report(type: LogType, tag: String, msg: String) { /* Crashlytics */ }
    override fun report(type: LogType, tag: String, msg: String, tr: Throwable) { /* Crashlytics */ }
}

logger.d(this, "Debug message")
logger.e("TAG", "Error occurred", exception)
```

### 4.2 New Capabilities

- **iOS Support:** NSLog-based logging
- **Desktop Support:** java.util.logging-based logging
- **Web Support:** console.log/warn/error-based logging
- **Ktor Integration:** New module for KMP HTTP client logging

---

## 5. Migration Path for Existing Users

### 5.1 Dependency Change

**Before (JitPack):**
```kotlin
implementation("com.github.projectdelta6:FlexiLogger:x.x.x")
```

**After (Maven Central):**
```kotlin
// Core library
implementation("com.duck.flexilogger:flexilogger:x.x.x")

// OkHttp integration (Android/JVM only)
implementation("com.duck.flexilogger:flexilogger-okhttp:x.x.x")

// Ktor integration (all platforms)
implementation("com.duck.flexilogger:flexilogger-ktor:x.x.x")
```

### 5.2 Code Changes

**None required.** Existing Android code will work unchanged.

---

## 6. Estimated Effort

| Phase | Effort | Notes |
|-------|--------|-------|
| Phase 1: Project Setup | 2-4 hours | Gradle configuration |
| Phase 2: Core Migration | 4-6 hours | Main work |
| Phase 3: OkHttp Integration | 1-2 hours | Minimal changes |
| Phase 4: Ktor Integration | 4-6 hours | New feature |
| Phase 5: Testing | 4-6 hours | Cross-platform testing |
| Phase 6: Publishing | 4-8 hours | Maven Central setup |

**Total: 2-3 weeks** (part-time / evenings)

---

## 7. Open Questions

1. **WASM Support:** Should we target wasmJs? (Currently experimental)
2. **Logging Backends:** Should we support pluggable backends (SLF4J, Logback)?
3. **Crashlytics KMP:** Firebase Crashlytics now has KMP support - should we provide a default `report()` implementation?
4. **File Logging:** Should `writeLogToFile` have platform-specific defaults?

---

## 8. Resources

- [Kotlin Multiplatform Documentation](https://kotlinlang.org/docs/multiplatform.html)
- [Publishing KMP Libraries](https://kotlinlang.org/docs/multiplatform-publish-lib.html)
- [Ktor Client Plugins](https://ktor.io/docs/client-plugins.html)
- [OkHttp KMP Support](https://square.github.io/okhttp/changelogs/changelog/)

---

## Changelog

| Date | Change |
|------|--------|
| 2026-01-18 | Initial migration plan created |
| 2026-01-18 | KMP migration implemented - all phases complete |
