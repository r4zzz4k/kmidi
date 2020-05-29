plugins {
    kotlin("multiplatform") version "1.3.72" apply false
}

allprojects {
    group = "me.r4zzz4k.kmidi"
    version = "0.1"
}

subprojects {
    repositories {
        mavenCentral()
    }
}
