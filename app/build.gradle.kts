plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)

    kotlin("plugin.serialization") version "1.9.22"
}

android {
    namespace = "com.example.quantorium"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.quantorium"
        minSdk = 24
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

//repositories {  // Добавляем репозитории здесь
//    google()
//    mavenCentral()
//    maven { url = uri("https://jitpack.io") }
//}

dependencies {

    implementation(libs.gotrue.kt)
    implementation(libs.supabase.realtime.kt)
    implementation(libs.supabase.storage.kt)
    implementation(libs.supabase.postgrest.kt)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.glide)

    implementation("io.coil-kt:coil-compose:2.6.0")


    
//    implementation(libs.ktor.client.okhttp)
//    implementation(libs.glide)

//    implementation(libs.gotrue.kt)
//    implementation(libs.supabase.realtime.kt)
//    implementation(libs.supabase.storage.kt)
//    implementation(libs.supabase.postgrest.kt)
//    implementation(libs.ktor.client.okhttp)
//    implementation(libs.glide)



    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)



}