import javax.inject.Inject

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
}

val repo: File = rootDir.parentFile                      // the israel-tv-plus repository
val appVersion = repo.resolve("VERSION").readText().trim()   // 3.4.0
val appCode = appVersion.split(".").let { it[0].toInt() * 100 + it[1].toInt() * 10 + it[2].toInt() }   // -> 340

// Release key: the same one tools/build_apk.ps1 always used, so the update installs over every earlier APK.
val keyStore = File(System.getProperty("user.home"), ".android/israel-tv-plus.jks")
val keyPass = File(System.getProperty("user.home"), ".android/israel-tv-plus.pass")

android {
    namespace = "com.israeltv.plus"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.israeltv.plus"
        minSdk = 24   // Material 3 1.5 (Expressive) needs Android 7.0
        targetSdk = 37
        versionCode = appCode
        versionName = appVersion
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

    sourceSets["main"].apply {
        java.directories.add(repo.resolve("platforms/android/src").path)   // MainActivity.java: the TV app (WebView)
        res.directories.add(repo.resolve("platforms/android/res").path)    // its theme, splash, launch sound, adaptive icon
    }

    androidResources {
        noCompress += listOf("wav")   // SoundPool reads the launch sound straight from the APK
    }

    packaging {
        resources.excludes += listOf("/META-INF/{AL2.0,LGPL2.1}", "/META-INF/versions/9/OSGI-INF/MANIFEST.MF")
    }
}

kotlin {
    jvmToolchain(17)
    compilerOptions {
        freeCompilerArgs.addAll(
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3ExpressiveApi",
            "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi",
        )
    }
}

/** The web app (TVs) + channels.json (phones) as APK assets, taken from the repository root like the other builds. */
abstract class WebAssets @Inject constructor(private val fs: FileSystemOperations) : DefaultTask() {
    @get:InputDirectory @get:PathSensitive(PathSensitivity.RELATIVE) abstract val repoAssets: DirectoryProperty
    @get:InputFiles @get:PathSensitive(PathSensitivity.RELATIVE) abstract val webFiles: ConfigurableFileCollection
    @get:InputFile @get:PathSensitive(PathSensitivity.NONE) abstract val channels: RegularFileProperty
    @get:OutputDirectory abstract val outputDir: DirectoryProperty

    @TaskAction
    fun run() {
        fs.sync {
            into(outputDir)
            from(webFiles) { into("www") }
            from(repoAssets) {
                into("www/assets")
                exclude("**/*&*", "**/*.map", "app/banner-1280*", "app/icon-1024*")
            }
            from(channels)
        }
    }
}

/** Launcher icons + the Android TV banner from assets/app (tools/make_icons.py). */
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
            from(appArt.file("banner-320x180.png")) { into("drawable-xhdpi"); rename { "banner.png" } }
        }
    }
}

val webAssets = tasks.register<WebAssets>("webAssets") {
    repoAssets.set(repo.resolve("assets"))
    webFiles.from(repo.resolve("tools/webfiles.txt").readLines().map { it.trim() }
        .filter { it.isNotEmpty() && !it.startsWith("#") }.map { repo.resolve(it) })
    channels.set(repo.resolve("channels.json"))
}
val appIcons = tasks.register<AppIcons>("appIcons") { appArt.set(repo.resolve("assets/app")) }

androidComponents {
    onVariants { variant ->
        variant.sources.assets?.addGeneratedSourceDirectory(webAssets, WebAssets::outputDir)
        variant.sources.res?.addGeneratedSourceDirectory(appIcons, AppIcons::outputDir)
    }
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2026.09.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3:1.5.0-beta02")   // Material 3 Expressive
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.activity:activity-compose:1.13.0")
    implementation("androidx.core:core-ktx:1.19.1")
    implementation("androidx.core:core-splashscreen:1.2.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.11.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.11.0")

    implementation("androidx.media3:media3-exoplayer:1.11.1")
    implementation("androidx.media3:media3-exoplayer-hls:1.11.1")
    implementation("androidx.media3:media3-ui:1.11.1")

    implementation("dev.chrisbanes.haze:haze:2.0.1")        // real frosted glass (RenderEffect blur)
    implementation("dev.chrisbanes.haze:haze-blur:2.0.1")
    implementation("io.coil-kt.coil3:coil-compose:3.6.3")
    implementation("io.coil-kt.coil3:coil-svg:3.6.3")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.6.3")
    implementation("com.squareup.okhttp3:okhttp:5.5.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.12.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.11.0")
}
