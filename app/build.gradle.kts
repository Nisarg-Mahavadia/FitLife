// app/build.gradle.kts

// ✅ Apply plugins here without 'apply false'
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services") // The version is defined in the root build.gradle.kts
}

android {
    namespace = "com.marwadiuniversity.fitlife"

    // ✅ Using the latest stable API levels
    compileSdk = 36 // Typically use 34 or higher for new projects

    defaultConfig {
        applicationId = "com.marwadiuniversity.fitlife"
        minSdk = 24
        targetSdk = 35 // Keep targetSdk <= compileSdk for modern projects
        versionCode = 4
        versionName = "1.4"
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

        // ⭐ REQUIRED for LocalDate, java.time, etc. (Fixes your API 26 error)
        isCoreLibraryDesugaringEnabled = true
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

    implementation("androidx.viewpager2:viewpager2:1.0.0")
    implementation("com.google.android.material:material:1.12.0")

    // 🚀 CORRECTED FIREBASE SDKs
    implementation(platform("com.google.firebase:firebase-bom:32.7.4"))

    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-storage")

    // Optional (for image loading)
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // ⭐ ADD THIS (Required for LocalDate / java.time on minSdk 24)
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
