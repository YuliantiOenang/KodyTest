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
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation("com.google.dagger:hilt-android:2.51.1")
    kapt("com.google.dagger:hilt-android-compiler:2.51.1")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.3")
    implementation ("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("com.github.bumptech.glide:glide:4.15.1")
    kapt("com.github.bumptech.glide:compiler:4.15.1")
    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")


    // Unit Testing with JUnit
    testImplementation("junit:junit:4.13.2")

    // Kotlin Coroutines Test library
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")

    // MockK library for mocking
    testImplementation("io.mockk:mockk:1.12.0")
    // If you prefer Mockito, use the following line instead:
    // testImplementation("org.mockito:mockito-core:4.0.0")

    // Turbine library for testing Flows
    testImplementation("app.cash.turbine:turbine:0.12.1")

    // AndroidX Test libraries
    testImplementation("androidx.test:core:1.5.0")
    testImplementation("androidx.test.ext:junit:1.1.5")

    // Core Testing library for LiveData and ViewModel
    testImplementation("androidx.arch.core:core-testing:2.2.0")

    // (Optional) AndroidX Test Runner and Rules if needed
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:rules:1.5.0")

    implementation("com.jakewharton.timber:timber:5.0.1")
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