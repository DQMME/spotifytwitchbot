plugins {
    kotlin("jvm") version "1.5.30"

    id("com.github.johnrengelman.shadow") version "6.1.0"
}

group = "de.dqmme"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.5.21")

    implementation("com.github.twitch4j:twitch4j:1.5.1")

    implementation("org.slf4j:slf4j-api:1.7.32")
    implementation("org.slf4j:slf4j-simple:1.7.32")

    implementation("org.ini4j:ini4j:0.5.4")

    implementation("com.google.code.gson:gson:2.8.8")

    implementation("com.squareup.okhttp3:okhttp:4.9.1")

    implementation("io.ktor:ktor-server-core:1.6.3")
    implementation("io.ktor:ktor-server-netty:1.6.3")
    implementation("io.ktor:ktor-html-builder:1.6.3")
}

tasks {
    shadowJar {
        manifest {
            attributes(
                "Main-Class" to "de.dqmme.twitchbot.MainKt",
                "Implementation-Title" to "Gradle",
                "Implementation-Version" to archiveVersion
            )
        }
    }
}