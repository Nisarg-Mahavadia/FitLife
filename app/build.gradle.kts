// app/build.gradle.kts

// ✅ Apply plugins here without 'apply false'
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services") // The version is defined in the root build.gradle.kts
}

android {
    namespace = "com.example.project"

    // ✅ Using the latest stable API levels
    compileSdk = 36 // Typically use 34 or higher for new projects

    defaultConfig {
        applicationId = "com.example.project"
        minSdk = 24
        targetSdk = 34 // Keep targetSdk <= compileSdk for modern projects
        versionCode = 1
        versionName = "1.0"
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

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        // ✅ Using Java 17 for modern Android development
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        // ✅ Match the Java target version
        jvmTarget = "17"
    }
}

dependencies {

    // Core Android libraries
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // 🚀 CORRECTED FIREBASE SDKs
    // Use the latest stable BoM (e.g., 32.7.4)
    implementation(platform("com.google.firebase:firebase-bom:32.7.4"))

    // 🔥 FIX: Remove -ktx suffix. Use the main artifacts for Kotlin-friendly code.
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-storage")

    // Optional (for image loading)
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}