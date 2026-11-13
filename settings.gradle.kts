pluginManagement {
    repositories { google(); mavenCentral(); gradlePluginPortal() }
    plugins { id("org.jetbrains.kotlin.android") version "2.4.21" }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories { google(); mavenCentral() }
}
rootProject.name = "CircularRingProgressview"
include(":app", ":circularringpercentageview")
