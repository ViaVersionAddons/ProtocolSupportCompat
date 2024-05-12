plugins {
    base
    `java-library`
}

group = "com.viaversion"
version = "1.0.0"

repositories {
    maven("https://repo.viaversion.com")
    maven("https://repo.papermc.io/repository/maven-public/")
    mavenCentral()
}

dependencies {
    compileOnly(project(":pslistener")) // This should not be included in the final jar
    compileOnly("com.viaversion:viaversion-bukkit:4.10.2")
    compileOnly("com.viaversion:viaversion-api:4.10.2")
    compileOnly("com.destroystokyo.paper:paper-api:1.16.5-R0.1-SNAPSHOT")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(8)
    }
}
