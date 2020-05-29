plugins {
    kotlin("multiplatform")
    id("maven-publish")
}

kotlin {
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
        val macosMain by getting {
        }
        val macosTest by getting {
        }
    }
}
