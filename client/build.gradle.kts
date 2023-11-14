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
            setSrcDirs(listOf("../assets"))
        }
    }
}

val appName = "Eclipse Origins"
val mainClassName = "git.eclipse.client.Main"
val assetsDir = file("../assets")

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

eclipse {
    project {
        name = "$appName-client"
    }
}