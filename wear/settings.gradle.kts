// Israel TV+ for Wear OS: a native watch app (Kotlin + Compose for Wear OS + Media3).
// Built from this folder on its own: gradlew assembleRelease -> app/build/outputs/apk/release/.
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "IsraelTVPlusWear"
include(":app")
