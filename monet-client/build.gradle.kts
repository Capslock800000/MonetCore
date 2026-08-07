plugins {
    id("com.android.library")
}

android {
    namespace = "com.monettheme.client"
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
        compose = true
    }

    // AGP 9.x 内置 Kotlin 编译器配置
    kotlin {
        jvmToolchain(17)
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }
}

dependencies {
    implementation(project(":monet-api"))

    // Compose BOM
    val composeBom = platform("androidx.compose:compose-bom:2026.06.01")
    implementation(composeBom)

    // Compose Core
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")

    // Material 3 Expressive
    implementation("androidx.compose.material3:material3-expressive")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.9.0")
}
