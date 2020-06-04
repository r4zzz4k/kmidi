plugins {
    kotlin("multiplatform") version "1.4-M2" apply false
}

allprojects {
    group = "me.r4zzz4k.kmidi"
    version = "0.1"
}

subprojects {
    repositories {
        maven(url = "https://dl.bintray.com/kotlin/kotlin-eap")
        jcenter()
    }
}
