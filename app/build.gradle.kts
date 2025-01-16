plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
    //id("com.android.library")
}

android {
    namespace = "com.example.myapplicationtest1"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.myapplicationtest1"
        minSdk = 24
        targetSdk = 34
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)

    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("org.osmdroid:osmdroid-android:6.1.13")
    implementation ("org.osmdroid:osmdroid-android:6.1.2")
    implementation("com.opencsv:opencsv:5.7.1")


    //FIREBASE:
    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
    implementation("com.google.firebase:firebase-analytics")
    //AUTHENTIFICATION:
    implementation("com.google.firebase:firebase-auth")
    //REALTIME DATABASE:
    implementation ("com.google.firebase:firebase-database")

    //HTTP Client
    implementation ("com.squareup.okhttp3:okhttp:4.11.0")







    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}