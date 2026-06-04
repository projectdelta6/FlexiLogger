# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
# Build all library modules (all platforms)
./gradlew assemble

# Build specific modules
./gradlew :flexilogger:assemble
./gradlew :flexilogger-okhttp:assemble
./gradlew :flexilogger-ktor:assemble

# Build TestApp (Android)
./gradlew :TestApp:assembleDebug

# Run tests (all platforms)
./gradlew allTests

# Run JVM tests only (fastest; covers commonTest logic)
./gradlew :flexilogger:jvmTest

# Code coverage (Kover) — aggregated across all library modules
./gradlew koverHtmlReport   # report at build/reports/kover/html/index.html
./gradlew koverXmlReport    # machine-readable, for CI

# Clean build
./gradlew clean
```

## Testing & Coverage

- Tests live in `commonTest` (cross-platform, run on JVM/Android/JS) and `jvmTest`
  (JVM-only: call-site capture, `Class<*>`/`KClass` extraction, platform logger).
- **Do not** call `platformLog` (i.e. enable `canLogToConsole`) from `commonTest` —
  the Android host test routes to `android.util.Log`, which is not mocked and throws.
  Console-output tests belong in `jvmTest`.
- Ktor plugin tests use `ktor-client-mock`; suspend tests use `kotlinx-coroutines-test`'s `runTest`.
- Kover only measures JVM + Android host tests; iOS/JS coverage is not reported.

## Publishing

Releases go to Maven Central via the Vanniktech Maven Publish plugin, driven by `publish.sh`:

```bash
# Dry run — every step (checks, clean, allTests, koverVerify) EXCEPT the upload
./publish.sh --dry-run      # or -n

# Full release — same gates, then prompts and publishes to Maven Central
./publish.sh
```

- The script gates on `./gradlew allTests koverVerify` (tests + coverage floor) **before**
  the publish confirmation, so a failing build or coverage regression aborts early.
- It also refuses to run with uncommitted changes, and warns if not on `master`.
- `--dry-run` downgrades missing publish credentials to a warning so it runs anywhere;
  a real publish still hard-fails without them.
- For deeper artifact validation (POM/signing) without uploading: `./gradlew publishToMavenLocal`.
- Coverage floor lives in the root `build.gradle.kts` `kover { reports { verify { ... } } }` block.

## Project Structure

This is a **Kotlin Multiplatform** library supporting Android, iOS, JVM, and JavaScript.

```
FlexiLogger/
├── flexilogger/                    # KMP core module
│   └── src/
│       ├── commonMain/             # Shared code (FlexiLog, LogType, etc.)
│       ├── commonTest/             # Shared tests
│       ├── androidMain/            # Android: android.util.Log
│       ├── jvmMain/                # JVM: java.util.logging
│       ├── iosMain/                # iOS: NSLog
│       └── jsMain/                 # JS: console.log
├── flexilogger-okhttp/             # OkHttp integration (JVM/Android only)
│   └── src/commonMain/
├── flexilogger-ktor/               # Ktor integration (all platforms)
│   └── src/commonMain/
└── TestApp/                        # Example Android app
```

## Architecture

### FlexiLog (Abstract Base Class)
The core of the library in `commonMain`. Consumers extend this class (typically as a Kotlin `object` named `Log`) and implement:
- `canLogToConsole(type: LogType)` - Control console output per log level
- `shouldReport(type: LogType)` - Control which logs go to crash reporting
- `shouldReportException(tr: Throwable)` - Filter exceptions from reporting
- `report(type, tag, msg)` / `report(type, tag, msg, tr)` - Send to crash reporting service
- Optional: `shouldLogToFile()` / `writeLogToFile()` - File logging hooks

Log methods (`i`, `d`, `v`, `e`, `w`, `wtf`) accept caller as `Any` or `String` tag. On JVM/Android, `Class<*>` is also supported via extension functions. Messages over 4000 chars are automatically chunked.

### Platform Implementations

Platform-specific code is in `PlatformLogger.kt` files:
- **Android** (`androidMain`): Uses `android.util.Log`
- **JVM** (`jvmMain`): Uses `java.util.logging.Logger`
- **iOS** (`iosMain`): Uses `NSLog`
- **JS** (`jsMain`): Uses `console.log/info/warn/error`

### LoggerWithLevel
Wrapper around FlexiLog that adds level-based filtering. Created via `flexiLog.withLevel(LoggingLevel.D)`. Useful for per-subsystem log control.

### FlexiLogHttpLoggingInterceptorLogger (flexilogger-okhttp)
Bridges FlexiLog to OkHttp's `HttpLoggingInterceptor.Logger`. JVM/Android only.
```kotlin
FlexiLogHttpLoggingInterceptorLogger.with(Log, LoggingLevel.D)
```

### FlexiLoggerPlugin (flexilogger-ktor)
Ktor HTTP client plugin for logging requests/responses. All platforms.
```kotlin
HttpClient {
    installFlexiLogger(Log, LoggingLevel.D)
}
```

## Key Types

- `LogType` - Enum: D, E, I, V, W, WTF
- `LoggingLevel` - Enum with hierarchical levels: V(5) > I(4) > D(3) > W(2) > E(1) > NONE(0)

## Expect/Actual Declarations

In `commonMain/PlatformLogger.kt`:
```kotlin
internal expect fun platformLog(type: LogType, tag: String, message: String, throwable: Throwable?)
internal expect fun getSimpleClassName(obj: Any): String
internal expect fun getSimpleClassName(clazz: KClass<*>): String
internal expect fun currentTimeMillis(): Long
```

Each platform provides `actual` implementations in their respective source sets.

## JVM Backward Compatibility

The `Class<*>` parameter overloads are provided as extension functions in `jvmMain` and `androidMain` to maintain backward compatibility with existing Java/Kotlin code that uses `Class<*>` directly.
