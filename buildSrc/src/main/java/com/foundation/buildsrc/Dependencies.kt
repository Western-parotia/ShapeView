package com.foundation.buildsrc

object Dependencies {
    const val kotlinVersion = "1.4.32"

    object Kotlin {
        const val kotlin_stdlib = "org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion"
    }

    object AndroidX {
        const val core_ktx = "androidx.core:core-ktx:1.3.2"
        const val appcompat = "androidx.appcompat:appcompat:1.2.0"

    }

    object Material {
        const val material = "com.google.android.material:material:1.3.0"
    }

    object Company {
        val shape = "com.foundation.widget:Shape:${Publish.Version.versionName}"
    }
}