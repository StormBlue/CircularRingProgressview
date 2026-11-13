plugins { id("com.android.library"); `maven-publish` }
android {
    namespace = "com.bluestrom.gao.circularringpercentageview"
    compileSdk = 37
    defaultConfig {
        minSdk = 23
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    publishing { singleVariant("release") { withSourcesJar() } }
}
dependencies {
    androidTestImplementation("androidx.test:runner:1.7.0")
}
afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                groupId = "io.github.stormblue"
                artifactId = "circular-ring-progressview"
                version = "2.0.0"
                pom {
                    name = "Circular Ring Progress View"
                    description = "A lightweight, batched tick ring progress view for Android"
                    url = "https://github.com/StormBlue/CircularRingProgressview"
                    licenses { license { name = "Apache License 2.0"; url = "https://www.apache.org/licenses/LICENSE-2.0" } }
                    developers { developer { id = "StormBlue"; name = "StormBlue" } }
                    scm { url = "https://github.com/StormBlue/CircularRingProgressview" }
                }
            }
        }
    }
}
