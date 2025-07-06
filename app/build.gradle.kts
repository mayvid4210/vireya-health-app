plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    buildFeatures {
        compose=true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }

    namespace = "com.example.vireya"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.vireya"
        minSdk = 26
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        jvmToolchain(17)
    }
    kotlinOptions {
        jvmTarget = "17"
    }

}

dependencies {
    implementation ("androidx.activity:activity-compose:1.7.0")
    implementation ("androidx.compose.ui:ui:1.4.3")

    implementation ("androidx.navigation:navigation-compose:2.5.3")
    implementation ("io.coil-kt:coil-compose:2.2.2")
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.compose.material:material-icons-extended") // or latest
    implementation ("androidx.compose.ui:ui-text:1.6.1")
    implementation ("androidx.compose.material3:material3:1.3.1")// for FontWeight



    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Firebase
implementation(platform("com.google.firebase:firebase-bom:32.7.1"))
implementation("com.google.firebase:firebase-firestore-ktx")
implementation("com.google.firebase:firebase-auth-ktx")
implementation("com.google.firebase:firebase-storage-ktx")

// ML Kit for OCR
implementation("com.google.mlkit:text-recognition:16.0.0")

// Core
implementation("androidx.core:core-ktx:1.12.0")
implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")

// Compose
implementation("androidx.activity:activity-compose:1.7.2")
implementation("androidx.compose.ui:ui:1.5.3")
implementation("androidx.compose.material3:material3:1.1.2")
implementation("androidx.navigation:navigation-compose:2.7.5")

// Tooling (optional)
debugImplementation("androidx.compose.ui:ui-tooling-preview:1.5.3")
debugImplementation("androidx.compose.ui:ui-tooling:1.5.3")

// Testing
testImplementation("junit:junit:4.13.2")
androidTestImplementation("androidx.test.ext:junit:1.1.5")
androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.5.3")

// Firebase BOM (manages versions automatically)
implementation("com.google.firebase:firebase-firestore-ktx")

}
