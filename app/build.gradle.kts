import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id("androidx.navigation.safeargs.kotlin")
    kotlin("plugin.serialization") version "2.0.21"
}

android {
    namespace = "com.yulianti.kodytest"
    compileSdk = 35

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.yulianti.kodytest"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        val keys = fetchKeys()
        buildConfigField("String", "PUBLIC_KEY", keys.first)
        buildConfigField("String", "PRIVATE_KEY", keys.second)

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isDebuggable = false
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isDebuggable = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.animation.core.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation (libs.logging.interceptor)
    implementation (libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    kapt(libs.androidx.room.compiler)
    implementation(libs.glide)
    kapt(libs.compiler)
    implementation(libs.androidx.recyclerview)
    implementation(libs.kotlinx.serialization.json)


    // Unit Testing with JUnit
    testImplementation(libs.junit)

    // Kotlin Coroutines Test library
    testImplementation(libs.kotlinx.coroutines.test)

    // MockK library for mocking
    testImplementation(libs.mockk)
    // If you prefer Mockito, use the following line instead:
    // testImplementation("org.mockito:mockito-core:4.0.0")

    // Turbine library for testing Flows
    testImplementation(libs.turbine)

    // AndroidX Test libraries
    testImplementation(libs.androidx.core)
    testImplementation(libs.androidx.junit.v115)

    // Core Testing library for LiveData and ViewModel
    testImplementation(libs.androidx.core.testing)

    // (Optional) AndroidX Test Runner and Rules if needed
    androidTestImplementation(libs.androidx.runner)
    androidTestImplementation(libs.androidx.rules)

    implementation(libs.timber)
}

fun fetchKeys(): Pair<String, String> {
    val properties = Properties()
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        properties.load(FileInputStream(localPropertiesFile))
    } else {
        println("Warning: local.properties file is missing. Using default keys.")
    }
    val publicKey = properties.getProperty("publicApiKey") ?: "default_public_key"
    val privateKey = properties.getProperty("privateApiKey") ?: "default_private_key"
    return "\"$publicKey\"" to "\"$privateKey\"" // Wrap in quotes for buildConfigField
}

kapt {
    correctErrorTypes = true
}