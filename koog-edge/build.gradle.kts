plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.multiplatform)
    id("maven-publish")
}

group = "io.github.lemcoder"
version = "0.0.2"

kotlin {
    jvmToolchain(17)

    androidTarget {
        publishLibraryVariants("release", "debug")
    }

    iosArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.cactus)
            implementation(libs.koog.agents.core)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }

        androidMain.dependencies {
            implementation(libs.leap.sdk)
        }
    }
}

android {
    namespace = "io.github.lemcoder.koog"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        consumerProguardFiles("consumer-rules.pro")
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
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
}

publishing {
    repositories {
        mavenLocal()
        maven {
            name = "localRepo"
            url = rootDir.toURI()
        }
    }
}