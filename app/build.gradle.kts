plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)              // procesador de anotaciones de Room
}

android {
    namespace = "com.example.pico_botella"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.pico_botella"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    buildFeatures {
        viewBinding = true               // lo usan MainActivity y SplashActivity
        dataBinding = true               // para lo nuevo (convención del profesor)
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation("com.airbnb.android:lottie:6.6.7")
    // lifecycleScope + coroutines para SplashActivity
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
    // ViewModel + viewModelScope para la capa viewmodel (MVVM)
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")

    // Room (SQLite) — RA-1
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Navigation Component — RA-1
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // LiveData (convención del profesor)
    implementation(libs.androidx.lifecycle.livedata.ktx)

    // RecyclerView (listado de retos)
    implementation(libs.androidx.recyclerview)

    testImplementation(libs.junit)
    // Permite probar corutinas y viewModelScope en tests unitarios (Dispatchers.setMain, runTest)
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Retrofit para consumir la API de Pokémon
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Glide para cargar la imagen del Pokémon en el diálogo
    implementation("com.github.bumptech.glide:glide:4.15.1")
}