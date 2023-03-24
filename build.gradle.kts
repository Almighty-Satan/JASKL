plugins {
    id("java")
}


group = "com.github.almightysatan"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
    compileOnly("org.jetbrains:annotations:24.0.1")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}