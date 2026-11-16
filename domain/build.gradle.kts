plugins {
    alias(libs.plugins.jvm)
    alias(libs.plugins.serialization)
    alias(libs.plugins.kover)
}

kotlin {
    jvmToolchain(21)
    compilerOptions {
        optIn.add("kotlin.uuid.ExperimentalUuidApi")
    }
}

dependencies {
    api(libs.kotlinx.serialization.json)
    api(libs.kotlinx.datetime)
    api(libs.kotlinx.coroutines.core)
    api(libs.koin.core)

    testImplementation(libs.kotlin.test.junit5)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testImplementation(libs.tests.mockk)
    testImplementation(libs.tests.coroutines)
}

tasks.test {
    useJUnitPlatform()
}

// CI runs `jvmTest` across the build; on a JVM module that is the `test` task.
tasks.register("jvmTest") {
    dependsOn("test")
}
