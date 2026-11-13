plugins { id("com.android.application") }
android {
    namespace = "com.bluestrom.gao.circularringtest"
    compileSdk = 37
    defaultConfig {
        applicationId = "com.bluestrom.gao.circularringtest"
        minSdk = 23
        targetSdk = 37
        versionCode = 2
        versionName = "2.0.0"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildTypes { release { isMinifyEnabled = true; isShrinkResources = true; proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt")) } }
}
dependencies { implementation(project(":circularringpercentageview")) }
