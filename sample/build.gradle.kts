plugins {
    kotlin("multiplatform")
}

kotlin {
    linuxX64("linux") {
        binaries {
            executable {
                entryPoint("me.r4zzz4k.kmidi.sample.main")
            }
        }
    }
    macosX64("macos") {
        binaries {
            executable {
                entryPoint("me.r4zzz4k.kmidi.sample.main")
            }
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                api(project(":core"))

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

        all {
            languageSettings.useExperimentalAnnotation("kotlin.RequiresOptIn")
            languageSettings.useExperimentalAnnotation("kotlin.ExperimentalStdlibApi")
            languageSettings.useExperimentalAnnotation("kotlin.ExperimentalUnsignedTypes")
        }
    }
}
