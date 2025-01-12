// Top-level build.gradle file for Gradle 7.0+ using plugins DSL
buildscript {
    dependencies {
        classpath("com.android.tools.build:gradle:8.0.0") // Ensure it's compatible with Gradle 8.0+
    }
}


repositories {
    google()
    mavenCentral()
}

plugins {
    alias(libs.plugins.android.application) apply false
    //id("com.google.gms.google-services") version "4.3.15" apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
}


