plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.plugin.parcelize")
    id("com.vanniktech.maven.publish")
}

android {
    namespace = "com.monettheme.api"
    compileSdk = 37

    defaultConfig {
        minSdk = 28
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            consumerProguardFiles("consumer-rules.pro")
        }
    }

    buildFeatures {
        aidl = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(libs.androidx.core.ktx)
}
