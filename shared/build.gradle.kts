import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

val ktor_version: String by project
val kotlin_version: String by project
val sql_delight_version: String by project
val realm_version: String by project
val mvi_version: String by project
val coroutines_version: String by project
val material_design_version: String by project
val junit_version: String by project
val turbine_version: String by project


plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("com.squareup.sqldelight")
    id("io.realm.kotlin")
}

val iosExportSuffix = getIosTargetName().toLowerCase()

fun getIosTargetName(): String {
    val sdkName = System.getenv("SDK_NAME") ?: "iphonesimulator"
    return "ios" + if (sdkName.startsWith("iphoneos")) "Arm64" else "X64"
}

kotlin {
    android()

    sourceSets.matching {
        it.name.endsWith("Test")
    }.configureEach {
        languageSettings.useExperimentalAnnotation("kotlin.time.ExperimentalTime")
    }

    val iosTarget: (String, KotlinNativeTarget.() -> Unit) -> KotlinNativeTarget =
        if (System.getenv("SDK_NAME")?.startsWith("iphoneos") == true)
            ::iosArm64
        else
            ::iosX64

    iosTarget("ios") {
        binaries {
            framework {
                baseName = "shared"
                export("com.arkivanov.mvikotlin:mvikotlin-${iosExportSuffix}:$mvi_version")
                export("com.arkivanov.mvikotlin:mvikotlin-main-${iosExportSuffix}:$mvi_version")
                export("com.arkivanov.mvikotlin:mvikotlin-extensions-coroutines-${iosExportSuffix}:$mvi_version")
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutines_version") {
                    version { strictly(coroutines_version) }
                }
                implementation("io.realm.kotlin:library:$realm_version")
//                implementation("com.squareup.sqldelight:runtime:$sql_delight_version")
                api("com.arkivanov.mvikotlin:mvikotlin:$mvi_version")
                api("com.arkivanov.mvikotlin:mvikotlin-main:$mvi_version")
                api("com.arkivanov.mvikotlin:mvikotlin-extensions-coroutines:$mvi_version")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("app.cash.turbine:turbine:$turbine_version")
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val androidMain by getting {
            dependencies {
                implementation("com.squareup.sqldelight:android-driver:$sql_delight_version")
            }
        }
        val androidTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("junit:junit:$junit_version")
            }
        }
        val iosMain by getting {
            dependencies {
                implementation("com.squareup.sqldelight:native-driver:$sql_delight_version")
            }
        }

        val iosTest by getting
    }
}

android {
    compileSdkVersion(30)
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdkVersion(26)
        targetSdkVersion(30)
    }
}

sqldelight {
    database("ChorgiDatabase") {
        packageName = "chorgi.db"
        schemaOutputDirectory = file("src/main/sqldelight/databases")
//        verifyMigrations = true
    }
}
