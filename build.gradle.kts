// Top-level build file. Common configuration for all sub-projects/modules
// lives here. Each module (shared / trainer / trainee) applies the plugins
// it needs in its own build.gradle.kts.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
}
