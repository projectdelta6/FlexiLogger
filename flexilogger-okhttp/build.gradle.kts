import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.vanniktech.publish)
}

kotlin {
    // Android target (new KMP plugin)
    androidLibrary {
        namespace = "com.duck.flexilogger.okhttp"
        compileSdk = libs.versions.compileSdk.get().toInt()
        minSdk = libs.versions.minSdk.get().toInt()

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    // JVM target (Desktop)
    jvm()

    // Source sets
    sourceSets {
        commonMain.dependencies {
            api(project(":flexilogger"))
            api(libs.logging.interceptor)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()

    coordinates("io.github.projectdelta6", "flexilogger-okhttp", libs.versions.flexiLoggerVersion.get())

    pom {
        name.set("FlexiLogger OkHttp")
        description.set("OkHttp logging interceptor integration for FlexiLogger.")
        url.set("https://github.com/projectdelta6/FlexiLogger")
        inceptionYear.set("2020")

        licenses {
            license {
                name.set("GNU General Public License v3.0")
                url.set("https://www.gnu.org/licenses/gpl-3.0.html")
            }
        }

        developers {
            developer {
                id.set("projectdelta6")
                name.set("Bradley Duck")
                email.set("projectdelta6@gmail.com")
            }
        }

        scm {
            url.set("https://github.com/projectdelta6/FlexiLogger")
            connection.set("scm:git:git://github.com/projectdelta6/FlexiLogger.git")
            developerConnection.set("scm:git:ssh://git@github.com/projectdelta6/FlexiLogger.git")
        }
    }
}
