plugins {
    jaskl
}

dependencies {
    api(project(":core"))
    implementation("com.fasterxml.jackson.core:jackson-databind:2.20.0")
}