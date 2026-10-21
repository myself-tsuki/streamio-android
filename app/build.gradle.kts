plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.jetbrains.kotlin)
  alias(libs.plugins.jetbrains.compose.compiler)
}

android {
  namespace = "com.shashwat.streamio"
  compileSdk = 36

  defaultConfig {
    applicationId = "com.shashwat.streamio"
    minSdk = 21  // Updated to Android 5.0 for better compatibility with modern features
    targetSdk = 36
    versionCode = libs.versions.versionCode.get().toInt()
    versionName = libs.versions.versionName.get()
    multiDexEnabled = true
    
    // NDK configuration for 16 KB page size support
    ndk {
      // Use the latest NDK version for 16 KB page size support
      version = "26.1.10909125"
    }
    
    // Android 16 compatibility flags
    manifestPlaceholders["android:extractNativeLibs"] = "true"
    manifestPlaceholders["android:appComponentFactory"] = "androidx.core.app.CoreComponentFactory"
  }
  
  buildTypes {
    release {
      isMinifyEnabled = false
      signingConfig = signingConfigs.getByName("debug")
      proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
    }
  }
  
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
  
  kotlin {
    jvmToolchain(17)
  }
  
  buildFeatures {
    buildConfig = true
    viewBinding = true
    compose = true
  }
  
  lint {
    abortOnError = false
  }
  
  // Enhanced packaging configuration for 16 KB page size support
  packaging {
    jniLibs {
      useLegacyPackaging = false
    }
    
    // Ensure native libraries are properly aligned
    resources {
      excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
  }
  
  // NDK build configuration
  ndkVersion = "26.1.10909125"
}

dependencies {
  implementation(project(":library"))
  implementation(project(":extra-sources"))
  implementation(libs.androidx.constraintlayout)
  implementation(libs.androidx.appcompat)
  implementation(libs.androidx.multidex)
  implementation("com.google.android.material:material:1.11.0")
  implementation("com.google.code.gson:gson:2.10.1")

  // Jetpack Compose & Compose Cupertino (iOS UI)
  val composeBom = platform("androidx.compose:compose-bom:2024.09.00")
  implementation(composeBom)
  implementation("androidx.compose.ui:ui")
  implementation("androidx.compose.ui:ui-graphics")
  implementation("androidx.compose.foundation:foundation")
  implementation("androidx.compose.material3:material3")
  implementation("androidx.activity:activity-compose:1.9.2")
  implementation("io.github.alexzhirkevich:cupertino:0.1.0-alpha04")
  implementation("io.github.alexzhirkevich:cupertino-adaptive:0.1.0-alpha04")
  implementation("io.github.alexzhirkevich:cupertino-icons-extended:0.1.0-alpha04")

  testImplementation(libs.junit)
}
