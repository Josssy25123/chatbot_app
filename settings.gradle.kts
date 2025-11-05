pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }

    plugins {
        //noinspection AndroidGradlePluginVersion
        id("com.android.application") version "8.6.0" apply false
        //noinspection NewerVersionAvailable
        id("org.jetbrains.kotlin.android") version "2.0.21" apply false
    }
}


dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "chatbot app"
include(":app")
