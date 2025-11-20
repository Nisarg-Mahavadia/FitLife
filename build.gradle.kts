// build.gradle.kts
buildscript {
    dependencies {
        // You would typically define plugin versions here if not using the 'plugins' block below,
        // but for a modern setup, the 'plugins' block in settings.gradle.kts is preferred.
    }
}

// NOTE: This block defines the plugin versions for the whole project.
plugins {
    // ✅ Android Application Plugin (Latest stable as of this response)
    alias(libs.plugins.android.application) apply false

    // ✅ Kotlin Android Plugin
    alias(libs.plugins.kotlin.android) apply false

    // ✅ Google Services (for Firebase) - Use a modern version like 4.4.1 
    id("com.google.gms.google-services") version "4.4.1" apply false
}