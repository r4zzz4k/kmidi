plugins {
    kotlin("multiplatform")
    id("maven-publish")
}

kotlin {
    linuxX64("linux") {
        compilations.named("main") {
            cinterops.register("asound")
        }
    }
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
        val linuxMain by getting
        val macosMain by getting

        all {
            languageSettings.useExperimentalAnnotation("kotlin.RequiresOptIn")
            languageSettings.useExperimentalAnnotation("kotlin.ExperimentalStdlibApi")
            languageSettings.useExperimentalAnnotation("kotlin.ExperimentalUnsignedTypes")
        }
    }
}
