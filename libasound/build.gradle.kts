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

    sourceSets {
        commonMain {
            dependencies {
                api(project(":utils"))
                implementation(kotlin("stdlib-common"))
            }
        }
        val linuxMain by getting

        all {
            languageSettings.useExperimentalAnnotation("kotlin.RequiresOptIn")
            languageSettings.useExperimentalAnnotation("kotlin.ExperimentalStdlibApi")
            languageSettings.useExperimentalAnnotation("kotlin.ExperimentalUnsignedTypes")
        }
    }
}
