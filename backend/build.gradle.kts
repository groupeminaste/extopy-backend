plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.serialization)
    alias(libs.plugins.kover)
    alias(libs.plugins.ksp)
    alias(libs.plugins.ktor)
    //alias(libs.plugins.maven)
}

application {
    mainClass.set("com.extopy.ApplicationKt")
}

ktor {
    docker {
        jreVersion.set(JavaVersion.VERSION_21)
        localImageName.set("extopy-backend")

        externalRegistry.set(
            io.ktor.plugin.features.DockerImageRegistry.dockerHub(
                appName = provider { "extopy-backend" },
                username = provider { "nathanfallet" },
                password = providers.environmentVariable("DOCKER_HUB_PASSWORD")
            )
        )
    }
}

/*
mavenPublishing {
    publishToMavenCentral(com.vanniktech.maven.publish.SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()
    pom {
        name.set("backend")
        description.set("Backend service of Extopy.")
        url.set(project.ext.get("url")?.toString())
        licenses {
            license {
                name.set(project.ext.get("license.name")?.toString())
                url.set(project.ext.get("license.url")?.toString())
            }
        }
        developers {
            developer {
                id.set(project.ext.get("developer.id")?.toString())
                name.set(project.ext.get("developer.name")?.toString())
                email.set(project.ext.get("developer.email")?.toString())
                url.set(project.ext.get("developer.url")?.toString())
            }
        }
        scm {
            url.set(project.ext.get("scm.url")?.toString())
        }
    }
}
*/

kotlin {
    jvmToolchain(21)
    jvm {
        withJava()
        testRuns.named("test") {
            executionTask.configure {
                useJUnitPlatform()
            }
        }
    }

    applyDefaultHierarchyTemplate()
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":commons"))
                implementation(libs.bundles.kaccelero.ktor)
                implementation(libs.bundles.ktor.server.api)
                implementation(libs.bundles.ktor.client.api)
                implementation(libs.koin.core)
                implementation(libs.koin.ktor)
                implementation(libs.logback.core)
                implementation(libs.logback.classic)
                implementation(libs.mysql)
                implementation(libs.sentry)
                implementation(libs.cloudflare.client)
                implementation(libs.apache.email)
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.bundles.ktor.server.tests)
                implementation(libs.tests.mockk)
                implementation(libs.tests.h2)
                implementation(libs.tests.jsoup)
            }
        }
    }
}
