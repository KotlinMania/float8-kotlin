pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins { id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0" }

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "float8-kotlin"

if (gradle.parent == null) {
    listOf(
        "../half-kotlin",
    ).forEach { path ->
        if (file(path).exists()) {
            includeBuild(path)
        }
    }
}

