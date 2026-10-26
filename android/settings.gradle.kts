// Israel TV+ for Android: the phone UI is native (Kotlin + Jetpack Compose); TVs keep the web app in a WebView
// (platforms/android/src). One APK for both, built from this folder: tools/build_apk.ps1 -> gradlew assembleRelease.
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
rootProject.name = "IsraelTVPlus"
include(":app")
