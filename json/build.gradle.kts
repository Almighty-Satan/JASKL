plugins {
    jaskl
}

dependencies {
    implementation(project(":core"))
    implementation(project(":jackson"))
    implementation("com.fasterxml.jackson.core:jackson-databind:2.14.2")
}
