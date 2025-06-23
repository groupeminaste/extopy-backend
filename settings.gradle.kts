pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            // Plugins
            version("kotlin", "2.1.10")
            plugin("multiplatform", "org.jetbrains.kotlin.multiplatform").versionRef("kotlin")
            plugin("serialization", "org.jetbrains.kotlin.plugin.serialization").versionRef("kotlin")
            plugin("kover", "org.jetbrains.kotlinx.kover").version("0.8.3")
            plugin("ksp", "com.google.devtools.ksp").version("2.1.10-1.0.30")
            plugin("maven", "com.vanniktech.maven.publish").version("0.30.0")
            plugin("npm", "dev.petuska.npm.publish").version("3.4.1")
            plugin("kotlinjsfix", "me.nathanfallet.kotlinjsfix").version("1.0.1")
            plugin("zodable", "digital.guimauve.zodable").version("1.4.1")

            // Kaccelero
            version("kaccelero", "0.6.0")
            library("kaccelero-routers-client", "dev.kaccelero", "routers-client-ktor").versionRef("kaccelero")
            library("kaccelero-routers", "dev.kaccelero", "routers-ktor").versionRef("kaccelero")
            library("kaccelero-i18n-freemarker", "dev.kaccelero", "i18n-ktor-freemarker").versionRef("kaccelero")
            library("kaccelero-auth", "dev.kaccelero", "auth").versionRef("kaccelero")
            library("kaccelero-database-sessions", "dev.kaccelero", "database-ktor-sessions").versionRef("kaccelero")
            library("kaccelero-health-sentry", "dev.kaccelero", "health-sentry-ktor").versionRef("kaccelero")
            bundle(
                "kaccelero-ktor",
                listOf(
                    "kaccelero-routers-client",
                    "kaccelero-routers",
                    "kaccelero-i18n-freemarker",
                    "kaccelero-auth",
                    "kaccelero-database-sessions",
                    "kaccelero-health-sentry",
                )
            )

            // Tests
            library("tests-mockk", "io.mockk:mockk:1.13.12")
            library("tests-h2", "com.h2database:h2:2.3.232")
            library("tests-jsoup", "org.jsoup:jsoup:1.16.2")

            // Ktor
            version("ktor", "3.1.3")
            plugin("ktor", "io.ktor.plugin").versionRef("ktor")
            library("ktor-serialization-kotlinx-json", "io.ktor", "ktor-serialization-kotlinx-json").versionRef("ktor")
            library("ktor-server-core", "io.ktor", "ktor-server-core").versionRef("ktor")
            library("ktor-server-netty", "io.ktor", "ktor-server-netty").versionRef("ktor")
            library("ktor-server-call-logging", "io.ktor", "ktor-server-call-logging").versionRef("ktor")
            library("ktor-server-status-pages", "io.ktor", "ktor-server-status-pages").versionRef("ktor")
            library("ktor-server-content-negotiation", "io.ktor", "ktor-server-content-negotiation").versionRef("ktor")
            library("ktor-server-sessions", "io.ktor", "ktor-server-sessions").versionRef("ktor")
            library("ktor-server-auth-jwt", "io.ktor", "ktor-server-auth-jwt").versionRef("ktor")
            library("ktor-server-freemarker", "io.ktor", "ktor-server-freemarker").versionRef("ktor")
            library("ktor-server-websockets", "io.ktor", "ktor-server-websockets").versionRef("ktor")
            library("ktor-server-test-host", "io.ktor", "ktor-server-test-host").versionRef("ktor")
            library("ktor-client-core", "io.ktor", "ktor-client-core").versionRef("ktor")
            library("ktor-client-apache", "io.ktor", "ktor-client-apache").versionRef("ktor")
            library("ktor-client-jetty", "io.ktor", "ktor-client-jetty").versionRef("ktor")
            library("ktor-client-content-negotiation", "io.ktor", "ktor-client-content-negotiation").versionRef("ktor")
            library("ktor-client-mock", "io.ktor", "ktor-client-mock").versionRef("ktor")
            bundle(
                "ktor-server-api",
                listOf(
                    "ktor-server-core",
                    "ktor-server-netty",
                    "ktor-server-call-logging",
                    "ktor-server-status-pages",
                    "ktor-server-content-negotiation",
                    "ktor-server-sessions",
                    "ktor-server-auth-jwt",
                    "ktor-server-freemarker",
                    "ktor-server-websockets",
                    "ktor-serialization-kotlinx-json"
                )
            )
            bundle(
                "ktor-server-tests",
                listOf(
                    "ktor-server-test-host",
                    "ktor-client-core",
                    "ktor-client-content-negotiation",
                )
            )
            bundle(
                "ktor-client-api",
                listOf(
                    "ktor-client-core",
                    "ktor-client-apache",
                    "ktor-client-jetty",
                    "ktor-client-content-negotiation",
                    "ktor-serialization-kotlinx-json"
                )
            )
            bundle(
                "ktor-client-tests",
                listOf(
                    "ktor-client-mock",
                )
            )

            // Koin
            version("koin", "4.1.0")
            library("koin-core", "io.insert-koin", "koin-core").versionRef("koin")
            library("koin-ktor", "io.insert-koin", "koin-ktor").versionRef("koin")

            // Others
            library("logback-core", "ch.qos.logback:logback-core:0.9.30")
            library("logback-classic", "ch.qos.logback:logback-classic:0.9.30")
            library("mysql", "com.mysql:mysql-connector-j:8.0.33")
            library("sentry", "io.sentry:sentry:7.9.0")
            library("cloudflare-client", "me.nathanfallet.cloudflare:cloudflare-api-client:4.4.2")
            library("apache-email", "org.apache.commons:commons-email:1.6.0")
        }
    }
}

rootProject.name = "extopy-backend"
include(":commons")
include(":backend")
