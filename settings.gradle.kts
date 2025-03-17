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
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "A Toute"
include(":app")
include(":core")
include(":core:data")
include(":core:domain")
include(":core:presentation")
include(":home")
include(":chat")
include(":todoList")
include(":profile")
include(":notifications")
include(":authentication")
include(":party")
include(":chat:data")
include(":chat:domain")
include(":chat:presentation")
include(":home:data")
include(":home:domain")
include(":home:presentation")
include(":authentication:data")
include(":authentication:domain")
include(":authentication:presentation")
include(":notifications:data")
include(":notifications:domain")
include(":notifications:presentation")
include(":party:data")
include(":party:domain")
include(":party:presentation")
include(":profile:data")
include(":profile:domain")
include(":profile:presentation")
include(":todoList:data")
include(":todoList:domain")
include(":todoList:presentation")
include(":core:utils")
include(":core:network")
