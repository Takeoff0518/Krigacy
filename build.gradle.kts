plugins {
    kotlin("jvm") version "1.9.22"

    id("com.google.protobuf") version "0.9.4"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

allprojects {
    group = "top.mrxiaom"
    version = "1.0.0"
}

repositories {
    mavenCentral()
}

dependencies {
    protobuf(files("kritor/protos"))

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
    implementation("io.grpc:grpc-stub:1.62.2")
    implementation("io.grpc:grpc-protobuf:1.62.2")
    implementation("com.google.protobuf:protobuf-java-util:3.25.1")
    implementation("com.google.protobuf:protobuf-kotlin:3.25.3")
    implementation("io.grpc:grpc-kotlin-stub:1.4.1")
    implementation("io.grpc:grpc-netty:1.62.2")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.jetbrains:annotations:24.0.1")
    implementation("org.slf4j:slf4j-api:2.0.5")

    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.25.3"
    }
    plugins {
        create("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.62.2"
        }
        create("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:1.4.1:jdk8@jar"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                create("grpc")
                create("grpckt")
            }
            it.builtins {
                create("kotlin")
            }
        }
    }
}

kotlin {
    jvmToolchain(8)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
    kotlinOptions {
        freeCompilerArgs = listOf("-opt-in=kotlin.RequiresOptIn")
    }
}
