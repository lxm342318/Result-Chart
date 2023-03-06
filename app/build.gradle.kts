import com.android.build.gradle.internal.api.ApkVariantOutputImpl
import java.text.SimpleDateFormat
import java.util.TimeZone
import java.util.Date

plugins {
    id ("com.android.application")
    id ("kotlin-android")
    id ("kotlin-kapt")
}

fun buildTime(): String{
    val df = SimpleDateFormat("yyyy_MM_dd_HH_mm")
    df.timeZone = TimeZone.getDefault()
    return df.format(Date())
}

android {
    compileSdk = ProjectProperties.compileSdk
    buildToolsVersion = ProjectProperties.buildVersion

    defaultConfig {
        applicationId = ProjectProperties.applicationId
        minSdk  = ProjectProperties.minSdk
        targetSdk = ProjectProperties.targetSdk
        versionCode =  ProjectProperties.versionCode
        versionName =  ProjectProperties.versionName
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            isMinifyEnabled = true // 混淆
            isZipAlignEnabled = false // Zipalign优化
            isShrinkResources =  false  // 移除无用的resource文件
            buildConfigField("boolean", "ISDEBUG", "false")
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
        getByName("debug") {
            isMinifyEnabled = false
            buildConfigField("boolean", "ISDEBUG", "true")
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }

    buildFeatures{
        viewBinding = true
    }

    compileOptions{
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
// Configure Kotlin compiler target Java 1.8 when compile Kotlin to bytecode
    kotlinOptions {
        jvmTarget = "1.8"
    }
    kapt.correctErrorTypes = true

    productFlavors {
        //测试环境
        create("ymtest"){
            manifestPlaceholders["BUGLY_APP_CHANNEL"] = "ymtest"
            flavorDimensions("ymtest")
            buildConfigField("String", "API_HOST", "\"http://124.71.110.82:8066\"")
            buildConfigField("String", "PUSH_HOST", "\"http://47.115.127.76:3000\"")
        }
        //正式环境
        create("ym"){
            manifestPlaceholders["BUGLY_APP_CHANNEL"] = "ym"
            flavorDimensions("ym")
            buildConfigField("String", "API_HOST", "\"http://124.71.110.82:8066\"")
            buildConfigField("String", "PUSH_HOST", "\"http://47.115.127.76:3000\"")
        }
    }

    defaultConfig {
        multiDexEnabled =  true
        //armeabi，armeabi-v7a，x86，mips，arm64-v8a，mips64，x86_64
        ndk {  abiFilters  }// 其他忽略
    }
}

android.applicationVariants.all {
    outputs.all {
        if (this is ApkVariantOutputImpl)
            this.outputFileName = "ym@_$flavorName@_v.$versionName@_${buildTime()}.apk"
    }

}

configurations.all {
    resolutionStrategy.eachDependency {
        if(requested.group == "com.android.support") {
            if (!requested.name.startsWith("multidex"))
                useVersion("30.0.0")
        }
    }
}

dependencies {

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(Dependencies.appcompat)
    implementation(Dependencies.material)
    implementation(Dependencies.coreKtx)
    implementation(Dependencies.activity)
    implementation(Dependencies.fragment)
    implementation(Dependencies.activityKtx)
    implementation(Dependencies.fragmentKtx)
    implementation(Dependencies.lifecycleRuntimeKtx)
    implementation(Dependencies.lifecycleLiveDataKtx)
    implementation(Dependencies.lifecycleLiveDataCoreKtx)
    implementation(Dependencies.lifecycleViewModelKtx)
    implementation(Dependencies.constraintLayout)
    implementation(Dependencies.appcompat)
    implementation(Dependencies.material)
    implementation(Dependencies.constraintLayout)
    implementation(Dependencies.androidxCore)
    implementation(Dependencies.androidxAnnotation)
    //test
    testImplementation(Dependencies.junit)
    androidTestImplementation(Dependencies.junitExt)
    androidTestImplementation(Dependencies.espresso)
    //BRVAH
    implementation(Dependencies.adapterHelper)
    // Kotlin
    implementation(Dependencies.kotlin)
    //retrofit
    implementation(Dependencies.okHttp)
    implementation(Dependencies.retrofit)
    implementation(Dependencies.converter)
    implementation(Dependencies.logging)
    // 协程核心库
    implementation(Dependencies.coroutinesCore)
    // 协程Android支持库
    implementation(Dependencies.coroutines)
    // To use Kotlin annotation processing tool (kapt)
    kapt(Dependencies.room)
    // optional - Kotlin Extensions and Coroutines support for Room
    implementation(Dependencies.roomKtx)
    // optional - Test helpers
    testImplementation(Dependencies.roomTest)
    //logger
    implementation(Dependencies.logger)
    //gson
    implementation(Dependencies.gSon)

}