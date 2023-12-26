buildscript {
    extra["composeVersion"] = "1.5.4"
    extra["composeCompiler"] = "1.5.7"
    extra["wearComposeVersion"] = "1.2.1"
}

plugins {
    id("com.android.application") version "8.2.0" apply false
    id("com.android.library") version "8.2.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.21" apply false
    id("com.google.devtools.ksp") version "1.9.21-1.0.16" apply false
}
