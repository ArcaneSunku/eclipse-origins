java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

sourceSets {
    main {
        java {
            srcDirs("src/")
        }
    }
}

val appName = "Eclipse Origins"
eclipse {
    project {
        name = "Eclipse Origins Core"
    }
}