plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("maven-publish")
}

group = "io.github.lemcoder"
version = "0.0.1"

android {
    namespace = "io.github.lemcoder.koog"
    compileSdk = libs.versions.compileSdk.get().toInt()

    kotlin {
        jvmToolchain(17)
    }

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

dependencies {
    implementation(libs.leap.sdk)
    implementation(files("libs/cactus-beta-1.0.0.aar"))
    implementation(libs.koog.agents.core)
    testImplementation(libs.kotlin.test)
}

publishing {
    publications {
        create<MavenPublication>("release") {
            afterEvaluate {
                from(components["release"])
            }

            groupId = "io.github.lemcoder.koog"
            artifactId = "koog-edge"
            version = "0.0.1"
        }
    }

    repositories {
        mavenLocal()
        maven {
            name = "localRepo"
            url = rootDir.toURI()
        }
    }
}