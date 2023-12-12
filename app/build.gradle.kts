plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    // KSP
    id("com.google.devtools.ksp")

    // Kapt
    id("kotlin-kapt")


}

android {
    namespace = "com.propertio.developer"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.propertio.developer"
        minSdk = 24
        targetSdk = 33
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }

//    sourceSets {
//        // Adds exported schema location as test app assets.
//        getByName("androidTest").assets.srcDir("$projectDir/schemas")
//    }

}

//ksp {
//    arg(RoomSchemaArgProvider(File(projectDir, "schemas")))
//}

dependencies {
    // API
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // ViewModel and LiveData
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")

    //TODO: Remove Glide Kapt and SVG
    // Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")
    kapt("com.github.bumptech.glide:compiler:4.16.0")

    // SVG
//    implementation("com.caverock:androidsvg-aar:1.4")
//    implementation("com.caverock:androidsvg:1.4")

    // Coil
    implementation("io.coil-kt:coil:2.5.0")
    implementation("io.coil-kt:coil-svg:2.5.0")



    // Room
    implementation("androidx.room:room-ktx:2.6.0")
    implementation("androidx.room:room-runtime:2.6.0")
    ksp("androidx.room:room-compiler:2.6.0")
    testImplementation("androidx.room:room-testing:2.6.1")


    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1")

    val nav_version = "2.7.5"
    implementation("androidx.navigation:navigation-fragment-ktx:$nav_version")
    implementation("androidx.navigation:navigation-ui-ktx:$nav_version")
    implementation("androidx.navigation:navigation-dynamic-features-fragment:$nav_version")
    androidTestImplementation("androidx.navigation:navigation-testing:$nav_version")

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}

//class RoomSchemaArgProvider(
//    @get:InputDirectory
//    @get:PathSensitive(PathSensitivity.RELATIVE)
//    val schemaDir: File
//) : CommandLineArgumentProvider {
//
//    override fun asArguments(): Iterable<String> {
//        return listOf("room.schemaLocation=${schemaDir.path}")
//    }
//}