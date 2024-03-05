plugins {
    kotlin("jvm").version("1.9.20").apply(false)
    kotlin("plugin.serialization").version("1.9.20").apply(false)
    id("convention.publication").apply(false)
    id("org.jetbrains.kotlinx.kover").version("0.7.4").apply(false)
    id("com.google.devtools.ksp").version("1.9.20-1.0.13").apply(false)
    id("dev.petuska.npm.publish").version("3.4.1").apply(false)
}

allprojects {
    group = "me.nathanfallet.extopy"
    version = "0.1.1"

    repositories {
        mavenCentral()
    }
}
