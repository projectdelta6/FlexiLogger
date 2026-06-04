# Changelog

All notable changes to this project are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [2.1.2] - Unreleased

### Fixed
- Class-reference log tags now resolve to the represented class name on every platform.
  `Log.d(MyActivity::class.java, "msg")` now tags `"MyActivity"` instead of `"Class"`, and
  passing a `KClass` (`MyActivity::class`) — previously mis-tagged on all platforms — now
  resolves correctly. The fix lives in the `getSimpleClassName(Any)` actuals, which now
  detect `Class<*>`/`KClass<*>` callers.

### Deprecated
- `FlexiLog.getClassName(clazz: Class<*>)` (JVM/Android). It is a redundant helper —
  use `clazz.simpleName` directly. Annotated with `ReplaceWith` for a one-click fix.
- The `Class<*>` log overloads on `FlexiLog` and `LoggerWithLevel` (`i`/`d`/`v`/`e`/`w`/`wtf`,
  JVM/Android). They are shadowed in Kotlin by the `Any` overloads, which now resolve
  `Class`/`KClass` callers to the correct tag, making these redundant. Kept for legacy
  Java interop.

### Changed
- Migrated the Kotlin Multiplatform Android target to the AGP 9 `android {}` DSL
  (was the deprecated `androidLibrary {}`), and enabled `withHostTest {}` so `commonTest`
  runs as Android host tests.
- Updated toolchain: Kotlin 2.4.0, Android Gradle Plugin 9.2.1, Gradle 9.5.

### Removed
- JitPack build configuration (`jitpack.yml` and the `jitpack.io` resolution repository),
  now that 2.x releases are published to Maven Central. Legacy 1.x JitPack builds are
  unaffected — they use the configuration committed at their own tags.

### Added
- Kover code-coverage tooling with aggregated reporting across all library modules
  (`./gradlew koverHtmlReport` / `koverXmlReport`), plus a `koverVerify` coverage gate
  wired into the release script so tests and a coverage floor block any publish.
- Comprehensive test suite: `CallSite` formatting, reporting/exception-filter/file-logging
  branches, `onConditionSuspend`, JVM call-site capture, `Class<*>`/`KClass` extraction,
  the OkHttp interceptor bridge, and the Ktor client plugin (via `ktor-client-mock`).

## [2.1.1] - 2025

### Changed
- Updated AGP, Kotlin, Gradle and dependencies.
- Fixed build issues and corrected `LoggingLevel` ordering.

## [2.1.0] - 2025

### Added
- Call site capture for accurate crash reporting (Android/JVM). New
  `report(..., callSite: CallSite?)` overloads with default implementations that delegate
  to the existing `report()` methods — fully backward compatible.

## [2.0.0] - 2024

### Changed
- Rewrote the library as Kotlin Multiplatform, adding iOS, JVM (Desktop) and
  JavaScript (Browser/Node.js) alongside Android.
- OkHttp integration package renamed: `com.duck.flexihttplogger` → `com.duck.flexilogger.okhttp`.

[2.1.2]: https://github.com/projectdelta6/FlexiLogger/compare/v2.1.1...HEAD
[2.1.1]: https://github.com/projectdelta6/FlexiLogger/compare/v2.1.0...v2.1.1
[2.1.0]: https://github.com/projectdelta6/FlexiLogger/compare/v2.0.0...v2.1.0
[2.0.0]: https://github.com/projectdelta6/FlexiLogger/releases/tag/v2.0.0
