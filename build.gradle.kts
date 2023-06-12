group = "io.runescript"
version = "1.0-SNAPSHOT"

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.8.20"
    id("org.jetbrains.intellij") version "1.14.1"
}

repositories {
    mavenCentral()
}

intellij {
    version.set("2023.1.2")
    type.set("IC")

    plugins.set(listOf("com.intellij.java"))
}

idea {
    module {
        generatedSourceDirs.add(file("src/main/gen"))
    }
}

sourceSets {
    main {
        java.srcDirs("src/main/gen")
    }
}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    patchPluginXml {
        sinceBuild.set("231")
        untilBuild.set("232.*")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}
