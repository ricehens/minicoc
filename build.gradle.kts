plugins {
    java
    application
}

group = "coc"
version = "0.1.0"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

repositories {
    mavenCentral()
}

application {
    mainClass.set("coc.Main")
}

sourceSets {
    main {
        java {
            srcDirs("src")
        }
    }
}
