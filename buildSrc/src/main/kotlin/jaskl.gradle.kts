plugins {
    id("java")
    id("checkstyle")
    id("maven-publish")
    id("java-test-fixtures")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

checkstyle {
    configDirectory.set(File("../checkstyle"))
    toolVersion = "9.3"
}

group = "com.github.almighty-satan"
version = "1.1.1"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.jetbrains:annotations:24.0.1")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testFixturesImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testImplementation(testFixtures(project(":core")))
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            pom {
                name.set("JASKL")
                description.set("Just Another Simple Konfig Library")
                url.set("https://github.com/Almighty-Satan/JASKL")
            }
            artifactId = "jaskl-${project.name}"
        }
        repositories {
            maven {
                setUrl("https://repo.varoplugin.de/repository/maven-releases/")
                credentials {
                    username = project.findProperty("repouser") as? String
                    password = project.findProperty("repopassword") as? String
                }
            }
        }
    }
}
