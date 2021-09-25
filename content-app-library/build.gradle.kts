import utils.version

plugins {
    id("com.android.library")
    id("maven-publish")
    id("com.diffplug.spotless") version "5.14.2"
    id("de.mannodermaus.android-junit5")
}

android {
    compileSdk = 30

    defaultConfig {
        minSdk = 21
        targetSdk = 30
        version = version()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments["runnerBuilder"] = "de.mannodermaus.junit5.AndroidJUnit5Builder"

        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), file("proguard-rules.pro"))
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    lint {
        enable(
            "MissingPermission",
            "SuspiciousImport",
            "UsesMinSdkAttributes",
            "Proguard"
        )
        isCheckTestSources = true
        isCheckAllWarnings = true
        isWarningsAsErrors = true
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.3.1")
    implementation("com.google.android.material:material:1.4.0")

    androidTestImplementation("androidx.test:runner:1.4.0")
    androidTestImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    androidTestImplementation("de.mannodermaus.junit5:android-test-core:1.3.0")
    androidTestImplementation("org.junit.jupiter:junit-jupiter-params:5.8.1")
    androidTestRuntimeOnly("de.mannodermaus.junit5:android-test-runner:1.3.0")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.8.1")

    testImplementation("androidx.test:core:1.4.0")
    testImplementation("org.mockito:mockito-core:3.12.4")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.register<Javadoc>("javadoc") {
    val variant = android.libraryVariants.first { it.name == "release" }
    description = "Generates Javadoc for ${variant.name}."
    source = fileTree(variant.sourceSets.first { it.name == "main" }.javaDirectories.first())
    classpath = files(variant.javaCompile.classpath.files) +
            files("${android.sdkDirectory}/platforms/${android.compileSdkVersion}/android.jar")
    (options as StandardJavadocDocletOptions).apply {
        source = "8" // workaround for https://bugs.openjdk.java.net/browse/JDK-8212233
        links(
            "https://docs.oracle.com/javase/7/docs/api/",
            "https://d.android.com/reference/"
        )
    }
}

val sourcesJar by tasks.creating(Jar::class) {
    archiveClassifier.set("sources")
    from(android.sourceSets.getByName("main").java.srcDirs)
}

val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
    from(tasks.named<Javadoc>("javadoc"))
}

tasks.register("printVersion") {
    doLast {
        println(version())
    }
}

fun libraryArtifactId(): String = "content-app-library"

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/EIDU/content-app-library")
            credentials {
                username = System.getenv("GITHUB_USER")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.eidu"
            artifactId = libraryArtifactId()
            version = version()
            artifact("$buildDir/outputs/aar/${libraryArtifactId()}-release.aar")
            artifact(sourcesJar)
            artifact(javadocJar)

            pom {
                name.value(libraryArtifactId())
                description.value("EIDU Content App Integration Library")
                url.value("https://github.com/EIDU/content-app-library")
                licenses {
                    license {
                        name.value("MIT License")
                        url.value("https://raw.githubusercontent.com/EIDU/content-app-library/main/LICENSE")
                    }
                }
                developers {
                    developer {
                        id.value("berlix")
                        name.value("Felix Engelhardt")
                        url.value("https://github.com/berlix/")
                    }
                }
                scm {
                    url.value("https://github.com/EIDU/content-app-library")
                    connection.value("scm:git:git://github.com/EIDU/content-app-library.git")
                    developerConnection.value("scm:git:ssh://git@github.com/EIDU/content-app-library.git")
                }
            }
        }
    }
}

spotless {
    java {
        target("src/*/java/**/*.java")
        importOrder()
        removeUnusedImports()
        googleJavaFormat().aosp()
    }
}
