plugins {
    id("com.android.library")
}

android {
    namespace = "com.monettheme.api"
    compileSdk = 36

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

    // AGP 9.x 内置 Kotlin 编译器配置
    kotlin {
        jvmToolchain(17)
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.16.0")
}
