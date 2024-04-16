plugins {
    kotlin("jvm") version "1.9.23"
}

group = "com.rover12421"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))

    implementation(libs.bundles.asm)
    implementation(libs.bundles.antlr)
    implementation(libs.bundles.jackson)
    implementation(libs.slf4j.api)
    implementation(libs.maven.impl)
    implementation(libs.guava)
    implementation(libs.commons.codec)

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

val javaVersion = 21
java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(javaVersion))
}
kotlin {
    jvmToolchain(javaVersion)
}
