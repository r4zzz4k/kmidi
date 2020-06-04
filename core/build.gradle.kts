plugins {
    kotlin("multiplatform")
}

kotlin {
    linuxX64("linux")
    macosX64("macos")

    sourceSets {
        commonMain {
            dependencies {
                api(project(":utils"))
                implementation(kotlin("stdlib-common"))
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val linuxMain by getting {
            dependencies {
                api(project(":libasound"))
            }
        }
        val macosMain by getting

        all {
            languageSettings.useExperimentalAnnotation("kotlin.RequiresOptIn")
            languageSettings.useExperimentalAnnotation("kotlin.ExperimentalStdlibApi")
            languageSettings.useExperimentalAnnotation("kotlin.ExperimentalUnsignedTypes")
        }
    }
}
