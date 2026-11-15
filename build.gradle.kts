plugins {
    alias(libs.plugins.jvm) apply false
    alias(libs.plugins.multiplatform) apply false
    alias(libs.plugins.maven) apply false
    alias(libs.plugins.kover)
}

// Development builds are snapshots published to Maven Local (`./gradlew publishToMavenLocal`).
// A release to Maven Central names its version explicitly: `./gradlew publishToMavenCentral -PextopyVersion=0.2.0`.
val extopyVersion = providers.gradleProperty("extopyVersion").orElse("0.2.0-SNAPSHOT").get()

allprojects {
    group = "com.extopy"
    version = extopyVersion

    // Maven Central counts every release against monthly limits: a snapshot never goes there.
    tasks.matching { it.name.startsWith("publish") && it.name.contains("MavenCentral") }.configureEach {
        doFirst {
            require(!extopyVersion.endsWith("-SNAPSHOT")) {
                "Publish snapshots to Maven Local only; pass -PextopyVersion=<x.y.z> to release on Maven Central."
            }
        }
    }
    project.ext.set("url", "https://github.com/groupeminaste/extopy-backend")
    project.ext.set("license.name", "GPL-3.0")
    project.ext.set("license.url", "https://opensource.org/licenses/GPL-3.0")
    project.ext.set("developer.id", "nathanfallet")
    project.ext.set("developer.name", "Nathan Fallet")
    project.ext.set("developer.email", "contact@nathanfallet.me")
    project.ext.set("developer.url", "https://www.nathanfallet.me")
    project.ext.set("scm.url", "https://github.com/groupeminaste/extopy-backend.git")

    repositories {
        mavenCentral()
    }
}

dependencies {
    kover(projects.api)
    kover(projects.client)
    kover(projects.domain)
    kover(projects.infrastructure)
    kover(projects.presentation)
    kover(projects.app)
}
