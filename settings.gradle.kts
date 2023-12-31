pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            setUrl("https://jitpack.io")
        }
    }
}

rootProject.name = "PokemonApp"
include(":app")
include(":feature:profilepic")
include(":core:common")
include(":core:network")
include(":feature:pokemon")
include(":core:data")
include(":core:domain")
