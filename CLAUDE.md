# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
# Build the library modules
./gradlew :flexilogger:assembleRelease
./gradlew :FlexiHttpLogger:assembleRelease

# Build everything including TestApp
./gradlew assembleDebug

# Run unit tests
./gradlew test

# Run a specific module's tests
./gradlew :flexilogger:testDebugUnitTest

# Clean build
./gradlew clean
```

## Project Structure

This is a multi-module Android library published via JitPack. The modules are:

- **flexilogger** - Core logging abstraction (`com.duck.flexilogger`)
- **FlexiHttpLogger** - OkHttp logging interceptor integration (`com.duck.flexilogger.flexihttplogger`)
- **TestApp** - Example app demonstrating usage

## Architecture

### FlexiLog (Abstract Base Class)
The core of the library. Consumers extend this class (typically as a Kotlin `object` named `Log`) and implement:
- `canLogToConsole(type: LogType)` - Control console output per log level
- `shouldReport(type: LogType)` - Control which logs go to crash reporting (Sentry, Crashlytics, etc.)
- `shouldReportException(tr: Throwable)` - Filter exceptions from reporting (e.g., ignore network errors)
- `report(type, tag, msg)` / `report(type, tag, msg, tr)` - Send to crash reporting service
- Optional: `shouldLogToFile()` / `writeLogToFile()` - File logging hooks

Log methods (`i`, `d`, `v`, `e`, `w`, `wtf`) accept caller as `Any`, `Class<*>`, or `String` tag. Messages over 4000 chars are automatically chunked.

### LoggerWithLevel
Wrapper around FlexiLog that adds level-based filtering. Created via `flexiLog.withLevel(LoggingLevel.D)`. Useful for per-subsystem log control.

### FlexiLogHttpLoggingInterceptorLogger
Bridges FlexiLog to OkHttp's `HttpLoggingInterceptor.Logger`. Create via companion object factory methods:
```kotlin
FlexiLogHttpLoggingInterceptorLogger.with(Log, LoggingLevel.D)
```

## Key Types

- `LogType` - Enum: D, E, I, V, W, WTF (mirrors android.util.Log)
- `LoggingLevel` - Enum with hierarchical levels: V(5) > I(4) > D(3) > W(2) > E(1) > NONE(0)
