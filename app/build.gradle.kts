plugins {
    id("com.android.application")
}

android {
    namespace = "com.rayolla.mylotto"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.rayolla.mylotto"
        minSdk = 31
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.6"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
//    implementation("jexcelapi:jxl:2.6")
//    implementation("net.sourceforge.jexcelapi:jxl:2.6.12")
//    implementation("org.apache.poi:poi:5.2.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}