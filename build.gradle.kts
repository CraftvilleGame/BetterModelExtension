plugins {
    kotlin("jvm") version "2.2.10"
    id("com.typewritermc.module-plugin") version "2.1.0"
}

group = "nl.craftville.extension"
version = "1.0"

repositories {
    mavenCentral()
    maven("https://maven.typewritermc.com/beta")
}

configurations.matching { it.isCanBeResolved }.configureEach {
    attributes {
        attribute(
            TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE,
            25
        )
    }
    resolutionStrategy.eachDependency {
        if (requested.group == "io.ktor") {
            useVersion("3.4.2")
            because("processor 2.1.0 was compiled against ktor 3.x")
        }
    }
}

dependencies {
    compileOnly("com.typewritermc:EntityExtension:0.9.0")
    compileOnly("io.github.toxicity188:bettermodel-bukkit-api:3.0.2")
}

typewriter {
    namespace = "craftville"

    extension {
        name = "BetterModel"
        shortDescription = "Simple usage of BetterModel for NPCs"
        description = "Adding the capability to use BetterModel for non player characters in your quests. I need atleast 100 characters blablablalbal"
        engineVersion = "0.9.0-beta-175"
        channel = com.typewritermc.moduleplugin.ReleaseChannel.BETA

        paper {
            dependency("BetterModel")
        }

        dependencies {
            dependency("typewritermc", "Entity")
        }
    }
}

tasks.jar {
    archiveVersion.set("")
}

kotlin {
    jvmToolchain(25)
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_24)
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(24)
}
