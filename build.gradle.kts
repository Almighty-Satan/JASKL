plugins {
    id("java")
}


group = "com.github.almightysatan"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.typesafe:config:1.4.2")
    implementation("com.moandjiezana.toml:toml4j:0.7.2")
    implementation("com.google.code.gson:gson:2.10.1")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
    compileOnly("org.jetbrains:annotations:24.0.1")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}