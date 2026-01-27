plugins {
    alias(libs.plugins.android.kmp.library)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.maven.publish)
    signing
}

group = "io.github.lemcoder"
version = "0.0.3"

kotlin {
    jvmToolchain(17)

    androidLibrary {
        namespace = "io.github.lemcoder.core"
        compileSdk = 36
        minSdk = 31

        withHostTestBuilder {}.configure {}
        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {}
    }

    iosArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.cactus)
            implementation(libs.koog.agents.core)
            implementation(libs.leap.sdk)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

publishing {
    repositories {
        mavenLocal()
    }
}

mavenPublishing {
    publishToMavenCentral()

    coordinates(
        groupId = group.toString(),
        artifactId = "koog-edge",
        version = version.toString()
    )

    pom {
        name.set("Koog Edge")
        description.set("Kotlin Multiplatform library for integrating Koog with local on device SLMs.")
        inceptionYear.set("2025")
        url.set("https://github.com/lemcoder/koog-edge")

        licenses {
            license {
                name.set("Apache-2.0 license")
                url.set("https://www.apache.org/licenses/LICENSE-2.0")
            }
        }

        developers {
            developer {
                id.set("lemcoder")
                name.set("Mikołaj Lemański")
                email.set("lemanski.dev@gmail.com")
            }
        }

        scm {
            url.set("https://github.com/lemcoder/koog-edge")
        }
    }
}

// Sign with default plugin
if (System.getenv("SIGNING_KEY") != null && System.getenv("SIGNING_KEY_PASSWORD") != null) {
    signing {
        useInMemoryPgpKeys(
            System.getenv("SIGNING_KEY"),
            System.getenv("SIGNING_KEY_PASSWORD")
        )
        sign(publishing.publications)

        // Temporary workaround, see https://github.com/gradle/gradle/issues/26091#issuecomment-1722947958
        tasks.withType<AbstractPublishToMaven>().configureEach {
            val signingTasks = tasks.withType<Sign>()
            mustRunAfter(signingTasks)
        }
    }
}
