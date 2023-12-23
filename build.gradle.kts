import org.gradle.internal.os.OperatingSystem

plugins {
    id("java-library")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

buildscript {
    group = "atomix.soft"
    version = "1.0-SNAPSHOT"

    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
        maven { setUrl("https://oss.sonatype.org/content/repositories/snapshots/") }
        google()
    }

    dependencies {

    }
}

val lwjglVersion = "3.3.3"
val jomlVersion = "1.10.5"

var lwjglNatives = ""
when(OperatingSystem.current()) {
    OperatingSystem.LINUX -> {
        val osArch = System.getProperty("os.arch")
        lwjglNatives = "natives-linux"
        if (osArch.startsWith("arm") || osArch.startsWith("aarch64")) {
            if(osArch.contains("64")) {
                lwjglNatives += "-arm64"
            } else if(osArch.contains("armv8")) {
                lwjglNatives += "-arm32"
            }
        } else if  (osArch.startsWith("ppc")) {
            lwjglNatives += "-ppc64le"
        } else if  (osArch.startsWith("riscv")) {
            lwjglNatives += "-riscv"
        }
    }

    OperatingSystem.WINDOWS -> {
        lwjglNatives = "natives-windows"
    }
    else -> print("End of Natives Check")
}


allprojects {
    plugins.apply("eclipse")

    repositories {
        mavenLocal()
        mavenCentral()
        google()
        gradlePluginPortal()
        maven { setUrl("https://oss.sonatype.org/content/repositories/snapshots/") }
        maven { setUrl("https://oss.sonatype.org/content/repositories/releases/") }
        maven { setUrl("https://jitpack.io") }
    }
}

project(":core") {
    plugins.apply("java-library")

    dependencies {
        api(platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))

        api("com.amihaiemil.web:eo-yaml:7.0.11")
        api("org.lwjgl:lwjgl")
        api("org.lwjgl:lwjgl-assimp")
        api("org.lwjgl:lwjgl-bgfx")
        api("org.lwjgl:lwjgl-fmod")
        api("org.lwjgl:lwjgl-freetype")
        api("org.lwjgl:lwjgl-glfw")
        api("org.lwjgl:lwjgl-jemalloc")
        api("org.lwjgl:lwjgl-nanovg")
        api("org.lwjgl:lwjgl-nuklear")
        api("org.lwjgl:lwjgl-openal")
        api("org.lwjgl:lwjgl-opengl")
        api("org.lwjgl:lwjgl-par")
        api("org.lwjgl:lwjgl-stb")
        api("org.lwjgl:lwjgl-vulkan")
        api("org.joml:joml:${jomlVersion}")

        runtimeOnly("org.lwjgl", "lwjgl", classifier = lwjglNatives)
        runtimeOnly("org.lwjgl", "lwjgl-assimp", classifier = lwjglNatives)
        runtimeOnly("org.lwjgl", "lwjgl-bgfx", classifier = lwjglNatives)
        runtimeOnly("org.lwjgl", "lwjgl-freetype", classifier = lwjglNatives)
        runtimeOnly("org.lwjgl", "lwjgl-glfw", classifier = lwjglNatives)
        runtimeOnly("org.lwjgl", "lwjgl-jemalloc", classifier = lwjglNatives)
        runtimeOnly("org.lwjgl", "lwjgl-nanovg", classifier = lwjglNatives)
        runtimeOnly("org.lwjgl", "lwjgl-nuklear", classifier = lwjglNatives)
        runtimeOnly("org.lwjgl", "lwjgl-openal", classifier = lwjglNatives)
        runtimeOnly("org.lwjgl", "lwjgl-opengl", classifier = lwjglNatives)
        runtimeOnly("org.lwjgl", "lwjgl-par", classifier = lwjglNatives)
        runtimeOnly("org.lwjgl", "lwjgl-stb", classifier = lwjglNatives)
    }
}

project(":client") {
    plugins.apply("java-library")

    dependencies {
        implementation(project(":core"))

        runtimeOnly("org.lwjgl", "lwjgl", classifier = lwjglNatives)
        runtimeOnly("org.lwjgl", "lwjgl-assimp", classifier = lwjglNatives)
        runtimeOnly("org.lwjgl", "lwjgl-bgfx", classifier = lwjglNatives)
        runtimeOnly("org.lwjgl", "lwjgl-freetype", classifier = lwjglNatives)
        runtimeOnly("org.lwjgl", "lwjgl-glfw", classifier = lwjglNatives)
        runtimeOnly("org.lwjgl", "lwjgl-jemalloc", classifier = lwjglNatives)
        runtimeOnly("org.lwjgl", "lwjgl-nanovg", classifier = lwjglNatives)
        runtimeOnly("org.lwjgl", "lwjgl-nuklear", classifier = lwjglNatives)
        runtimeOnly("org.lwjgl", "lwjgl-openal", classifier = lwjglNatives)
        runtimeOnly("org.lwjgl", "lwjgl-opengl", classifier = lwjglNatives)
        runtimeOnly("org.lwjgl", "lwjgl-par", classifier = lwjglNatives)
        runtimeOnly("org.lwjgl", "lwjgl-stb", classifier = lwjglNatives)
    }
}

project(":server") {
    plugins.apply("java-library")

    dependencies {
        implementation(project(":core"))
    }
}