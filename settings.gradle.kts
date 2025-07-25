pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
//plugins {
   // id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
//}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
include(":app")
rootProject.name = "AppDiag"

