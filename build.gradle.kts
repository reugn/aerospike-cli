import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") version "1.7.21"
    id("com.github.gmazzo.buildconfig") version "3.1.0"
    application
}

group = "io.github.reugn"

repositories {
    mavenCentral()
}

extra["jlineVersion"] = "3.22.0"
extra["slf4jVersion"] = "2.0.6"

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.5")
    implementation("com.aerospike:aerospike-jdbc:1.7.0")
    implementation("org.jline:jline-console:${project.extra["jlineVersion"]}")
    implementation("org.jline:jline-reader:${project.extra["jlineVersion"]}")
    implementation("org.jline:jline-terminal:${project.extra["jlineVersion"]}")
    runtimeOnly("org.jline:jline-terminal-jna:${project.extra["jlineVersion"]}")
    implementation("org.slf4j:slf4j-api:${project.extra["slf4jVersion"]}")
    implementation("org.slf4j:slf4j-nop:${project.extra["slf4jVersion"]}")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("io.github.reugn.aerospike.cli.MainKt")
}

tasks {
    val fatJar = register<Jar>("fatJar") {
        dependsOn.addAll(
            listOf(
                "compileJava",
                "compileKotlin",
                "processResources"
            )
        )
        archiveClassifier.set("standalone")
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        manifest { attributes(mapOf("Main-Class" to application.mainClass)) }
        exclude("META-INF/*.RSA", "META-INF/*.SF", "META-INF/*.DSA")
        val sourcesMain = sourceSets.main.get()
        val contents = configurations.runtimeClasspath.get()
            .map { if (it.isDirectory) it else zipTree(it) } +
                sourcesMain.output
        from(contents)
    }
    build {
        dependsOn(fatJar)
    }
}

buildConfig {
    packageName("io.github.reugn.aerospike.cli")
    buildConfigField("String", "APP_NAME", "\"${project.name}\"")
    buildConfigField("String", "APP_VERSION", provider { "\"${project.version}\"" })
}
