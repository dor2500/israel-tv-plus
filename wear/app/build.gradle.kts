import javax.inject.Inject

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
}

val repo: File = rootDir.parentFile                      // the israel-tv-plus repository
val appVersion = repo.resolve("VERSION").readText().trim()   // the app's version, read only (never bumped here)
// The watch build shares the phone app's package (Google Play pairs them), so its version code lives in its own range.
val appCode = 1_000_000 + appVersion.split(".").let { it[0].toInt() * 100 + it[1].toInt() * 10 + it[2].toInt() }

// Release key: the phone app's key (~/.android), so the watch app is signed like every other Android build.
val keyStore = File(System.getProperty("user.home"), ".android/israel-tv-plus.jks")
val keyPass = File(System.getProperty("user.home"), ".android/israel-tv-plus.pass")

android {
    namespace = "com.israeltv.plus.wear"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.israeltv.plus"
        minSdk = 30   // Wear OS 3
        targetSdk = 36
        versionCode = appCode
        versionName = "$appVersion-wear"
    }

    signingConfigs {
        if (keyStore.isFile && keyPass.isFile) {
            create("release") {
                val pw = keyPass.readText().trim()
                storeFile = keyStore
                storePassword = pw
                keyAlias = "itv"
                keyPassword = pw
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.findByName("release") ?: signingConfigs.getByName("debug")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

kotlin {
    jvmToolchain(17)
}

/** channels.json from the repository root (the same list as every other build) as an APK asset. */
abstract class ChannelAsset @Inject constructor(private val fs: FileSystemOperations) : DefaultTask() {
    @get:InputFile @get:PathSensitive(PathSensitivity.NONE) abstract val channels: RegularFileProperty
    @get:OutputDirectory abstract val outputDir: DirectoryProperty

    @TaskAction
    fun run() {
        fs.sync {
            into(outputDir)
            from(channels)
        }
    }
}

/** Launcher icons from assets/app (tools/make_icons.py), the same art as the phone and TV apps. */
abstract class AppIcons @Inject constructor(private val fs: FileSystemOperations) : DefaultTask() {
    @get:InputDirectory @get:PathSensitive(PathSensitivity.RELATIVE) abstract val appArt: DirectoryProperty
    @get:OutputDirectory abstract val outputDir: DirectoryProperty

    @TaskAction
    fun run() {
        fs.sync {
            into(outputDir)
            for ((dir, px) in listOf("mdpi" to 48, "hdpi" to 72, "xhdpi" to 96, "xxhdpi" to 144, "xxxhdpi" to 192)) {
                from(appArt.file("icon-$px.png")) { into("mipmap-$dir"); rename { "ic_launcher.png" } }
            }
        }
    }
}

val channelAsset = tasks.register<ChannelAsset>("channelAsset") { channels.set(repo.resolve("channels.json")) }
val appIcons = tasks.register<AppIcons>("appIcons") { appArt.set(repo.resolve("assets/app")) }

androidComponents {
    onVariants { variant ->
        variant.sources.assets?.addGeneratedSourceDirectory(channelAsset, ChannelAsset::outputDir)
        variant.sources.res?.addGeneratedSourceDirectory(appIcons, AppIcons::outputDir)
    }
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2026.09.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.wear.compose:compose-material3:1.7.1")
    implementation("androidx.wear.compose:compose-foundation:1.7.1")
    implementation("androidx.wear.compose:compose-navigation:1.7.1")
    implementation("androidx.activity:activity-compose:1.13.0")
    implementation("androidx.core:core-ktx:1.19.1")
    implementation("androidx.core:core-splashscreen:1.2.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.11.0")

    implementation("androidx.media3:media3-exoplayer:1.11.1")
    implementation("androidx.media3:media3-exoplayer-hls:1.11.1")
    implementation("androidx.media3:media3-session:1.11.1")
    implementation("androidx.media3:media3-ui-compose:1.11.1")

    implementation("io.coil-kt.coil3:coil-compose:3.6.3")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.6.3")
    implementation("com.squareup.okhttp3:okhttp:5.5.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.12.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.11.0")
}
