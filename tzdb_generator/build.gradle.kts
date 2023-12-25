@file:Suppress("UnstableApiUsage")

import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "gay.depau.tzdb_generator"
    compileSdk = 34

    defaultConfig {
        minSdk = 26
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    testOptions {
        targetSdk = 33

        unitTests.all {
            it.apply {
                testLogging {
                    events(
                        TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED, TestLogEvent.STANDARD_OUT,
                        TestLogEvent.STANDARD_ERROR
                    )
                    exceptionFormat = TestExceptionFormat.FULL
                    showStandardStreams = true
                }
                outputs.upToDateWhen { false }
            }
        }
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.test:core-ktx:1.5.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.1.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    testImplementation(project(":wear"))

    testImplementation("org.robolectric:robolectric:4.9.2")

    // HTTP
    testImplementation("com.github.kittinunf.fuel:fuel:2.3.1")

    // DB
    testImplementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.5.0")
}
