import org.gradle.internal.os.OperatingSystem

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

sourceSets {
    main {
        java {
            setSrcDirs(listOf("src/"))
        }

        resources {
            setSrcDirs(listOf("../resources"))
        }
    }
}

val appName = "Eclipse Origins"
val mainClassName = "git.eclipse.server.Main"
val assetsDir = file("../resources")

tasks.register<JavaExec>("run") {
    dependsOn("classes")
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath
    standardInput = System.`in`
    workingDir = assetsDir
    isIgnoreExitValue = true

    if(OperatingSystem.current().isMacOsX) {
        jvmArgs("-XstartOnFirstThread")
    }
}

tasks.register<JavaExec>("debug") {
    dependsOn("classes")
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath
    standardInput = System.`in`
    workingDir = assetsDir
    isIgnoreExitValue = true
    debug = true
}

tasks.register<Jar>("dist") {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    manifest {
        attributes(mapOf("Main-Class" to mainClassName))
    }

    dependsOn("classes")

    from(sourceSets["main"].output)
    from({ configurations.runtimeClasspath.get().filter { it.isDirectory }.map { it }  })
    configurations.runtimeClasspath.get().filter { !it.isDirectory }.forEach { from(zipTree(it)) }

    with(tasks.named<Jar>("jar").get())
}

tasks.named<Jar>("dist").configure {
    dependsOn("classes")
}

eclipse {
    project {
        name = "Eclipse Server"
    }
}