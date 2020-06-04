pluginManagement {
    repositories {
        maven(url = "https://dl.bintray.com/kotlin/kotlin-eap")
        gradlePluginPortal()
        jcenter()
    }
    resolutionStrategy {
        eachPlugin {
            when {
                requested.id.namespace == "org.jetbrains.kotlin" ->
                    useModule("org.jetbrains.kotlin:kotlin-gradle-plugin:${requested.version}")
            }
        }
    }
}

rootProject.name = "kmidi"

include(
    ":core",
    ":libasound",
    ":sample",
    ":utils"
)
