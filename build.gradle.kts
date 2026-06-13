pluginManagement {
    repositories {
        google()            // <-- Explictly tells Gradle to look in Google's warehouse for the Android plugin
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()            // <-- Tells your app modules where to download UI packages like Jetpack Compose/Glance
        mavenCentral()
    }
}

rootProject.name = "quick-thoughts"
include(":app")