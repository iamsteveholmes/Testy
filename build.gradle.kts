buildscript {
    val sql_delight_version: String = "1.5.0"
    val realm_version: String = "0.4.1"
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.21")
        classpath("com.android.tools.build:gradle:7.1.0-alpha07")
        classpath("com.squareup.sqldelight:gradle-plugin:$sql_delight_version")
        classpath("io.realm.kotlin:gradle-plugin:$realm_version")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}