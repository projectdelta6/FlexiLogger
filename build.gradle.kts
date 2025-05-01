plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlinKSP) apply false
    alias(libs.plugins.kotlin.compose) apply false
    `maven-publish`
}

tasks.wrapper {
    gradleVersion = libs.versions.agp.get()
    distributionType = Wrapper.DistributionType.ALL
}