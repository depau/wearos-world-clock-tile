plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
}

android {
    namespace = "gay.depau.worldclocktile"
    compileSdk = 34

    defaultConfig {
        applicationId = "gay.depau.worldclocktile"
        minSdk = 26
        targetSdk = 34
        versionCode = 2
        versionName = "1.1"

        vectorDrawables.useSupportLibrary = true
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
    packaging {
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
            excludes.add("META-INF/atomicfu.kotlin_module")
        }
    }

    buildFeatures {
        compose = true
        viewBinding = true
        buildConfig = true
    }

    buildTypes {
        all {
            isMinifyEnabled = true
            isShrinkResources = true
        }
        release {
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        debug {
            proguardFiles(
                getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro", "proguard-rules-debug.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    composeOptions {
        val composeCompiler: String by rootProject.extra
        kotlinCompilerExtensionVersion = composeCompiler
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.percentlayout:percentlayout:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    // Wear OS / Tiles
    implementation("com.google.android.horologist:horologist-compose-tools:0.5.16")
    implementation("com.google.android.horologist:horologist-tiles:0.5.16")
    implementation("androidx.wear:wear:1.3.0")
    implementation("androidx.wear.tiles:tiles:1.2.0")
    implementation("androidx.wear.tiles:tiles-material:1.2.0")
    val composeVersion: String by rootProject.extra
    val wearComposeVersion: String by rootProject.extra
    implementation("androidx.compose.ui:ui:$composeVersion")
    implementation("androidx.wear.compose:compose-material:$wearComposeVersion")
    implementation("androidx.wear.compose:compose-foundation:$wearComposeVersion")
    implementation("androidx.wear.compose:compose-navigation:$wearComposeVersion")
    implementation("androidx.compose.material3:material3:1.1.2")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.wear:wear-remote-interactions:1.0.0")
    implementation("androidx.compose.animation:animation-graphics:1.5.4")
    implementation(project(":shared"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.5.4")
    debugImplementation("androidx.compose.ui:ui-test-manifest:$composeVersion")

    debugImplementation("androidx.compose.ui:ui-tooling-preview:$composeVersion")
    debugImplementation("androidx.wear:wear-tooling-preview:1.0.0")
    debugImplementation("androidx.wear.tiles:tiles-renderer:1.2.0")
    debugImplementation("androidx.compose.ui:ui-tooling:1.5.4")

    // DB
    val roomVersion: String by rootProject.extra
    implementation("androidx.room:room-ktx:$roomVersion")
    //noinspection KaptUsageInsteadOfKsp
    kapt("androidx.room:room-compiler:$roomVersion")

    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-common-java8:2.6.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    constraints {
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.0") {
            because("kotlin-stdlib-jdk8 is now a part of kotlin-stdlib")
        }
    }
}
