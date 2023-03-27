plugins {
    jaskl
}

dependencies {
    implementation(project(":core"))
    implementation(project(":jackson"))
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-toml:2.14.2")
}
