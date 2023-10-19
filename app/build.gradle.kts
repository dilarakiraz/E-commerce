plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id ("kotlin-parcelize")
    id ("androidx.navigation.safeargs.kotlin")
    id ("kotlin-kapt")
    id ("dagger.hilt.android.plugin")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.dilarakiraz.upschoolcapstoneproject"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.dilarakiraz.upschoolcapstoneproject"
        minSdk = 24
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures{
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-database-ktx:20.2.2")
    implementation("com.google.firebase:firebase-analytics-ktx:21.3.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    //Glide
    implementation ("com.github.bumptech.glide:glide:4.16.0")

    //circular image
    implementation ("de.hdodenhof:circleimageview:3.1.0")

    //Android Ktx
    implementation ("androidx.fragment:fragment-ktx:1.4.1")

    //Dagger hilt
    implementation ("com.google.dagger:hilt-android:2.48")
    kapt ("com.google.dagger:hilt-compiler:2.48")

    //navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.3")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.3")

    //retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    //chucker
    implementation("com.github.chuckerteam.chucker:library:4.0.0")

    //lottie
    implementation ("com.airbnb.android:lottie:6.1.0")

    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    implementation ("androidx.room:room-runtime:2.5.2")
    kapt ("androidx.room:room-compiler:2.5.2")
    implementation ("androidx.room:room-ktx:2.5.2")

    implementation ("com.intuit.ssp:ssp-android:1.1.0")
    implementation ("com.intuit.sdp:sdp-android:1.1.0")

    implementation ("com.github.zladnrms:RoundableLayout:1.1.3")

    //firebase
    implementation("com.google.firebase:firebase-auth-ktx:22.1.2")
    implementation(platform("com.google.firebase:firebase-bom:32.2.3"))
    implementation ("com.google.firebase:firebase-firestore-ktx")

    //circleImage
    implementation ("de.hdodenhof:circleimageview:3.1.0")

    //workManager
    implementation("androidx.work:work-runtime-ktx:2.8.1")
}