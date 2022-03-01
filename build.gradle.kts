plugins {
    java
    application
    kotlin("jvm") version "1.6.10"
    id("org.openjfx.javafxplugin") version "0.0.10"
}

group = "com.ruppyrup"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

val junitVersion = "5.8.2"

application {
    mainClass.set("com.ruppyrup.bigfun.ServerApplication")
}

javafx {
    modules("javafx.controls", "javafx.fxml", "javafx.web")
}

dependencies {
    implementation("com.jfoenix:jfoenix:9.0.10")

//    testImplementation group:"io.datafx", name: "flow", version: "8.0.7"

    testImplementation("org.testfx:openjfx-monocle:jdk-12.0.1+2")
    testImplementation("org.testfx:testfx-core:4.0.16-alpha")
    testImplementation("org.testfx:testfx-junit5:4.0.16-alpha")
    testImplementation("org.junit.jupiter:junit-jupiter-api:${junitVersion}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${junitVersion}")
}

//test.systemProperty("headless", "true")

tasks {
    test {
        useJUnitPlatform()
    }

    val fatJar = register<Jar>("fatJar") {
        dependsOn.addAll(listOf("compileJava", "compileKotlin", "processResources")) // We need this for Gradle optimization to work
        archiveClassifier.set("standalone") // Naming the jar
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
//        manifest { attributes(mapOf("Main-Class" to application.mainClass)) } // Provided we set it up in the application plugin configuration
        manifest {
            attributes["Main-Class"] = "com.ruppyrup.bigfun.Launcher"
        }
        val sourcesMain = sourceSets.main.get()
        val contents = configurations.runtimeClasspath.get()
                .map { if (it.isDirectory) it else zipTree(it) + sourcesMain.output }
        from(contents)
        exclude("**/module-info.class")
    }
    build {
        dependsOn(fatJar) // Trigger fat jar creation during build
    }

}

tasks.withType<Test>().configureEach {
    jvmArgs = listOf("-Dheadless=true", "-Dtestfx.robot=glass", "-Dtestfx.headless=true",
            "-Dprism.order=sw", "-Dprism.text=t2k")
}

