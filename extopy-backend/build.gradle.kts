plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("org.jetbrains.kotlinx.kover")
    id("com.google.devtools.ksp")
    id("io.ktor.plugin") version "2.3.7"
}

application {
    mainClass.set("me.nathanfallet.extopy.ApplicationKt")
}

kotlin {
    jvm {
        jvmToolchain(19)
        withJava()
        testRuns.named("test") {
            executionTask.configure {
                useJUnitPlatform()
            }
        }
    }

    applyDefaultHierarchyTemplate()

    val coroutinesVersion = "1.7.3"
    val ktorVersion = "2.3.6"
    val koinVersion = "3.5.0"
    val exposedVersion = "0.40.1"
    val logbackVersion = "0.9.30"
    val ktorxVersion = "1.7.4"

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
                implementation("io.ktor:ktor-server-core:$ktorVersion")
                implementation("io.ktor:ktor-server-netty:$ktorVersion")
                implementation("io.ktor:ktor-server-call-logging:$ktorVersion")
                implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
                implementation("io.ktor:ktor-server-status-pages:$ktorVersion")
                implementation("io.ktor:ktor-server-auth:$ktorVersion")
                implementation("io.ktor:ktor-server-auth-jwt:$ktorVersion")
                implementation("io.ktor:ktor-server-sessions:$ktorVersion")
                implementation("io.ktor:ktor-server-websockets:$ktorVersion")
                implementation("io.ktor:ktor-server-freemarker:$ktorVersion")
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("io.ktor:ktor-client-apache:$ktorVersion")
                implementation("io.ktor:ktor-client-jetty:$ktorVersion")
                implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")

                implementation("io.insert-koin:koin-core:$koinVersion")
                implementation("io.insert-koin:koin-ktor:$koinVersion")

                implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
                implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
                implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")

                implementation("ch.qos.logback:logback-core:$logbackVersion")
                implementation("ch.qos.logback:logback-classic:$logbackVersion")

                implementation("me.nathanfallet.i18n:i18n:1.0.7")
                implementation("me.nathanfallet.ktorx:ktor-i18n:$ktorxVersion")
                implementation("me.nathanfallet.ktorx:ktor-i18n-freemarker:$ktorxVersion")
                implementation("me.nathanfallet.ktorx:ktor-routers:$ktorxVersion")
                implementation("me.nathanfallet.ktorx:ktor-routers-locale:$ktorxVersion")
                implementation("me.nathanfallet.ktorx:ktor-sentry:$ktorxVersion")
                implementation("me.nathanfallet.cloudflare:cloudflare-api-client:4.0.10")

                implementation("com.mysql:mysql-connector-j:8.0.33")
                implementation("at.favre.lib:bcrypt:0.9.0")
                implementation("org.apache.commons:commons-email:1.5")
                implementation("io.sentry:sentry:6.34.0")

                api(project(":extopy-commons"))
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("io.ktor:ktor-server-test-host:$ktorVersion")
                implementation("io.mockative:mockative:2.0.1")
                implementation("io.mockk:mockk:1.13.8")
                implementation("org.jsoup:jsoup:1.16.2")
                implementation("com.h2database:h2:2.2.224")
            }
        }
    }
}
