plugins {
    kotlin("multiplatform")
    id("maven-publish")
}

kotlin {
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
                api(project(":device"))

                implementation(kotlin("stdlib-common"))
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val macosMain by getting {
        }
        val macosTest by getting {
        }
    }
}
