plugins {
    jaskl
}

dependencies {
    api(project(":jackson"))
    implementation("com.fasterxml.jackson.core:jackson-databind:2.16.0")
}
