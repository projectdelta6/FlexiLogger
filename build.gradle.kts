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

// Kotlin/JS resolves kotlin-js-store/yarn.lock slightly differently between cold
// and warm builds (cosmetic grouping of npm-aliased entries; identical versions
// and integrity hashes). Warn on that benign drift instead of failing the build —
// otherwise `clean`-then-test (e.g. the publish gate) fails spuriously. The
// committed lock is kept as a reproducibility baseline.
plugins.withType<org.jetbrains.kotlin.gradle.targets.js.yarn.YarnPlugin> {
    the<org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootExtension>().yarnLockMismatchReport =
        org.jetbrains.kotlin.gradle.targets.js.yarn.YarnLockMismatchReport.WARNING
}

// `./gradlew signPublications` signs every publication of every library module
// without installing or uploading anything. publish.sh --dry-run uses it so a bad
// signing key, key ID or passphrase fails before an irreversible Central upload.
// Depending on every Sign task (rather than listing them) keeps new KMP targets
// covered automatically.
subprojects {
    plugins.withId("signing") {
        tasks.register("signPublications") {
            group = "publishing"
            description = "Signs every publication in this module without publishing it."
            dependsOn(tasks.withType<Sign>())
        }
    }
}
