plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.monettheme.service"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.monettheme.service"
        minSdk = 28
        targetSdk = 37
        versionCode = 1
        versionName = "1.0.0"
    }

    // ===== 签名配置 =====
    signingConfigs {
        create("release") {
            storeFile = file(System.getenv("KEYSTORE_PATH") ?: "monet-release.keystore")
            storePassword = System.getenv("KEYSTORE_PASSWORD") ?: "monet123"
            keyAlias = System.getenv("KEY_ALIAS") ?: "monet"
            keyPassword = System.getenv("KEY_PASSWORD") ?: "monet123"
        }
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        aidl = true
        compose = true
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
    implementation(project(":monet-api"))

    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    debugImplementation("androidx.compose.ui:ui-tooling")

    implementation(libs.androidx.compose.material3)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.core.ktx)
    implementation("androidx.palette:palette:1.0.0")
    implementation("com.google.android.material:material-color-utilities:1.0.0")
}
