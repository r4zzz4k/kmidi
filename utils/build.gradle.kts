plugins {
    kotlin("multiplatform")
    id("maven-publish")
}

kotlin {
    linuxX64("linux")
    macosX64("macos")
    sourceSets {
        commonMain {
            dependencies {
                implementation(kotlin("stdlib-common"))
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val linuxMain by getting
        val macosMain by getting

        val nativeMain by creating {
            dependsOn(commonMain.get())
            linuxMain.dependsOn(this)
            macosMain.dependsOn(this)
        }

        all {
            languageSettings.useExperimentalAnnotation("kotlin.RequiresOptIn")
            languageSettings.useExperimentalAnnotation("kotlin.ExperimentalStdlibApi")
            languageSettings.useExperimentalAnnotation("kotlin.ExperimentalUnsignedTypes")
        }
    }
}
