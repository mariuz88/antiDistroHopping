import java.util.Properties


plugins {
    id("com.android.application")    //per creare apk finale
    id("org.jetbrains.kotlin.android") 
}

android {
    namespace = "com.mario.beta_antidh"
    compileSdk = 36 
    defaultConfig {
        applicationId = "com.mario.beta_antidh"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"



  //prendere chiave Groq da local prop.
        val localProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localProperties.load(localPropertiesFile.inputStream())
        }
        val groqApiKey = localProperties.getProperty("GROQ_API_KEY") ?: ""
        buildConfigField("String", "GROQ_API_KEY", "\"$groqApiKey\"")
    }

    buildTypes {
        release {
//per r8 obfuscation anti reverse
            isMinifyEnabled = true 
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"),"proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true    //consigliato alla lezione 8 al posto del findViewById 
        buildConfig =true
// builConf -> passaggio inf. sensibili da da gradle a koptlin 
    }
}    



dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)

        // per retroFit e netw.
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp.logging)
    
//per componenti nav
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)
        // viewmodel
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.livedata.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}
