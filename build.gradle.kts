plugins {
    alias(libs.plugins.jvm) apply false
    alias(libs.plugins.multiplatform) apply false
    alias(libs.plugins.maven) apply false
    alias(libs.plugins.kover)
}

allprojects {
    group = "com.extopy"
    version = "0.1.5"
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
