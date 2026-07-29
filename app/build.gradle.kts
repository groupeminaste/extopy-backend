plugins {
    alias(libs.plugins.jvm)
    alias(libs.plugins.serialization)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kover)
    application
}

kotlin {
    jvmToolchain(21)
    compilerOptions {
        optIn.add("kotlin.uuid.ExperimentalUuidApi")
    }
}

application {
    mainClass.set("com.extopy.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

ktor {
    docker {
        jreVersion.set(JavaVersion.VERSION_21)
        localImageName.set("extopy-backend")
        findProperty("imageTag")?.let { imageTag.set(it.toString()) }

        externalRegistry.set(
            io.ktor.plugin.features.DockerImageRegistry.dockerHub(
                appName = provider { "extopy-backend" },
                username = provider { "nathanfallet" },
                password = providers.environmentVariable("DOCKER_HUB_PASSWORD")
            )
        )
    }
}

jib {
    from {
        platforms {
            platform {
                os = "linux"
                architecture = System.getenv("JIB_PLATFORM_ARCHITECTURE") ?: "amd64"
            }
        }
    }
}

dependencies {
    implementation(projects.api)
    implementation(projects.domain)
    implementation(projects.infrastructure)
    implementation(projects.presentation)

    implementation(libs.logback.core)
    implementation(libs.logback.classic)
    implementation(libs.ktor.server.netty)

    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit5)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testImplementation(libs.h2)
    testImplementation(libs.tests.jsoup)
}

tasks.test {
    useJUnitPlatform()
}

tasks.register("jvmTest") {
    dependsOn("test") // To run normal `jvm` module `test` task when running all `jvmTest` tasks.
}
