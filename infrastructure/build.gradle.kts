plugins {
    alias(libs.plugins.jvm)
    alias(libs.plugins.serialization)
    alias(libs.plugins.kover)
    alias(libs.plugins.exposed)
}

kotlin {
    jvmToolchain(21)
    compilerOptions {
        optIn.add("kotlin.uuid.ExperimentalUuidApi")
    }
}

dependencies {
    api(projects.domain)

    api(libs.bundles.exposed)
    api(libs.hikari)
    implementation(libs.flyway.core)
    implementation(libs.flyway.mysql)
    api(libs.mysql)
    api(libs.bcrypt)
    api(libs.apache.email)
    api(libs.sentry)

    api(libs.koin.ktor)
    api(libs.ktor.server.core)
    api(libs.ktor.server.auth.jwt)
    api(libs.ktor.server.sessions)

    testImplementation(libs.kotlin.test.junit5)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testImplementation(libs.tests.mockk)
    testImplementation(libs.tests.coroutines)
    testImplementation(libs.testcontainers.mysql)
    testImplementation(libs.exposed.migration.jdbc)
}

tasks.test {
    useJUnitPlatform()
}

// CI runs `jvmTest` across the build; on a JVM module that is the `test` task.
tasks.register("jvmTest") {
    dependsOn("test")
}

// Migrations are generated from the Exposed tables against a throwaway MySQL 8.4 container (Docker),
// after replaying the committed history; Flyway applies them when the server starts. Review the
// generated file before committing it: `./gradlew :infrastructure:generateMigrations`.
exposed {
    migrations {
        tablesPackage = "com.extopy.infrastructure.database.tables"
        testContainersImageName = "mysql:8.4"
        fileDirectory = layout.projectDirectory.dir("src/main/resources/db/migration")
    }
}
