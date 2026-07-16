plugins {
    alias(libs.plugins.android.application)

}

android {
    namespace = "com.example.wearos"
    compileSdk {
        version = release(37) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.example.wearos"
        minSdk = 30
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.play.services.wearable)
    implementation(libs.androidx.constraintlayout)

    // Para Wear OS
    implementation("androidx.wear:wear:1.2.0")
    // Si usas Compose para Wear
    implementation("androidx.wear.compose:compose-material:1.3.0")

    // Navegación entre pantallas (single Activity + Fragments, patrón estándar)
    // Mismas versiones que usa el módulo :app para mantener consistencia.
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")
    implementation("androidx.fragment:fragment-ktx:1.8.1")

}