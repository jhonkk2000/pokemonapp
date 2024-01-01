plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.jhonkk.pokemonapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.jhonkk.pokemonapp"
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "com.jhonkk.pokemonapp.CustomTestRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        manifestPlaceholders["MAPS_API_KEY"] = "${properties["MAPS_API_KEY"]}"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        viewBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.fragment)
    implementation(libs.android.material)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.hilt.android.testing)
    kaptTest(libs.hilt.compiler)
    kaptAndroidTest(libs.hilt.compiler)
    debugImplementation(libs.androidx.fragment.testing)
    testImplementation(libs.junit)
    debugImplementation(libs.hilt.android.testing)
    debugImplementation(libs.androidx.core.testing)
    androidTestImplementation(libs.androidx.test.junit)
    androidTestImplementation(libs.androidx.test.espresso)

    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    implementation(libs.androidx.work.runtime.ktx)

    implementation(project(":feature:profilepic"))
    implementation(project(":feature:pokemon"))
    implementation(project(":feature:mylocations"))
    implementation(project(":core:data"))
    implementation(project(":core:common"))

//    room()
//    hilt()
//    retrofit()
}