plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.tracker"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.tracker"
        minSdk = 27
        targetSdk = 34
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation ("com.yandex.android:maps.mobile:4.1.0-full") // Используйте актуальную версию
    implementation("androidx.appcompat:appcompat:1.6.1") //1.6.1
    implementation("com.google.android.material:material:1.11.0") //1.11.0
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation ("com.squareup.okhttp3:okhttp:4.9.1")
    implementation ("androidx.work:work-runtime:2.10.0")
 //   implementation("com.google.android.ads:mediation-test-suite:3.0.0")
    //  androidTestImplementation("androidx.test.ext:junit:4.13.2") //1.1.5
   // androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1") //3.5.1
   // implementation ("com.google.android.gms:play-services-location:21.2.0")
    testImplementation("junit:junit:4.13.2")

}