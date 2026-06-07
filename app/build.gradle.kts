import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.rais.nexusbody"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.rais.nexusbody"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        // 2. UBAH DI SINI: Gunakan Properties() langsung tanpa tulisan java.util
        val properties = Properties()
        val localPropertiesFile = project.rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            properties.load(localPropertiesFile.inputStream())
        }

        buildConfigField("String", "SUPABASE_URL", "\"${properties.getProperty("SUPABASE_URL", "")}\"")
        buildConfigField("String", "SUPABASE_ANON_KEY", "\"${properties.getProperty("SUPABASE_ANON_KEY", "")}\"")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        // Ini WAJIB ditambahkan agar buildConfigField tidak merah dan bisa di-generate oleh sistem
        buildConfig = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    // hilt & navigasi
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.androidx.navigation.compose)

    // library nexusbody
    implementation("com.j256.ormlite:ormlite-android:6.1")
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")
    implementation("io.coil-kt:coil-compose:2.6.0")

    // testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Room Database
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")
    implementation("com.google.code.gson:gson:2.10.1")

    // Supabase Kotlin SDK
    implementation(platform("io.github.jan-tennert.supabase:bom:2.4.0"))
    implementation("io.github.jan-tennert.supabase:gotrue-kt") // Modul Autentikasi
    implementation("io.github.jan-tennert.supabase:postgrest-kt") // Modul Database
    implementation("io.ktor:ktor-client-android:2.3.11") // Engine jaringan
}

kotlin {
    jvmToolchain(17)
}

configurations.all {
    exclude(group = "com.intellij", module = "annotations")
    resolutionStrategy {
        // Paksa Gradle untuk selalu menggunakan Kotlin 2.0.0
        force("org.jetbrains.kotlin:kotlin-stdlib:2.0.0")
        force("org.jetbrains.kotlin:kotlin-stdlib-jdk7:2.0.0")
        force("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.0.0")

        // Paksa Gradle mengabaikan Room 3.0.0-alpha05 dan kembali ke 2.6.1
        force("androidx.room:room-runtime:2.6.1")
        force("androidx.room:room-ktx:2.6.1")
        force("androidx.room:room-compiler:2.6.1")
    }
}