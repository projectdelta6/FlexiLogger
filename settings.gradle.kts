pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://jitpack.io")
        }
        // Node.js distribution for Kotlin/JS
        exclusiveContent {
            forRepository {
                ivy("https://nodejs.org/dist") {
                    patternLayout {
                        artifact("v[revision]/[artifact](-v[revision]-[classifier]).[ext]")
                    }
                    metadataSources { artifact() }
                    content { includeModule("org.nodejs", "node") }
                }
            }
            filter { includeGroup("org.nodejs") }
        }
        // Yarn distribution for Kotlin/JS
        exclusiveContent {
            forRepository {
                ivy("https://github.com/yarnpkg/yarn/releases/download") {
                    patternLayout {
                        artifact("v[revision]/[artifact](-v[revision]).[ext]")
                    }
                    metadataSources { artifact() }
                    content { includeModule("com.yarnpkg", "yarn") }
                }
            }
            filter { includeGroup("com.yarnpkg") }
        }
    }
}

rootProject.name = "FlexiLogger"
include(":flexilogger")
include(":flexilogger-okhttp")
include(":flexilogger-ktor")
include(":TestApp")
