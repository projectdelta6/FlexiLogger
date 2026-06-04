// Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.android.kotlin.multiplatform.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kover)
}

// Aggregate coverage across all library modules.
// Run `./gradlew koverHtmlReport` (output: build/reports/kover/html/index.html)
// or `./gradlew koverXmlReport` for CI consumption.
dependencies {
    kover(project(":flexilogger"))
    kover(project(":flexilogger-okhttp"))
    kover(project(":flexilogger-ktor"))
}

// Coverage gate. `./gradlew koverVerify` fails the build if aggregated coverage
// drops below the configured floor. Wired into the release flow in publish.sh.
kover {
    reports {
        verify {
            rule {
                // Aggregated line coverage across all library modules
                // (JVM + Android host tests; iOS/JS are not measured by Kover).
                // Current ~78%; floor set below that to catch regressions without
                // tripping on minor refactors. Raise as coverage improves.
                minBound(70)
            }
        }
    }
}
