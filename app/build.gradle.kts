plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.hackathon"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.hackathon"
        minSdk = 27
        targetSdk = 35
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    // Camera library
    implementation("androidx.camera:camera-lifecycle:1.4.2")
    implementation("androidx.camera:camera-core:1.4.2")
    implementation("androidx.camera:camera-camera2:1.4.2")
    implementation("androidx.camera:camera-view:1.4.2")
    implementation("androidx.camera:camera-mlkit-vision:1.4.2")

    implementation("com.google.android.gms:play-services-mlkit-face-detection:17.1.0")

    implementation("androidx.fragment:fragment-ktx:1.5.7")
    implementation("androidx.compose.ui:ui:1.4.0")
    implementation("androidx.compose.material:material:1.4.0")
    implementation("androidx.compose.ui:ui-tooling-preview:1.4.0")
    debugImplementation("androidx.compose.ui:ui-tooling:1.4.0")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}